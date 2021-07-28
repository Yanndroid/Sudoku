package de.dlyt.yanndroid.sudoku;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;

import de.dlyt.yanndroid.samsung.ThemeColor;
import de.dlyt.yanndroid.samsung.layout.AboutPage;
import de.dlyt.yanndroid.sudoku.utils.Updater;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeColor(this);
        setContentView(R.layout.activity_about);

        AboutPage about_page = findViewById(R.id.about_page);
        about_page.setUpdateState(AboutPage.UPDATE_AVAILABLE);

        about_page.setUpdateButtonOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Updater.DownloadAndInstall(getBaseContext(), "https://github.com/Yanndroid/Sudoku/raw/master/app/release/app-release.apk", "Sudoku.apk", "Sudoku Update", "downloading");
            }
        });


        ((MaterialButton) findViewById(R.id.about_github)).setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/Yanndroid/Sudoku"))));

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

    }
}