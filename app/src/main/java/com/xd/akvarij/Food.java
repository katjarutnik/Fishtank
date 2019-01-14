package com.xd.akvarij;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

import java.util.Random;

public class Food {
    private int x;
    private int y;
    private float xVztrajnost;
    private float yVztrajnost;
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
        this.xVztrajnost = 0;
        this.yVztrajnost = 0;
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
        float rnd = (random.nextFloat() * 0.4f) + 0.9f;

        xVztrajnost += (gX * rnd);
        if (xVztrajnost > 7) {
            xVztrajnost = 7;
        }
        if (xVztrajnost < -7) {
            xVztrajnost = -7;
        }

        yVztrajnost += (gY * rnd);
        if (yVztrajnost > 7) {
            yVztrajnost = 7;
        }
        if (yVztrajnost < -7) {
            yVztrajnost = -7;
        }

        if (xVztrajnost > 5) {
            x += 5;
        } else if (xVztrajnost < -5) {
            x -= 5;
        } else {
            x += (int) xVztrajnost;
        }

        if (yVztrajnost > 5) {
            y += 5;
        } else if (yVztrajnost < -5) {
            y -= 5;
        } else {
            y += (int) yVztrajnost;
        }

        if (x > Constants.SCREEN_WIDTH - size) {
            x = Constants.SCREEN_WIDTH - size - random.nextInt(15);
            y += random.nextInt(15);
        }
        if (y > Constants.SCREEN_HEIGHT - size) {
            y = Constants.SCREEN_HEIGHT - size - random.nextInt(2);
            x += random.nextInt(5);
        }
        if (x < 0) {
            x += size + random.nextInt(15);
            y += random.nextInt(15);
        }
        if (y < 0) {
            y += size + random.nextInt(15);
            x += random.nextInt(15);
        }

        changePosition(x, y);
    }

}
