package com.xd.akvarij;

import android.content.Context;
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

    public ArrayList<Poop> poop;

    private int popSize;
    private Bitmap fishImage;
    private Random random;
    private Rect background;
    private Paint paint;

    public boolean dayTime;
    public int dayNightCycle;
    public int dayNightCycleTemp;
    public int dayCounter;

    public StringBuilder sb = new StringBuilder();
    public ArrayList<Data> gatherer = new ArrayList<>();
    public DataReadWrite drw = new DataReadWrite();
    public Context context;

    public Tank(int popSize, Bitmap fishImage, MyCallback callback, Context context) {
        this.popSize = popSize;
        this.fishImage = fishImage;
        this.graveyard = new ArrayList<>();
        this.fish = new ArrayList<>();
        this.food = new ArrayList<>();
        this.poop = new ArrayList<>();
        this.random = new Random();
        this.dayTime = true;
        this.dayNightCycle = 0;
        this.dayCounter = 1;
        this.paint = new Paint();
        this.background = new Rect(0, 0, Constants.SCREEN_WIDTH, Constants.SCREEN_HEIGHT);
        this.myCallback = callback;
        this.context = context;
    }

    public void draw(Canvas canvas) {
        if (this.dayTime) {
            paint.setARGB(255, 255, 255, 255);
            canvas.drawRect(background, paint);
        } else {
            paint.setARGB(255, 61, 61, 61);
            canvas.drawRect(background, paint);
        }
        for (int i = 0; i < fish.size(); i++) {
            fish.get(i).draw(canvas);
        }
        for (int i = 0; i < food.size(); i++) {
            food.get(i).draw(canvas);
        }
        for (int i = 0; i < poop.size(); i++) {
            poop.get(i).draw(canvas);
        }
    }

    public void update(boolean daytime) {
        if (dayNightCycle == 0 && dayCounter > 1) {
            // DISPLAY PROCESSED DATA FROM YESTERDAY
            sb = drw.readFromFile(context);
        }
        if (this.dayTime != daytime) {
            dayNightCycle++;
            dayNightCycleTemp++;
            this.dayTime = daytime;
        }
        if (dayNightCycle == 2) {
            // COLLECT DATA
            for (int i = 0; i < fish.size(); i++) {
                gatherer.add(new Data(fish.get(i)));
            }
            drw.writeToFile(gatherer, context);
            gatherer.clear();
            dayCounter++;
            Log.d("Tank", "IT'S A NEW DAY");
            dayNightCycle = 0;
            myCallback.updateMyText("DAY " + dayCounter);
        }
        for (int i = 0; i < fish.size(); i++) {
            fish.get(i).update(food, fish, graveyard, poop, dayNightCycleTemp);
        }
        for (int i = 0; i < food.size(); i++) {
            food.get(i).update();
        }
        for (int i = 0; i < poop.size(); i++) {
            poop.get(i).update();
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
                    random.nextInt(Constants.MED_VISION) + Constants.MIN_VISION,
                    random.nextInt(Constants.MAX_HUNGER),
                    random.nextInt(Constants.AGE_MAX),
                    (random.nextInt(100) >= 35) ? Gender.FEMALE : Gender.MALE,
                    paint));
        }
    }

    public void feedFish() {
        int i = 0;
        while (i < 10) {
            food.add(new Food(random.nextInt(Constants.SCREEN_WIDTH) - Constants.FOOD_SIZE,
                    random.nextInt(30)));
            i++;
        }
    }

    public void shakingStart(float x, float y) {
        for (int i = 0; i < food.size(); i++) {
            food.get(i).shaking = true;
            food.get(i).moveShaking(x, y);
        }
        for (int i = 0; i < poop.size(); i++) {
            poop.get(i).shaking = true;
            poop.get(i).moveShaking(x, y);
        }
    }

    public void shakingStop() {
        for(int i = 0; i < food.size(); i++) {
            food.get(i).shaking = false;
        }
        for (int i = 0; i < poop.size(); i++) {
            poop.get(i).shaking = false;
        }
    }

}
