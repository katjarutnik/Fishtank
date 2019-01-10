package com.xd.akvarij;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

public class Food {
    private Rect rectangle;
    private int x, y;
    private int size = 10;
    Paint paint = new Paint();
    Random random = new Random();
    public boolean shaking = false;

    public Food() {}

    public Food(int x) {
        this.rectangle = new Rect(x, 0, x+size, size);
        this.paint.setColor(Color.BLACK);
        this.x = x;
        this.y = 0;
    }

    public Food(int x, int y) {
        this.rectangle = new Rect(x, y, x+size, y+size);
        this.paint.setColor(Color.BLACK);
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(rectangle, paint);
    }

    public void update() {
        if (!shaking) {
            if (this.y < Constants.SCREEN_HEIGHT - size) {
                moveNormally();
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

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void changePosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.rectangle.offsetTo(x, y);
    }

    public void moveNormally() {
        this.rectangle.offsetTo(x, y++);
    }

    public void moveShaking(float gX, float gY) {
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
