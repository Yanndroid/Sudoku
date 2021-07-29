package de.dlyt.yanndroid.sudoku;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.RippleDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

import de.dlyt.yanndroid.samsung.ColorPickerDialog;
import de.dlyt.yanndroid.samsung.ThemeColor;
import de.dlyt.yanndroid.samsung.layout.ToolbarLayout;

public class SettingsActivity extends AppCompatActivity {

    private View light_mode_card;
    private RadioButton light_mode_card_radio;
    private View dark_mode_card;
    private RadioButton dark_mode_card_radio;
    private SwitchMaterial theme_mode_system_switch;

    private View colorCircle;
    private SharedPreferences spColorTheme;
    private SwitchMaterial grid_color_switch;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeColor(this);
        setContentView(R.layout.activity_settings);

        sharedPreferences = getSharedPreferences("settings", Activity.MODE_PRIVATE);

        ToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setNavigationOnClickListener(v -> onBackPressed());

        light_mode_card = findViewById(R.id.light_mode_card);
        light_mode_card_radio = findViewById(R.id.light_mode_card_radio);
        dark_mode_card = findViewById(R.id.dark_mode_card);
        dark_mode_card_radio = findViewById(R.id.dark_mode_card_radio);
        theme_mode_system_switch = findViewById(R.id.theme_mode_system_switch);

        grid_color_switch = findViewById(R.id.grid_color_switch);
        colorCircle = findViewById(R.id.colorCircle);
        spColorTheme = getSharedPreferences("ThemeColor", Context.MODE_PRIVATE);
        GradientDrawable circleDrawable = (GradientDrawable) ((RippleDrawable) colorCircle.getBackground()).getDrawable(0);
        circleDrawable.setColor(ColorStateList.valueOf(Color.parseColor("#" + spColorTheme.getString("color", "15b76d"))));

        grid_color_switch.setChecked(sharedPreferences.getBoolean("gridColorSwitch", true));
        grid_color_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            MainActivity.gridSettingChanged = true;
            sharedPreferences.edit().putBoolean("gridColorSwitch", isChecked).apply();
        });

        setLayoutToTheme(sharedPreferences.getBoolean("darkMode", false));
        theme_mode_system_switch.setChecked(sharedPreferences.getBoolean("themeSystemSwitch", true));
        theme_mode_system_switch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sharedPreferences.edit().putBoolean("themeSystemSwitch", isChecked).apply();
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            } else {
                boolean sysIsDark = ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
                AppCompatDelegate.setDefaultNightMode(sysIsDark ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
                sharedPreferences.edit().putBoolean("darkMode", sysIsDark).apply();
            }
        });

        light_mode_card.setOnClickListener(v -> {
            theme_mode_system_switch.setChecked(false);
            sharedPreferences.edit().putBoolean("themeSystemSwitch", false).apply();
            sharedPreferences.edit().putBoolean("darkMode", false).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            setLayoutToTheme(false);
        });
        dark_mode_card.setOnClickListener(v -> {
            theme_mode_system_switch.setChecked(false);
            sharedPreferences.edit().putBoolean("themeSystemSwitch", false).apply();
            sharedPreferences.edit().putBoolean("darkMode", true).apply();
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            setLayoutToTheme(true);
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean sysIsDark = ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
        setLayoutToTheme(sysIsDark);
    }

    private void setLayoutToTheme(boolean night) {
        dark_mode_card_radio.setChecked(night);
        dark_mode_card_radio.setTypeface(night ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
        light_mode_card_radio.setChecked(!night);
        light_mode_card_radio.setTypeface(!night ? Typeface.DEFAULT_BOLD : Typeface.DEFAULT);
    }

    public void colorPickerDialog(View view) {
        ColorPickerDialog mColorPickerDialog;
        String stringColor = spColorTheme.getString("color", "15b76d");
        float[] currentColor = new float[3];
        Color.colorToHSV(Color.parseColor("#" + stringColor), currentColor);
        mColorPickerDialog = new ColorPickerDialog(this, 2, currentColor);
        mColorPickerDialog.setColorPickerChangeListener(new ColorPickerDialog.ColorPickerChangedListener() {
            @Override
            public void onColorChanged(int i, float[] fArr) {
                if (!(fArr[0] == currentColor[0] && fArr[1] == currentColor[1] && fArr[2] == currentColor[2])) {
                    ThemeColor.setColor(SettingsActivity.this, fArr);
                    MainActivity.colorSettingChanged = true;
                }
            }

            @Override
            public void onViewModeChanged(int i) {

            }
        });
        mColorPickerDialog.show();
    }

    public void openAboutPage(View view) {
        startActivity(new Intent().setClass(getApplicationContext(), AboutActivity.class));
    }
}