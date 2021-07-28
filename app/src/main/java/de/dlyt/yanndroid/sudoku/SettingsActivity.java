package de.dlyt.yanndroid.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import de.dlyt.yanndroid.samsung.layout.ToolbarLayout;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        ToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setNavigationOnClickListener(v -> onBackPressed());
    }

    public void openAboutPage(View view) {
        startActivity(new Intent().setClass(getApplicationContext(), AboutActivity.class));
    }
}