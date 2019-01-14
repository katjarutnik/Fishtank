package com.xd.akvarij;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

public class Food {
    private int x;
    private int y;
    private float xGain;
    private float yGain;
    private int size;
    private Paint paint;
    private RectF shape;

    private Random random;

    public boolean shaking = false;


    public Food(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = Constants.FOOD_SIZE;
        this.paint = new Paint();
        paint.setARGB(255, 0, 0, 0);
        this.shape = new RectF(x, y, x+size, y+size);
        this.xGain = 0;
        this.yGain = 0;
        this.random = new Random();
    }

    public void draw(Canvas canvas) {
        canvas.drawOval(shape, paint);
    }

    public void update() {
        if (!shaking) {
            if (this.y < Constants.SCREEN_HEIGHT - size) {
                moveDefault();
            }
        }
    }

    public int getSize() {
        return this.size;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void changePosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.shape.offsetTo(x, y);
    }

    public void moveDefault() {
        this.shape.offsetTo(x, y++);
    }

    public void moveShaking(float gX, float gY) {
        float rnd = (random.nextFloat() * 0.3f) + 0.9f;

        xGain += (gX * rnd);
        if (xGain > Constants.MAX_GAIN_X_UPPER) {
            xGain = Constants.MAX_GAIN_X_UPPER;
        }
        if (xGain < Constants.MIN_GAIN_X_UPPER) {
            xGain = Constants.MIN_GAIN_X_UPPER;
        }

        yGain += (gY * rnd);
        if (yGain > Constants.MAX_GAIN_Y_UPPER) {
            yGain = Constants.MAX_GAIN_Y_UPPER;
        }
        if (yGain < Constants.MIN_GAIN_Y_UPPER) {
            yGain = Constants.MIN_GAIN_Y_UPPER;
        }

        if (xGain > Constants.MAX_GAIN_X_LOWER) {
            x += Constants.MAX_GAIN_X_LOWER;
        } else if (xGain < Constants.MIN_GAIN_X_LOWER) {
            x += Constants.MIN_GAIN_X_LOWER;
        } else {
            x += (int) xGain;
        }

        if (yGain > Constants.MAX_GAIN_Y_LOWER) {
            y += Constants.MAX_GAIN_Y_LOWER;
        } else if (yGain < Constants.MIN_GAIN_Y_LOWER) {
            y += Constants.MIN_GAIN_Y_LOWER;
        } else {
            y += (int) yGain;
        }

        if (x >= Constants.SCREEN_WIDTH - size) {
            x -= (random.nextInt(5) + size);
            y += random.nextInt(3);
        }
        if (y >= Constants.SCREEN_HEIGHT - size) {
            y -= (random.nextInt(3) + size);
            x -= random.nextInt(3);
        }
        if (x <= 0) {
            x = random.nextInt(5);
        }
        if (y <= 0) {
            y = random.nextInt(5);
        }

        changePosition(x, y);
    }

}
