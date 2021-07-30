package de.dlyt.yanndroid.sudoku;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

import de.dlyt.yanndroid.samsung.ThemeColor;
import de.dlyt.yanndroid.samsung.layout.AboutPage;
import de.dlyt.yanndroid.sudoku.utils.Updater;

public class AboutActivity extends AppCompatActivity {

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeColor(this);
        setContentView(R.layout.activity_about);

        AboutPage about_page = findViewById(R.id.about_page);
        MaterialButton about_github = findViewById(R.id.about_github);

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
                        about_page.setUpdateState(AboutPage.UPDATE_AVAILABLE);
                        about_page.setUpdateButtonOnClickListener(v -> Updater.DownloadAndInstall(getBaseContext(), hashMap.get("url"), hashMap.get("name") + "_" + hashMap.get("versionName") + ".apk", hashMap.get("name") + " Update", hashMap.get("versionName")));
                    } else {
                        about_page.setUpdateState(AboutPage.NO_UPDATE);
                    }

                    about_github.setVisibility(View.VISIBLE);
                    about_github.setOnClickListener(v -> startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(hashMap.get("github")))));


                } catch (PackageManager.NameNotFoundException e) {
                    about_page.setUpdateState(AboutPage.NO_UPDATE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}