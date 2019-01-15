package com.xd.akvarij;

import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

import java.util.concurrent.TimeUnit;

public class MainThread extends Thread {
    private SurfaceHolder surfaceHolder;
    private GameView gameView;
    private boolean running;
    public static Canvas canvas;
    private double averageFPS;

    private boolean daytime;
    private long timer;

    public MainThread(SurfaceHolder surfaceHolder, GameView gameView) {
        super();
        this.surfaceHolder = surfaceHolder;
        this.gameView = gameView;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    @Override
    public void run() {
        gameView.daytime = gameView.tank.dayTime;
        this.daytime = gameView.tank.dayTime;
        int frameCount = 0;
        long startTime;
        long timeMillis;
        long waitTime;
        long totalTime = 0;
        long targetTime = 1000/Constants.MAX_FPS;
        this.timer = System.nanoTime();

        while (running) {
            startTime = System.nanoTime();
            canvas = null;
            try {
                canvas = this.surfaceHolder.lockCanvas();
                synchronized (surfaceHolder) {
                    this.gameView.update(this.daytime);
                    this.gameView.draw(canvas);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (canvas != null) {
                    try {
                        surfaceHolder.unlockCanvasAndPost(canvas);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            timeMillis = (System.nanoTime() - startTime) / 1000000;
            waitTime = targetTime - timeMillis;
            try {
                if (waitTime > 0) {
                    this.sleep(waitTime);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            totalTime += System.nanoTime() - startTime;
            frameCount++;
            if (frameCount == Constants.MAX_FPS) {
                averageFPS = 1000 / ((totalTime / frameCount) / 1000000);
                frameCount = 0;
                totalTime = 0;
                System.out.println("FPS:" + averageFPS);
            }
            checkIfDayPassed();
        }
    }

    public void checkIfDayPassed() {
        long endTime = System.nanoTime() - timer;
        if (TimeUnit.SECONDS.convert(endTime, TimeUnit.NANOSECONDS) >
                Constants.DAY_LENGTH_IN_SECONDS) {
            timer = System.nanoTime();
            this.daytime = !this.daytime;
            Log.d("MainThread", "HALF A DAY HAS PASSED AWAY");

        }
    }
}
