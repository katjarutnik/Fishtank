package com.xd.akvarij;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

public class Food {
    private int x;
    private int y;
    private int size;
    private Paint paint;
    private RectF shape;

    public boolean shaking = false;

    public Food(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = Constants.FOOD_SIZE;
        this.paint = new Paint();
        paint.setARGB(255, 0, 0, 0);
        this.shape = new RectF(x, y, x+size, y+size);
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

    public void moveShaking(float gX, float gY, Random random) {
        x += gX;
        if (gY < 5) {
            y += gY;
        } else
            y += random.nextInt(5);

        if (x > Constants.SCREEN_WIDTH - size) {
            x = Constants.SCREEN_WIDTH - random.nextInt(15);
            y += random.nextInt(5);
        }
        if (y > Constants.SCREEN_HEIGHT - size) {
            y = Constants.SCREEN_HEIGHT - random.nextInt(15);
            x += random.nextInt(5);
        }
        if (x < size) {
            x += size + random.nextInt(15);
            y += random.nextInt(5);
        }
        if (y < size) {
            y += size + random.nextInt(15);
            x += random.nextInt(5);
        }
        changePosition(x, y);
    }

}
