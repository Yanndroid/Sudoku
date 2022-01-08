package de.dlyt.yanndroid.sudoku;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.ColorRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.util.SeslMisc;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import de.dlyt.yanndroid.oneui.dialog.AlertDialog;
import de.dlyt.yanndroid.oneui.layout.ToolbarLayout;
import de.dlyt.yanndroid.oneui.preference.ColorPickerPreference;
import de.dlyt.yanndroid.oneui.preference.HorizontalRadioPreference;
import de.dlyt.yanndroid.oneui.preference.Preference;
import de.dlyt.yanndroid.oneui.preference.PreferenceFragment;
import de.dlyt.yanndroid.oneui.preference.SwitchPreference;
import de.dlyt.yanndroid.oneui.utils.ThemeUtil;
import de.dlyt.yanndroid.sudoku.utils.Updater;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new ThemeUtil(this);
        setContentView(R.layout.activity_settings);

        ToolbarLayout toolbarLayout = findViewById(R.id.toolbar_layout);
        toolbarLayout.setNavigationButtonTooltip(getString(R.string.sesl_navigate_up));
        toolbarLayout.setNavigationButtonOnClickListener(v -> onBackPressed());

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.settings, new SettingsFragment()).commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {

        private Context mContext;
        private SettingsActivity mActivity;
        private SharedPreferences sharedPreferences;

        @Override
        public void onAttach(Context context) {
            super.onAttach(context);
            mContext = getContext();
            if (getActivity() instanceof SettingsActivity)
                mActivity = ((SettingsActivity) getActivity());
        }

        @Override
        public void onCreatePreferences(Bundle bundle, String str) {
            addPreferencesFromResource(R.xml.preferences);
        }

        @SuppressLint("RestrictedApi")
        @Override
        public void onCreate(Bundle bundle) {
            super.onCreate(bundle);

            sharedPreferences = mContext.getSharedPreferences("de.dlyt.yanndroid.sudoku_preferences", Context.MODE_PRIVATE);
            int darkMode = ThemeUtil.getDarkMode(mContext);

            HorizontalRadioPreference darkModePref = (HorizontalRadioPreference) findPreference("dark_mode");
            darkModePref.setOnPreferenceChangeListener(this);
            darkModePref.setDividerEnabled(false);
            darkModePref.setTouchEffectEnabled(false);
            darkModePref.setEnabled(darkMode != ThemeUtil.DARK_MODE_AUTO);
            darkModePref.setValue(SeslMisc.isLightTheme(mContext) ? "0" : "1");

            SwitchPreference autoDarkModePref = (SwitchPreference) findPreference("dark_mode_auto");
            autoDarkModePref.setOnPreferenceChangeListener(this);
            autoDarkModePref.setChecked(darkMode == ThemeUtil.DARK_MODE_AUTO);

            initThemeColorPicker();
            initGameColorPicker("hint_color", "recent_hint_colors", R.color.blue);
            initGameColorPicker("error_color", "recent_error_colors", R.color.sesl_error_color);

            findPreference("grid_box_colors").setOnPreferenceChangeListener(this);
            findPreference("invert_colors").setOnPreferenceChangeListener(this);

            findPreference("how_to_play").setOnPreferenceClickListener(var1 -> {
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.how_to_play)
                        .setMessage(R.string.how_to_play_summary)
                        .setPositiveButton(R.string.dismiss, null)
                        .show();
                return true;
            });

            findPreference("restore_all_settings").setOnPreferenceClickListener(var1 -> {
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.restore_all_settings)
                        .setNegativeButton(android.R.string.cancel, null)
                        .setPositiveButton(android.R.string.ok, (dialog, which) -> {
                            mContext.getSharedPreferences("ThemeColor", Context.MODE_PRIVATE).edit().clear().apply();
                            mContext.getSharedPreferences("de.dlyt.yanndroid.sudoku_preferences", Context.MODE_PRIVATE).edit().clear().apply();

                            MainActivity.colorSettingChanged = true;
                            MainActivity.gameSettingChanged = true;
                            mActivity.recreate();
                        })
                        .show();
                return true;
            });

            Updater.checkForUpdate(getContext(), new Updater.UpdateChecker() {
                @Override
                public void updateAvailable(boolean available, String url, String versionName) {
                    Preference about_app = findPreference("about_app");
                    about_app.setWidgetLayoutResource(available ? R.layout.sesl_preference_badge : 0);
                }

                @Override
                public void githubAvailable(String url) {

                }

                @Override
                public void noConnection() {

                }
            });

            if (!sharedPreferences.getBoolean("dev_enabled", false)) {
                getPreferenceScreen().removePreference(findPreference("dev_options"));
            }
        }

        private void initThemeColorPicker() {
            ColorPickerPreference themeColorPickerPref = (ColorPickerPreference) findPreference("theme_color");
            ArrayList<Integer> recent_colors = new Gson().fromJson(sharedPreferences.getString("recent_theme_colors", new Gson().toJson(new int[]{getResources().getColor(R.color.primary_color, mContext.getTheme())})), new TypeToken<ArrayList<Integer>>() {
            }.getType());
            for (Integer recent_color : recent_colors)
                themeColorPickerPref.onColorChanged(recent_color);

            themeColorPickerPref.setOnPreferenceChangeListener((var1, var2) -> {
                Color color = Color.valueOf((Integer) var2);

                recent_colors.add((Integer) var2);
                sharedPreferences.edit().putString("recent_theme_colors", new Gson().toJson(recent_colors)).apply();

                ThemeUtil.setColor(mActivity, color.red(), color.green(), color.blue());
                MainActivity.colorSettingChanged = true;
                return true;
            });
        }

        private void initGameColorPicker(String prefKey, String shprKey, @ColorRes int defaultColor) {
            ColorPickerPreference themeColorPickerPref = (ColorPickerPreference) findPreference(prefKey);
            ArrayList<Integer> recent_colors = new Gson().fromJson(sharedPreferences.getString(shprKey, new Gson().toJson(new int[]{getResources().getColor(defaultColor, mContext.getTheme())})), new TypeToken<ArrayList<Integer>>() {
            }.getType());
            for (Integer recent_color : recent_colors)
                themeColorPickerPref.onColorChanged(recent_color);

            themeColorPickerPref.setOnPreferenceChangeListener((var1, var2) -> {
                recent_colors.add((Integer) var2);
                sharedPreferences.edit().putString("recent_hint_colors", new Gson().toJson(recent_colors)).apply();
                MainActivity.gameSettingChanged = true;
                return true;
            });
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            getView().setBackgroundColor(getResources().getColor(R.color.item_background_color, mContext.getTheme()));
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {

            switch (preference.getKey()) {
                case "dark_mode":
                    String currentDarkMode = String.valueOf(ThemeUtil.getDarkMode(mContext));
                    if (currentDarkMode != newValue) {
                        ThemeUtil.setDarkMode(mActivity, ((String) newValue).equals("0") ? ThemeUtil.DARK_MODE_DISABLED : ThemeUtil.DARK_MODE_ENABLED);
                    }
                    return true;
                case "dark_mode_auto":
                    HorizontalRadioPreference darkModePref = (HorizontalRadioPreference) findPreference("dark_mode");
                    if ((boolean) newValue) {
                        darkModePref.setEnabled(false);
                        ThemeUtil.setDarkMode(mActivity, ThemeUtil.DARK_MODE_AUTO);
                    } else {
                        darkModePref.setEnabled(true);
                    }
                    return true;
                case "grid_box_colors":
                case "invert_colors":
                    MainActivity.gameSettingChanged = true;
                    return true;
            }

            return false;
        }

    }
}