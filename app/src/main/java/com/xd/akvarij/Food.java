package com.xd.akvarij;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

public class Food {
    private int x;
    private int y;
    private float persistenceX;
    private float persistenceY;
    private int size;
    private Paint paint;
    private RectF shape;
    private Random random;

    public boolean shaking = false;

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

    public Food(int x, int y) {
        this.x = x;
        this.y = y;
        this.persistenceX = 0;
        this.persistenceY = 0;
        this.size = Constants.FOOD_SIZE;
        this.paint = new Paint();
        this.paint.setARGB(255, 0, 0, 0);
        this.shape = new RectF(x, y, x+size, y+size);
        this.random = new Random();
    }

    public int getX() {
        return x;
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

        persistenceX += (gX * rnd);
        if (persistenceX > Constants.MAX_PERSISTENCE_X_UPPER)
            persistenceX = Constants.MAX_PERSISTENCE_X_UPPER;
        if (persistenceX < Constants.MIN_PERSISTENCE_X_UPPER)
            persistenceX = Constants.MIN_PERSISTENCE_X_UPPER;

        persistenceY += (gY * rnd);
        if (persistenceY > Constants.MAX_PERSISTENCE_Y_UPPER)
            persistenceY = Constants.MAX_PERSISTENCE_Y_UPPER;
        if (persistenceY < Constants.MIN_PERSISTENCE_Y_UPPER)
            persistenceY = Constants.MIN_PERSISTENCE_Y_UPPER;

        if (persistenceX > Constants.MAX_PERSISTENCE_X_LOWER)
            x += Constants.MAX_PERSISTENCE_X_LOWER;
        else if (persistenceX < Constants.MIN_PERSISTENCE_X_LOWER)
            x += Constants.MIN_PERSISTENCE_X_LOWER;
        else
            x += (int) persistenceX;

        if (persistenceY > Constants.MAX_PERSISTENCE_Y_LOWER)
            y += Constants.MAX_PERSISTENCE_Y_LOWER;
        else if (persistenceY < Constants.MIN_PERSISTENCE_Y_LOWER)
            y += Constants.MIN_PERSISTENCE_Y_LOWER;
        else
            y += (int) persistenceY;

        if (x >= Constants.SCREEN_WIDTH - size) {
            x -= (random.nextInt(5) + size);
            y += random.nextInt(3);
        }
        if (y >= Constants.SCREEN_HEIGHT - size) {
            y -= (random.nextInt(3) + size);
            x -= random.nextInt(3);
        }
        if (x <= 0)
            x = random.nextInt(5);
        if (y <= 0)
            y = random.nextInt(5);

        changePosition(x, y);
    }

}
