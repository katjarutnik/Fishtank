package com.xd.akvarij;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

public class Tank {

    public boolean daytime;

    public ArrayList<Fish> fish;
    public ArrayList<Food> food;

    private int popSize;

    private Bitmap fishImage;

    private Random random;

    public Tank(int popSize, Bitmap fishImage) {
        this.popSize = popSize;
        this.fishImage = fishImage;
        this.fish = new ArrayList<>();
        this.food = new ArrayList<>();
        this.random = new Random();
        this.daytime = true;
    }

    public void draw(Canvas canvas) {
        for (int i = 0; i < fish.size(); i++) {
            fish.get(i).draw(canvas);
        }
        for (int i = 0; i < food.size(); i++) {
            food.get(i).draw(canvas);
        }
    }

    public void update() {
        for (int i = 0; i < fish.size(); i++) {
            fish.get(i).update(food);
        }
        for (int i = 0; i < food.size(); i++) {
            food.get(i).update();
        }
    }

    public void generateFirstGeneration() {
        for (int i = 0; i < this.popSize; i++) {
            Paint paint = new Paint();
            paint.setARGB(255, random.nextInt(256), random.nextInt(256),
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
                    random.nextBoolean(),
                    paint));
        }
    }

    public void feedFish() {
        int i = 0;
        while(i < 10) {
            food.add(new Food(random.nextInt(Constants.SCREEN_WIDTH),
                    random.nextInt(10) * (-1)));
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

    public ArrayList<Food> getFood() {
        return food;
    }

    public ArrayList<Fish> getFish() {
        return fish;
    }
}
