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
import de.dlyt.yanndroid.sudoku.utils.Game;
import de.dlyt.yanndroid.sudoku.utils.GameArrayList;
import de.dlyt.yanndroid.sudoku.utils.SudokuAdapter;

public class MainActivity extends AppCompatActivity {

    public static boolean colorSettingChanged = false;
    public static boolean gridSettingChanged = false;

    private DrawerLayout drawerLayout;
    private OptionGroup optionGroup;
    private Dialog mLoadingDialog;

    private Context context;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;

    private GridView sudokuView;
    private SudokuAdapter sudokuAdapter;
    private GameArrayList games = new GameArrayList();
    private Game currentGame;
    private Integer[][] currentGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeColor(this);
        setContentView(R.layout.activity_main);

        context = this;
        sharedPreferences = getSharedPreferences("Sudoku", Activity.MODE_PRIVATE);

        drawerLayout = findViewById(R.id.drawer_view);
        setSupportActionBar(drawerLayout.getToolbar());
        drawerLayout.setDrawerIconOnClickListener(v -> startActivity(new Intent().setClass(getApplicationContext(), SettingsActivity.class)));

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
                    loadGame(games.get(0));
                    break;
                case R.id.solve_sudoku:
                    loadSolver();
                    break;
            }
        });

        checkForUpdate();


        games = new Gson().fromJson(sharedPreferences.getString("Games", "{}"), GameArrayList.class);
        if (games.isEmpty()) {
            newSudoku(null);
        } else {
            loadGame(games.get(games.size() - 1));
        }
    }


    private void loadGame(Game game) {
        drawerLayout.setToolbarSubtitle(context.getResources().getString(R.string.elapsed_time, String.valueOf(game.getTime())));
        drawerLayout.getToolbar().getMenu().setGroupVisible(R.id.play_group, true);
        drawerLayout.getToolbar().getMenu().setGroupVisible(R.id.solve_group, false);

        sudokuView.setNumColumns(game.getLength());
        sudokuAdapter = new SudokuAdapter(context, game);
        sudokuView.setAdapter(sudokuAdapter);

        currentGame = game;
        currentGrid = null;
    }

    private void loadSolver() {
        CharSequence[] charSequences = {"4 x 4", "9 x 9", "16 x 16"};
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogStyle))
                .setTitle("Sudoku size")
                .setSingleChoiceItems(charSequences, -1, (dialog, which) -> {
                    drawerLayout.setToolbarSubtitle("Solver");
                    drawerLayout.getToolbar().getMenu().setGroupVisible(R.id.play_group, false);
                    drawerLayout.getToolbar().getMenu().setGroupVisible(R.id.solve_group, true);

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
                    currentGame = null;
                    dialog.dismiss();
                })
                .show();
    }

    @SuppressLint("StaticFieldLeak")
    public void newSudoku(View view) {
        drawerLayout.setDrawerOpen(false, true);

        CharSequence[] charSequences = {"4 x 4", "9 x 9"};
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.DialogStyle))
                .setTitle("Sudoku size")
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
                                loadGame(game);
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
        sharedPreferences.edit().putString("Games", new Gson().toJson(games)).apply();
    }


    @Override
    protected void onResume() {
        super.onResume();
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
                break;

            case R.id.save:
                games.add(currentGame);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSolution() {
        sudokuAdapter = new SudokuAdapter(context, currentGame.getSolutions().get(0), currentGame.getPreNumbers(), true);
        sudokuView.setAdapter(sudokuAdapter);
    }

}