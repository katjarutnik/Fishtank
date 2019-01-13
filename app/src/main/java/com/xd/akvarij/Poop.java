package com.xd.akvarij;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.Random;

// IF FISH MAX_HUNGER == TRUE THEN POOP && FISH_HUNGER-=5
public class Poop {
    private int x;
    private int y;
    private int size;
    private Paint paint;
    private Rect shape;

    public boolean shaking = false;

    public Poop(int x, int y) {
        this.x = x;
        this.y = y;
        this.size = Constants.POOP_SIZE;
        this.paint = new Paint(Constants.POOP_COLOR);
        this.shape = new Rect(x, y, x + size, y + size);
    }

    public void draw(Canvas canvas) {
        canvas.drawRect(this.shape, this.paint);
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

    public void moveShaking(float gravityX, float gravityY, Random random) {
        this.x += gravityX;
        if (gravityY < 5) {
            y += gravityY;
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

    public void changePosition(int x, int y) {
        this.x = x;
        this.y = y;
        this.shape.offsetTo(x, y);
    }
}
