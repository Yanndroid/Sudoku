package de.dlyt.yanndroid.sudoku;

import android.content.Intent;
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

import java.util.HashMap;

import de.dlyt.yanndroid.samsung.ThemeColor;
import de.dlyt.yanndroid.samsung.layout.DrawerLayout;

public class MainActivity extends AppCompatActivity {

    public static boolean colorSettingChanged = false;
    public static boolean gridSettingChanged = false;

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

    private DrawerLayout drawerLayout;
    private GridView sudokuView;
    private DatabaseReference mDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeColor(this);
        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_view);
        setSupportActionBar(drawerLayout.getToolbar());
        drawerLayout.setDrawerIconOnClickListener(v -> startActivity(new Intent().setClass(getApplicationContext(), SettingsActivity.class)));


        sudokuView = findViewById(R.id.sudokuView);
        sudokuView.setClipToOutline(true);
        sudokuView.setNumColumns(grid.length);

        sudokuView.setAdapter(new SudokuAdapter(this, grid));

        checkForUpdate();
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
            sudokuView.setAdapter(new SudokuAdapter(this, grid));
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