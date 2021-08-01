package de.dlyt.yanndroid.sudoku;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.GridView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.HashMap;

import de.dlyt.yanndroid.samsung.ThemeColor;
import de.dlyt.yanndroid.samsung.drawer.OptionGroup;
import de.dlyt.yanndroid.samsung.layout.DrawerLayout;
import de.dlyt.yanndroid.sudoku.adapter.GamesAdapter;
import de.dlyt.yanndroid.sudoku.adapter.SudokuAdapter;
import de.dlyt.yanndroid.sudoku.utils.Game;
import de.dlyt.yanndroid.sudoku.utils.GameArrayList;
import de.dlyt.yanndroid.sudoku.utils.Timer;

public class MainActivity extends AppCompatActivity {

    public static boolean colorSettingChanged = false;
    public static boolean gridSettingChanged = false;

    private DrawerLayout drawerLayout;
    private OptionGroup optionGroup;
    private Menu menu;
    private Dialog mLoadingDialog;

    private Timer timer;
    private View hide_layout;
    private MaterialButton resume_button;

    private Context context;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;

    private GridView sudokuView;
    private SudokuAdapter sudokuAdapter;
    private GameArrayList games = new GameArrayList();
    private Game currentGame;
    private Integer[][] currentGrid;

    private RecyclerView games_recycler;
    private GamesAdapter gamesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeColor(this);
        setContentView(R.layout.activity_main);

        context = this;
        sharedPreferences = getSharedPreferences("Sudoku", Activity.MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawer_view);
        setSupportActionBar(drawerLayout.getToolbar());
        drawerLayout.setDrawerIconOnClickListener(v -> startActivity(new Intent().setClass(context, SettingsActivity.class)));
        menu = drawerLayout.getToolbar().getMenu();

        mLoadingDialog = new Dialog(context, R.style.LargeProgressDialog);
        mLoadingDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mLoadingDialog.setCancelable(false);
        mLoadingDialog.setContentView(getLayoutInflater().inflate(R.layout.dialog_full_loading, null));

        sudokuView = findViewById(R.id.sudokuView);
        sudokuView.setClipToOutline(true);

        optionGroup = findViewById(R.id.optionGroup);
        optionGroup.setOnOptionButtonClickListener((optionButton, i, i1) -> {
            drawerLayout.setDrawerOpen(false, true);
            switch (i) {
                case R.id.play_sudoku:
                    loadGame(sharedPreferences.getInt("lastGame", 0));
                    break;
                case R.id.solve_sudoku:
                    drawerLayout.setToolbarTitle(getString(R.string.app_name));
                    drawerLayout.setToolbarSubtitle(getString(R.string.solver));
                    menu.setGroupVisible(R.id.play_group, false);
                    stopTimer();
                    currentGame = null;
                    sudokuAdapter = null;
                    sudokuView.setAdapter(sudokuAdapter);
                    loadSolver();
                    break;
            }
        });

        checkForUpdate();

        hide_layout = findViewById(R.id.hide_layout);
        resume_button = findViewById(R.id.resume_button);
        resume_button.setOnClickListener(v -> startTimer());
        timer = new Timer();
        timer.setOnTimeChanged(time -> drawerLayout.setToolbarSubtitle(getString(R.string.elapsed_time, timer.getTimeString())));

        games = new Gson().fromJson(sharedPreferences.getString("Games", "{}"), GameArrayList.class);
        if (games.isEmpty()) {
            newSudoku(null);
        } else {
            loadGame(sharedPreferences.getInt("lastGame", 0));
        }

        games_recycler = findViewById(R.id.games_recycler);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        games_recycler.setLayoutManager(linearLayoutManager);
        gamesAdapter = new GamesAdapter(context, games);
        games_recycler.setAdapter(gamesAdapter);


    }

    public void onDeleteGame(Game delGame) {
        if (delGame == currentGame) {
            sudokuAdapter = null;
            sudokuView.setAdapter(sudokuAdapter);
            drawerLayout.setToolbarTitle(getString(R.string.app_name));
            drawerLayout.setToolbarSubtitle(null);
            menu.setGroupVisible(R.id.play_group, false);
            currentGame = null;
            stopTimer();
        }
        if (games.isEmpty()) {
            drawerLayout.setDrawerOpen(false, true);
            newSudoku(null);
        }

    }

    public void onGameFinished() {
        stopTimer();
        currentGame.setFinished(true);
        gamesAdapter.notifyDataSetChanged();
        menu.setGroupVisible(R.id.play_group, false);
        drawerLayout.setToolbarSubtitle(getString(R.string.time, new Timer().timeToString(currentGame.getTime())));
        new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.DialogStyle))
                .setTitle(R.string.sudoku_done)
                .setMessage(context.getString(R.string.time, timer.getTimeString()))
                .setPositiveButton(R.string.new_sudoku, (dialog, which) -> ((MainActivity) context).newSudoku(null))
                .setNegativeButton(R.string.dismiss, null)
                .show();
        sudokuAdapter = new SudokuAdapter(context, currentGame);
        sudokuView.setAdapter(sudokuAdapter);
    }

    public void onNameChange(Game reGame, String name) {
        if (reGame == currentGame) {
            drawerLayout.setToolbarTitle(name);
        }
    }

    private void stopTimer() {
        if (timer != null) timer.stop();
        if (currentGame != null)
            if (!currentGame.isFinished()) currentGame.setTime(timer.getTime());
    }

    private void startTimer() {
        if (timer != null && currentGame != null) if (!currentGame.isFinished()) {
            timer.start();
            hide_layout.setVisibility(View.GONE);
            MenuItem menuItem = menu.findItem(R.id.pause);
            if (menuItem != null) menuItem.setIcon(R.drawable.ic_samsung_pause);
        }
    }

    public void loadGame(int index) {
        if (!(index < games.size())) {
            loadGame(games.size() - 1);
            return;
        }
        optionGroup.setSelectedOptionButton(findViewById(R.id.play_sudoku));
        drawerLayout.setDrawerOpen(false, true);
        Game game = games.get(index);
        sharedPreferences.edit().putInt("lastGame", index).apply();

        drawerLayout.setToolbarTitle(game.getName());
        menu.setGroupVisible(R.id.play_group, !game.isFinished());
        menu.setGroupVisible(R.id.solve_group, false);

        hide_layout.setVisibility(View.GONE);
        MenuItem menuItem = menu.findItem(R.id.pause);
        if (menuItem != null) menuItem.setIcon(R.drawable.ic_samsung_pause);

        if (currentGame != game) {
            stopTimer();
            if (!game.isFinished()) {
                timer.setTime(game.getTime());
                timer.start();
            } else {
                drawerLayout.setToolbarSubtitle(getString(R.string.time, new Timer().timeToString(game.getTime())));
            }
            sudokuView.setNumColumns(game.getLength());
            sudokuAdapter = new SudokuAdapter(context, game);
            sudokuView.setAdapter(sudokuAdapter);
        }

        currentGame = game;
        currentGrid = null;
    }

    private void loadSolver() {
        CharSequence[] charSequences = {"4 x 4", "9 x 9", "16 x 16"};
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogStyle))
                .setTitle(R.string.sudoku_size)
                .setSingleChoiceItems(charSequences, -1, (dialog, which) -> {
                    menu.setGroupVisible(R.id.solve_group, true);

                    int length = 9;
                    switch (which) {
                        case 0:
                            length = 4;
                            break;
                        case 1:
                            length = 9;
                            break;
                        case 2:
                            length = 16;
                            break;
                    }

                    sudokuView.setNumColumns(length);
                    Integer[][] grid = new Integer[length][length];
                    boolean[][] preNumbers = new boolean[length][length];
                    for (int i = 0; i < length; i++)
                        for (int j = 0; j < length; j++) preNumbers[i][j] = false;
                    sudokuAdapter = new SudokuAdapter(context, grid, preNumbers);
                    sudokuView.setAdapter(sudokuAdapter);
                    currentGrid = grid;
                    dialog.dismiss();
                })
                .show();
    }

    @SuppressLint("StaticFieldLeak")
    public void newSudoku(View view) {
        drawerLayout.setDrawerOpen(false, true);

        CharSequence[] charSequences = {"4 x 4", "9 x 9"};
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogStyle))
                .setTitle(R.string.new_sudoku)
                .setCancelable(!games.isEmpty())
                .setSingleChoiceItems(charSequences, -1, (dialog, which) -> {

                    mLoadingDialog.show();

                    int length = 9;
                    switch (which) {
                        case 0:
                            length = 4;
                            break;
                        case 1:
                            length = 9;
                            break;
                    }
                    new AsyncTask<Integer, Void, Game>() {
                        @Override
                        protected Game doInBackground(Integer... integers) {
                            return new Game(integers[0]);
                        }

                        @Override
                        protected void onPostExecute(Game game) {
                            super.onPostExecute(game);
                            runOnUiThread(() -> {
                                mLoadingDialog.dismiss();
                                games.add(game);
                                gamesAdapter.notifyDataSetChanged();
                                loadGame(games.size() - 1);
                            });
                        }
                    }.execute(length);

                    dialog.dismiss();
                })
                .show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTimer();
        sharedPreferences.edit().putString("Games", new Gson().toJson(games)).apply();
    }


    @Override
    protected void onResume() {
        super.onResume();
        startTimer();
        if (colorSettingChanged) {
            colorSettingChanged = false;
            drawerLayout.setDrawerOpen(false, false);
            recreate();
        }
        if (gridSettingChanged) {
            gridSettingChanged = false;
            sudokuView.setAdapter(sudokuAdapter.getNew());
        }
    }


    private void checkForUpdate() {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Sudoku");
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    HashMap<String, String> hashMap = new HashMap<>();
                    for (DataSnapshot child : snapshot.getChildren()) {
                        hashMap.put(child.getKey(), child.getValue().toString());
                    }

                    if (Integer.parseInt(hashMap.get("versionCode")) > getPackageManager().getPackageInfo(getPackageName(), 0).versionCode) {
                        drawerLayout.showIconNotification(true, true);
                    } else {
                        drawerLayout.showIconNotification(false, false);
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    drawerLayout.showIconNotification(false, false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (currentGame != null) menu.setGroupVisible(R.id.play_group, !currentGame.isFinished());
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.solve:
                mLoadingDialog.show();
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        currentGame = new Game(currentGrid);
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void unused) {
                        runOnUiThread(() -> {
                            mLoadingDialog.dismiss();
                            showSolution();
                        });
                        super.onPostExecute(unused);
                    }
                }.execute();
                break;

            case R.id.clear:
                loadSolver();
                break;

            case R.id.solutions:
                showSolution();
                break;

            case R.id.pause:
                if (hide_layout.getVisibility() == View.GONE) {
                    hide_layout.setVisibility(View.VISIBLE);
                    stopTimer();
                    item.setIcon(R.drawable.ic_samsung_play);
                } else {
                    startTimer();
                    item.setIcon(R.drawable.ic_samsung_pause);
                }

                break;

            case R.id.save:
                Game saveGame = new Gson().fromJson(new Gson().toJson(currentGame), Game.class);
                saveGame.setName(currentGame.getName() + " " + getString(R.string.copy));
                games.add(saveGame);
                gamesAdapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSolution() {
        Intent intent = new Intent().setClass(context, SolutionsActivity.class);
        intent.putExtra("game", currentGame);
        startActivity(intent);
    }

}