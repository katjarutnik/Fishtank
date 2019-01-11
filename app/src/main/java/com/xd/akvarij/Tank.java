package com.xd.akvarij;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

public class Tank {

    MyCallback myCallback = null;

    public ArrayList<Fish> graveyard;
    public ArrayList<Fish> fish;
    public ArrayList<Food> food;

    private int popSize;

    private Bitmap fishImage;

    private Random random;

    private Rect background;

    private Paint paint;

    public boolean dayTime;
    public int dayNightCycle;
    public int dayNightCycleTemp;
    public int dayCounter;

    String text;

    public Tank(int popSize, Bitmap fishImage, MyCallback callback) {
        this.popSize = popSize;
        this.fishImage = fishImage;
        this.graveyard = new ArrayList<>();
        this.fish = new ArrayList<>();
        this.food = new ArrayList<>();
        this.random = new Random();
        this.dayTime = true;
        this.dayNightCycle = 0;
        this.dayCounter = 0;
        this.paint = new Paint();
        this.background = new Rect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        this.myCallback = callback;
    }

    public void draw(Canvas canvas) {
        if (this.dayTime) {
            paint.setARGB(255, 204, 228, 255);
            canvas.drawRect(background, paint);
        } else {
            paint.setARGB(255, 65, 150, 190);
            canvas.drawRect(background, paint);
        }
        for (int i = 0; i < fish.size(); i++) {
            fish.get(i).draw(canvas);
        }
        for (int i = 0; i < food.size(); i++) {
            food.get(i).draw(canvas);
        }
    }

    public void update(boolean daytime) {
        if (this.dayTime != daytime) {
            dayNightCycle++;
            dayNightCycleTemp++;
            this.dayTime = daytime;
        }
        if (dayNightCycle == 2) {
            dayCounter++;
            Log.d("Tank", "IT'S A NEW DAY");
            dayNightCycle = 0;
            text = "It's day " + dayCounter + "!";
            myCallback.updateMyText(text);
        }
        for (int i = 0; i < fish.size(); i++) {
            fish.get(i).update(food, fish, graveyard, dayNightCycleTemp);
        }
        for (int i = 0; i < food.size(); i++) {
            food.get(i).update();
        }
        if (dayNightCycleTemp == 2) dayNightCycleTemp = 0;
    }

    public void generateFirstGeneration() {
        for (int i = 0; i < this.popSize; i++) {
            Paint paint = new Paint();
            paint.setARGB(255,
                    random.nextInt(256),
                    random.nextInt(256),
                    random.nextInt(256));
            fish.add(new Fish(
                    i,
                    fishImage,
                    random.nextInt(Constants.SCREEN_WIDTH - fishImage.getWidth()),
                    random.nextInt(Constants.SCREEN_HEIGHT - fishImage.getHeight()),
                    random.nextBoolean(),
                    random.nextBoolean(),
                    random.nextInt(Constants.MAX_HORIZONTAL_SPEED) +
                            Constants.MIN_HORIZONTAL_SPEED,
                    random.nextInt(Constants.MAX_VERTICAL_SPEED) +
                            Constants.MIN_VERTICAL_SPEED,
                    random.nextInt(Constants.MAX_VISION) + Constants.MIN_VISION,
                    random.nextInt(Constants.MAX_HUNGER),
                    random.nextInt(Constants.AGE_MAX),
                    (random.nextInt(100) >= 35) ? Gender.FEMALE : Gender.MALE,
                    paint));
        }
    }

    public void feedFish() {
        int i = 0;
        while (i < 10) {
            food.add(new Food(random.nextInt(Constants.SCREEN_WIDTH),
                    random.nextInt(25) * (-1)));
            i++;
        }
    }

    public void shakingStart(float x, float y) {
        for(int i = 0; i < food.size(); i++) {
            food.get(i).shaking = true;
            food.get(i).moveShaking(x, y);
        }
    }

    public void shakingStop() {
        for(int i = 0; i < food.size(); i++) {
            food.get(i).shaking = false;
        }
    }

}
