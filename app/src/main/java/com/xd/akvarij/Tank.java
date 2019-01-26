package com.xd.akvarij;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private Random random;
    private Rect background;
    private Paint paint;

    public boolean dayTime;
    public int dayNightCycle;
    public int dayCounter;

    //public StringBuilder sb = new StringBuilder();
    //public ArrayList<Data> gatherer = new ArrayList<>();
    //public DataReadWrite drw = new DataReadWrite();
    public Context context;

    public boolean gameOver = false;

    public int countFish;
    public int countFishNewborn;
    public int countFishDied;


    public Tank(int popSize, MyCallback callback, Context context) {
        this.popSize = popSize;
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
        /*if (dayNightCycle == 0 && dayCounter > 1) {
            sb = drw.readFromFile(context);
        }*/
        if (allFishAreDead()) {
            myCallback.updateTxtDays("YOU LEFT ALL YOUR FISH TO DIE. SAD.");
            gameOver = true;
        }

        if (this.dayTime != daytime) {
            myCallback.updateTxtInfoTop("It's night time.");
            dayNightCycle++;
            this.dayTime = daytime;
        }
        if (dayNightCycle == 2) {
            myCallback.updateTxtInfoTop("It's day time.");
            // COLLECT DATA
            /*for (int i = 0; i < fish.size(); i++) {
                gatherer.add(new Data(fish.get(i)));
            }
            drw.writeToFile(gatherer, context);
            gatherer.clear();*/
            dayCounter++;
            Log.d("Tank", "IT'S A NEW DAY");
            myCallback.updateTxtDays("DAY " + dayCounter);
        }
        for (int i = 0; i < fish.size(); i++)
            fish.get(i).update(food, fish, graveyard, poop, dayNightCycle);
        for (int i = 0; i < food.size(); i++)
            food.get(i).update();
        for (int i = 0; i < poop.size(); i++)
            poop.get(i).update();
        if (dayNightCycle == 2)
            dayNightCycle = 0;
    }

    public void generateRandomNew() {
        for (int i = 0; i < popSize; i++) {
            fish.add(new Fish(
                    i,
                    random.nextInt(Constants.SCREEN_WIDTH - 64),
                    random.nextInt(Constants.SCREEN_HEIGHT - 64),
                    random.nextBoolean(),
                    random.nextBoolean(),
                    random.nextInt(Constants.MAX_HORIZONTAL_SPEED) +
                            Constants.MIN_HORIZONTAL_SPEED,
                    random.nextInt(Constants.MAX_VERTICAL_SPEED) +
                            Constants.MIN_VERTICAL_SPEED,
                    random.nextInt(Constants.MED_VISION) + Constants.MIN_VISION,
                    random.nextInt(Constants.MAX_HUNGER),
                    random.nextInt(Constants.AGE_MAX),
                    (random.nextInt(100) >= 50) ? Gender.FEMALE : Gender.MALE,
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)),
                    Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256)),
                    context));
        }
    }

    public void generateCustomNew(int primaryColor, int secondaryColor) {
        for (int i = 0; i < this.popSize; i++) {
            fish.add(new Fish(
                    i,
                    random.nextInt(Constants.SCREEN_WIDTH - 64),
                    random.nextInt(Constants.SCREEN_HEIGHT - 64),
                    random.nextBoolean(),
                    random.nextBoolean(),
                    random.nextInt(Constants.MAX_HORIZONTAL_SPEED) +
                            Constants.MIN_HORIZONTAL_SPEED,
                    random.nextInt(Constants.MAX_VERTICAL_SPEED) +
                            Constants.MIN_VERTICAL_SPEED,
                    random.nextInt(Constants.MED_VISION) + Constants.MIN_VISION,
                    random.nextInt(Constants.MAX_HUNGER),
                    random.nextInt(Constants.AGE_MAX),
                    (random.nextInt(100) >= 50) ? Gender.FEMALE : Gender.MALE,
                    primaryColor,
                    secondaryColor,
                    context));
        }
    }

    private boolean allFishAreDead() {
        for (int i=0; i < fish.size(); i++) {
            if (fish.get(i).getAlive()) {
                return false;
            }
        }
        return true;
    }

    public void feedFish() {
        int i = 0;
        while (i < 10) {
            food.add(new Food(random.nextInt(Constants.SCREEN_WIDTH) - Constants.FOOD_SIZE,
                    random.nextInt(30)));
            i++;
        }
    }

    public void cleanPoop() {
        poop.clear();
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

    // if you tap on the glass and fish are nearby, they will quicky swim away
    public void scare(float x, float y) {
        for (int i = 0; i < fish.size(); i++) {
            if (fish.get(i).getAlive()) {
                if (Math.abs((int)x - fish.get(i).getX()) < 100 &&
                        (Math.abs((int)y - fish.get(i).getY())) < 100) {
                    fish.get(i).gotScared();
                }
            }
        }
    }
}
