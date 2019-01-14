package com.xd.akvarij;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

public class Poop {
    private int x;
    private int y;
    private int size;
    private Paint paint;
    private RectF shape;

    Random random;

    public boolean shaking = false;

    public Poop(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = Constants.POOP_SIZE;
        this.paint = new Paint();
        paint.setARGB(255, 210, 155, 34);
        this.shape = new RectF(x, y, x + size, y + size);
    }

    public void draw(Canvas canvas) {
        canvas.drawOval(this.shape, this.paint);
    }

    public void update() {
        if (!shaking) {
            if (this.y < Constants.SCREEN_HEIGHT - size) {
                moveDefault();
            }
        }
    }

    public void moveDefault() {
        this.shape.offsetTo(x, y++);
    }

    public void moveShaking(float gravityX, float gravityY) {
        random = new Random();

        this.x += gravityX;
        if (gravityY < 10) {
            y += gravityY;
        } else
            y += random.nextInt(5);

        if (x > Constants.SCREEN_WIDTH - size) {
            x = Constants.SCREEN_WIDTH - size - random.nextInt(15);
            y += random.nextInt(5);
        }
        if (y > Constants.SCREEN_HEIGHT - size) {
            y = Constants.SCREEN_HEIGHT - size - random.nextInt(3);
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

    public void changePosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.shape.offsetTo(x, y);
    }
}
