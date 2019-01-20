package com.xd.akvarij;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;

import java.util.ArrayList;
import java.util.Random;

public class Fish {
    // id
    private int id;
    private boolean alive;
    // display
    private Bitmap image;
    private int width;
    private int height;
    private Paint paint;
    // position, movement
    private int x;
    private int y;
    private boolean goingRight;
    private boolean goingDown;
    private int speedHorizontal;
    private int speedVertical;
    // info, family
    private int age;
    private Gender gender;
    private LifeStage stage;
    private int mother;
    private int father;
    private ArrayList<Integer> children;
    // feeding
    private int vision;
    private int hunger;
    public Food nearestFood;
    public boolean hasFoundNearestFood;
    // other
    Random random;
    Context context;

    // constructor for new tank fish, no parents
    public Fish(int id, int x, int y, boolean goingRight, boolean goingDown,
                int speedHorizontal, int speedVertical, int vision, int hunger, int age,
                Gender gender, Paint paint, Context context) {
        this.context = context;
        this.id = id;
        this.x = x;
        this.y = y;
        this.goingRight = goingRight;
        this.goingDown = goingDown;
        this.speedHorizontal = speedHorizontal;
        this.speedVertical = speedVertical;
        this.vision = vision;
        this.hunger = hunger;
        this.age = age;
        this.gender = gender;
        this.children = new ArrayList<>();
        this.paint = paint;
        ColorFilter filter = new PorterDuffColorFilter(this.paint.getColor(), PorterDuff.Mode.SRC_IN);
        this.paint.setColorFilter(filter);
        if (age < Constants.AGE_MAX_INFANT) {
            this.stage = LifeStage.INFANT;
            Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.fishy_bmp);
            this.image = ImageManager.resize(img, Constants.FISH_SIZE_INFANT, Constants.FISH_SIZE_INFANT);
        } else if (age < Constants.AGE_MAX_TEEN) {
            this.stage = LifeStage.TEEN;
            Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.fishy_bmp);
            this.image = ImageManager.resize(img, Constants.FISH_SIZE_TEEN, Constants.FISH_SIZE_TEEN);
        } else if (age < Constants.AGE_MAX_ADULT) {
            this.stage = LifeStage.ADULT;
            Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.fishy_bmp);
            this.image = ImageManager.resize(img, Constants.FISH_SIZE_ADULT, Constants.FISH_SIZE_ADULT);
        } else {
            this.stage = LifeStage.OLD;
            Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.fishy_bmp);
            this.image = ImageManager.resize(img, Constants.FISH_SIZE_OLD, Constants.FISH_SIZE_OLD);
        }
        if (!goingRight) {
            this.image = ImageManager.flipHorizontally(this.image);
        }
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.image = ImageManager.setWhitePixelsToTransparent(this.image);
        hasFoundNearestFood = false;
        random = new Random();
        alive = true;
    }

    public int getAlive() {
        return alive ? 1 : 0;
    }

    public int getLifeStage() {
        if (this.stage == LifeStage.INFANT) {
            return 0;
        } else if (this.stage == LifeStage.TEEN) {
            return 1;
        } else if (this.stage == LifeStage.ADULT) {
            return 2;
        } else {
            return 3;
        }
    }

    public int getAge() {
        return age;
    }

    public int getGender() {
        return gender == Gender.MALE ? 0 : 1;
    }

    public int getHunger() {
        return hunger;
    }

    public int getVision() {
        return vision;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, paint);
    }

    public void update(ArrayList<Food> food, ArrayList<Fish> fish, ArrayList<Fish> graveyard,
                       ArrayList<Poop> poop, int dayNightCycle) {
        if (alive) {
            if (dayNightCycle == 2) {
                increaseHunger();
                growUp(graveyard);
            }
            defaultMove(food, poop);
        } else {
            floatToTop(fish);
        }
    }
    
    public void defaultMove(ArrayList<Food> food, ArrayList<Poop> poop) {
        // 50% chance that it will want to eat if it's not starving
        if (hunger > Constants.HUNGER_AFTER_POOP && random.nextInt(100) > 50) {
            // if it knows which food it's going after and other fish didn't eat it yet
            if (nearestFood != null && hasFoundNearestFood) {
                huntFood(food, nearestFood, poop);
            // if it finds food
            } else if (!hasFoundNearestFood && findFood(food)) {
                if (nearestFood != null) {
                    huntFood(food, nearestFood, poop);
                }
            } else {
                randomHorizontalMove();
            }
        } else if (hunger <= Constants.HUNGER_AFTER_POOP) {
            if (nearestFood != null && hasFoundNearestFood) {
                huntFood(food, nearestFood, poop);
            } else if (!hasFoundNearestFood && findFood(food)) {
                huntFood(food, nearestFood, poop);
            } else {
                randomHorizontalMove();
            }
        } else {
            randomHorizontalMove();
        }
    }

    public boolean findFood(ArrayList<Food> food) {
        Food nearest = null;
        int oldDistanceVisionX = 0;
        int oldDistanceVisionY = 0;
        int newDistanceVisionX;
        int newDistanceVisionY;
        for (Food f : food) {
            // preveri ce je hrana v vision fieldu
            if ((x - vision) <= f.getX() && f.getX() <= (x + vision) &&
                    (y - vision) <= f.getY() && f.getY() <= (y + vision)) {
                //preveri ce je trenutni blizje kot prejsni dodeljen
                if (nearest != null) {
                    if (x - vision <= f.getX() && f.getX() <= x) { // f se nahaja levo
                        newDistanceVisionX = x - f.getX();
                    } else { // f se nahaja desno
                        newDistanceVisionX = f.getX() - x;
                    }
                    if (y - vision <= f.getY() && f.getY() <= y) { // f se nahaja zgoraj
                        newDistanceVisionY = y - f.getY();
                    } else { // f se nahaja spodaj
                        newDistanceVisionY = f.getY() - y;
                    }
                    if (newDistanceVisionX < oldDistanceVisionX &&
                            newDistanceVisionY < oldDistanceVisionY) {
                        nearest = f;
                    }
                } else {
                    nearest = f; // dodeli prvega najdenega
                    if (x - vision <= f.getX() && f.getX() <= x) { // f se nahaja levo
                        oldDistanceVisionX = x - f.getX();
                    } else { // f se nahaja desno
                        oldDistanceVisionX = f.getX() - x;
                    }
                    if (y - vision <= f.getY() && f.getY() <= y) { // f se nahaja zgoraj
                        oldDistanceVisionY = y - f.getY();
                    } else { // f se nahaja spodaj
                        oldDistanceVisionY = f.getY() - y;
                    }
                }
            }
        }
        if (nearest != null) {
            nearestFood = nearest;
            return true;
        }
        else {
            return false;
        }
    }

    public void huntFood(ArrayList<Food> food, Food nearest, ArrayList<Poop> poop) {
        // ce se je zaletela v hrano
        if (nearest.getX() >= x && nearest.getX() <= x+width &&
                nearest.getY() >= y && nearest.getY() <= y+height) {
            decreaseHunger(poop);
            food.remove(nearest);
            nearestFood = null;
            hasFoundNearestFood = false;
        } else { // drugac se premikaj proti hrani
            if (x < nearest.getX()) {
                if (!goingRight) {
                    goingRight = true;
                    image = ImageManager.flipHorizontally(image);
                }
                x += speedHorizontal;
            } else {
                if (goingRight) {
                    goingRight = false;
                    image = ImageManager.flipHorizontally(image);
                }
                x -= speedHorizontal;
            }
            if (y < nearest.getY()) {
                y += speedVertical;
            } else {
                y -= speedVertical;
            }
        }
    }

    public void randomHorizontalMove() {
        if (x < Constants.SCREEN_WIDTH - width && goingRight) {
            x += speedHorizontal;
            if (x >= Constants.SCREEN_WIDTH - width) {
                goingRight = false;
                image = ImageManager.flipHorizontally(image);
            }
        } else {
            x -= speedHorizontal;
            if (x <= 0) {
                goingRight = true;
                image = ImageManager.flipHorizontally(image);
            }
        }
        if (random.nextInt(100) > 35)
            randomVerticalMove();
    }

    public void randomVerticalMove() {
        if (y < Constants.SCREEN_HEIGHT - height && goingDown) {
            y += speedVertical;
            if (y >= Constants.SCREEN_HEIGHT - height)
                goingDown = false;
        } else {
            y -= speedVertical;
            if (y <= 0)
                goingDown = true;
        }
    }

    // call when fish eats food
    public void decreaseHunger(ArrayList<Poop> poop) {
        hunger++;
        if (hunger >= Constants.MAX_HUNGER) {
            poop.add(new Poop(this.x, this.y));
            hunger = Constants.HUNGER_AFTER_POOP;
        }
    }

    // call on each new day cycle
    public void increaseHunger() {
        hunger--;
        if (hunger <= 0)
            stage = LifeStage.DEAD;
    }

    // call on each new day cycle
    public void growUp(ArrayList<Fish> graveyard) {
        age++;
        if (age == Constants.AGE_MAX_INFANT) {
            stage = LifeStage.TEEN;
            growInSize(Constants.FISH_SIZE_TEEN);
        } else if (age == Constants.AGE_MAX_TEEN) {
            stage = LifeStage.ADULT;
            growInSize(Constants.FISH_SIZE_ADULT);
        } else if (age == Constants.AGE_MAX_ADULT) {
            stage = LifeStage.OLD;
            growInSize(Constants.FISH_SIZE_OLD);
        } else if (age == Constants.AGE_MAX) {
            stage = LifeStage.DEAD;
        }
        if (stage == LifeStage.DEAD && alive) {
            alive = false;
            image = ImageManager.flipVertically(image);
            graveyard.add(this);
        }
    }

    // call after fish enters new life stage
    public void growInSize(int newFishSize) {
        Bitmap img = BitmapFactory.decodeResource(context.getResources(), R.drawable.fishy_bmp);
        image = ImageManager.resize(img, newFishSize, newFishSize);
        image = ImageManager.setWhitePixelsToTransparent(image);
        if (!goingRight) {
            image = ImageManager.flipHorizontally(image);
        }
    }

    // call when fish dies
    public void floatToTop(ArrayList<Fish> fish) {
        if (y > 0) {
            y--;
        } else {
            fish.remove(this);
        }
    }
}
