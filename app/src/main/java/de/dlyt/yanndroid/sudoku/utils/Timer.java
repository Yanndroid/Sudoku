package de.dlyt.yanndroid.sudoku.utils;

import java.util.TimerTask;

public class Timer {

    private long time;
    private java.util.Timer timer;
    private Timer thisTimer;


    public interface TimerListener {
        void onTimeChanged(long time);
    }

    private TimerListener timerListener;

    public void setOnTimeChanged(TimerListener timerListener) {
        this.timerListener = timerListener;
    }


    public Timer() {
        this.time = 0;
        timer = new java.util.Timer();
        this.thisTimer = this;
    }

    public void start() {
        if (timer != null) timer.cancel();
        timer = new java.util.Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                time++;
                timerListener.onTimeChanged(time);
            }
        }, 0, 1000);
    }

    public void stop() {
        timer.cancel();
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getTime() {
        return time;
    }

    public String getTimeString() {
        return timeToString(time);
    }

    public String timeToString(long t) {
        if (t >= 3600) {
            return String.format("%02d:%02d:%02d", t / 3600, (t / 60) % 60, t % 60);
        } else {
            return String.format("%02d:%02d", t / 60, t % 60);
        }
    }
}
