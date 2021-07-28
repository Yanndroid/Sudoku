package de.dlyt.yanndroid.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import de.dlyt.yanndroid.samsung.layout.DrawerLayout;

public class MainActivity extends AppCompatActivity {


    Integer[][] grid = {
            {null, null, null, null, null, 7, null, 3, null},
            {null, null, null, null, null, null, 5, null, null},
            {null, 5, 9, null, null, 2, 7, null, null},

            {1, null, null, 4, null, null, null, null, 8},
            {null, 2, null, null, null, 5, null, null, null},
            {null, 4, 3, 8, null, null, null, 6, null},

            {9, 3, null, null, null, null, null, null, null},
            {null, null, null, null, 7, null, 8, 2, null},
            {null, 8, null, 9, 4, null, null, null, 5}
    };

    /*Integer[][] grid = {
            {1, 2, 3, 4},
            {3, 4, 1, 2},
            {4, null, null, null},
            {null, null, 4, null}
    };*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DrawerLayout drawerLayout = findViewById(R.id.drawer_view);
        setSupportActionBar(drawerLayout.getToolbar());
        drawerLayout.setDrawerIconOnClickListener(v -> startActivity(new Intent().setClass(getApplicationContext(), SettingsActivity.class)));

        drawerLayout.showIconNotification(true, true);

        GridView sudokuView = findViewById(R.id.sudokuView);
        sudokuView.setClipToOutline(true);
        sudokuView.setNumColumns(grid.length);
        sudokuView.setAdapter(new SudokuAdapter(this, grid));

    }

}