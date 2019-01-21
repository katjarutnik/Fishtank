package com.xd.akvarij;

import android.content.Context;
import android.graphics.Canvas;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    public MainThread thread;

    public Tank tank;

    Context context;

    public boolean daytime;

    final MediaPlayer mp;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new MainThread(getHolder(), this);
        this.context = context;
        mp = MediaPlayer.create(context, R.raw.tap);
        setFocusable(true);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread = new MainThread(getHolder(), this);
        thread.setRunning(true);
        thread.start();
        this.daytime = tank.dayTime;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
            retry = false;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                mp.start();


        }
        return true;
    }

    @Override
    public void draw(Canvas canvas) {
        if (canvas != null) {
            super.draw(canvas);
            tank.draw(canvas);
        }
    }

    public void update(boolean daytime) {
        if (tank.gameOver) {
            thread.setRunning(false);
        }
        tank.update(daytime);
        if (this.daytime != daytime) {
            this.daytime = daytime;
            invalidate();
            tank.update(daytime);
        }
    }
}
