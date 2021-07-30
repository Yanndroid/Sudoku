package de.dlyt.yanndroid.sudoku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
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

    Integer[][] dummyGrid = {
            {1, 2, 3, 4},
            {3, 4, 1, 2},
            {4, null, null, null},
            {null, null, 4, null}
    };


    private DrawerLayout drawerLayout;
    private OptionGroup optionGroup;

    private Context context;
    private SharedPreferences sharedPreferences;
    private DatabaseReference mDatabase;

    private GridView sudokuView;
    private SudokuAdapter sudokuAdapter;
    private GameArrayList games = new GameArrayList();

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

        sudokuView = findViewById(R.id.sudokuView);
        sudokuView.setClipToOutline(true);


        games = new Gson().fromJson(sharedPreferences.getString("Games", "{}"), GameArrayList.class);
        if (games.isEmpty()) games.add(new Game(9));

        Game game = games.get(0);
        sudokuView.setNumColumns(game.getLength());
        sudokuAdapter = new SudokuAdapter(context, game);
        sudokuView.setAdapter(sudokuAdapter);


        optionGroup = findViewById(R.id.optionGroup);
        optionGroup.setOnOptionButtonClickListener((optionButton, i, i1) -> {
            switch (i) {
                case R.id.play_sudoku:
                    loadGame(games.get(0));
                    break;
                case R.id.solve_sudoku:
                    loadSolver(9);
                    break;
            }
        });

        checkForUpdate();
    }


    private void loadGame(Game game) {
        drawerLayout.setToolbarSubtitle(context.getResources().getString(R.string.elapsed_time, String.valueOf(game.getTime())));


        sudokuView.setNumColumns(game.getLength());
        sudokuAdapter = new SudokuAdapter(context, game);
        sudokuView.setAdapter(sudokuAdapter);
    }

    private void loadSolver(int length) {
        drawerLayout.setToolbarSubtitle("Solver");


        sudokuView.setNumColumns(length);
        Integer[][] grid = new Integer[length][length];
        boolean[][] preNumbers = new boolean[length][length];
        for (int i = 0; i < length; i++) for (int j = 0; j < length; j++) preNumbers[i][j] = false;
        sudokuAdapter = new SudokuAdapter(context, grid, preNumbers);
        sudokuView.setAdapter(sudokuAdapter);
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

}