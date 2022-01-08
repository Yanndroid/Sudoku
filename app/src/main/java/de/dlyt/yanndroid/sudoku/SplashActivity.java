package de.dlyt.yanndroid.sudoku;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.animation.Animation;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import de.dlyt.yanndroid.oneui.layout.SplashView;
import de.dlyt.yanndroid.oneui.utils.ThemeUtil;

public class SplashActivity extends AppCompatActivity {

    private boolean launchCanceled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        new ThemeUtil(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        SplashView splashView = findViewById(R.id.splash);

        if (getSharedPreferences("de.dlyt.yanndroid.sudoku_preferences", Context.MODE_PRIVATE).getBoolean("dev_enabled", false)) {
            Spannable dev_text = new SpannableString(getString(R.string._dev));
            dev_text.setSpan(new ForegroundColorSpan(getColor(R.color.orange)), 0, dev_text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            ((TextView) splashView.findViewById(R.id.sesl_splash_text)).append(dev_text);
        }

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(splashView::startSplashAnimation, 500);

        splashView.setSplashAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!launchCanceled) launchApp();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    private void launchApp() {
        Intent intent = new Intent().setClass(getApplicationContext(), MainActivity.class);
        intent.setData(getIntent().getData()); //transfer intent data -> game import
        intent.setAction(getIntent().getAction()); //transfer intent action -> shortcuts
        startActivity(intent);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
        launchCanceled = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (launchCanceled) launchApp();
    }
}