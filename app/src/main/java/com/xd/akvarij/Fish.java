package com.xd.akvarij;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.ArrayList;
import java.util.Random;

public class Fish {
    // id
    private int id;
    private boolean alive;
    private int generation;
    // display
    public Bitmap image;
    private int width;
    private int height;
    //private Paint paint;
    private int primaryColor;
    private int secondaryColor;
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
    private boolean pregnant;
    private int pregnantDays;
    private int eggs;
    private Fish coParent;
    // feeding
    private int vision;
    private int hunger;
    public Food nearestFood;
    public boolean hasFoundNearestFood;
    // other
    private Random random;
    private Context context;
    private MyCallback myCallback;
    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);

    // TODO FIX BLADDER NEED
    // TODO ADD ENVIRONMENT NEED
    // TODO ADD OVERALL HAPPINESS

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, p);
    }

    public void update(ArrayList<Food> food, ArrayList<Fish> fish,
                       ArrayList<Poop> poop, int dayNightCycle) {
        if (alive) {
            if (dayNightCycle == 2) { // new day
                becomeHungry();
                growUp();
                if (pregnant && pregnantDays == Constants.PREGNANCY_DAYS) {
                    pregnant = false;
                    while (this.eggs > 0) {
                        fish.add(new Fish(this, this.coParent, context, myCallback));
                        myCallback.statsUpdateFishOffspring();
                        myCallback.updateAdapter();
                        this.eggs--;
                    }
                    this.coParent = null;
                } else if (pregnant && pregnantDays < Constants.PREGNANCY_DAYS) {
                    pregnantDays++;
                }
                if ((gender == Gender.FEMALE) && (eggs < 2) && (random.nextInt(100) < Constants.PREGNANCY_CHANCE)) {
                    getPregnant(fish);
                }
            }
            defaultMove(food, poop);
        } else {
            floatToTop(fish);
        }
    }

    // first gen
    public Fish(int id, int x, int y, boolean goingRight, boolean goingDown,
                int speedHorizontal, int speedVertical, int vision, int hunger, int age,
                Gender gender, int primaryColor, int secondaryColor, Context context,
                MyCallback myCallback) {
        this.myCallback = myCallback;
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
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        Bitmap img = BitmapFactory.decodeResource(context.getResources(), Constants.FISH_IMAGE);
        if (age < Constants.AGE_MAX_INFANT) {
            this.stage = LifeStage.INFANT;
            img = ImageManager.resize(img, Constants.FISH_SIZE_INFANT, Constants.FISH_SIZE_INFANT);
        } else if (age < Constants.AGE_MAX_TEEN) {
            this.stage = LifeStage.TEEN;
            img = ImageManager.resize(img, Constants.FISH_SIZE_TEEN, Constants.FISH_SIZE_TEEN);
        } else if (age < Constants.AGE_MAX_ADULT) {
            this.stage = LifeStage.ADULT;
            img = ImageManager.resize(img, Constants.FISH_SIZE_ADULT, Constants.FISH_SIZE_ADULT);
        } else {
            this.stage = LifeStage.OLD;
            img = ImageManager.resize(img, Constants.FISH_SIZE_OLD, Constants.FISH_SIZE_OLD);
        }
        this.image = ImageManager.setPrimaryAndSecondaryColor(img, primaryColor, secondaryColor);
        if (!goingRight)
            this.image = ImageManager.flipHorizontally(img);
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.hasFoundNearestFood = false;
        this.random = new Random();
        this.alive = true;
        this.pregnant = false;
        this.pregnantDays = 0;
        this.eggs = 0;
        this.generation = 0;
    }

    // offspring
    public Fish(Fish mom, Fish dad, Context context, MyCallback myCallback) {
        this.myCallback = myCallback;
        this.random = new Random();
        this.context = context;
        this.id = 0; // placeholder, id has no use for now
        this.x = Integer.valueOf(mom.x);
        this.y = Integer.valueOf(mom.y);
        this.goingRight = random.nextBoolean();
        this.goingDown = random.nextBoolean();
        this.speedHorizontal = Integer.valueOf((mom.speedHorizontal + dad.speedHorizontal) / 2);
        this.speedVertical = Integer.valueOf((mom.speedVertical + dad.speedVertical) / 2);
        this.vision = Integer.valueOf((mom.vision + dad.vision) / 2);
        this.hunger = Constants.HUNGER_AFTER_POOP;
        this.age = 0;
        this.stage = LifeStage.INFANT;
        this.gender = random.nextBoolean() ? Gender.FEMALE : Gender.MALE;
        if (random.nextInt(100) < Constants.MUTATION_CHANCE)
            this.primaryColor = 0xFF000000
                    | (random.nextInt(256) << 16)
                    | (random.nextInt(256) << 8)
                    | (random.nextInt(256));
        else
            this.primaryColor = random.nextBoolean() ? Integer.valueOf(mom.primaryColor) :
                    Integer.valueOf(dad.secondaryColor);
        if (random.nextInt(100) < Constants.MUTATION_CHANCE)
            this.secondaryColor = 0xFF000000
                    | (random.nextInt(256) << 16)
                    | (random.nextInt(256) << 8)
                    | (random.nextInt(256));
        else
            this.secondaryColor = random.nextBoolean() ? Integer.valueOf(dad.secondaryColor) :
                    Integer.valueOf(mom.primaryColor);
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), Constants.FISH_IMAGE);
        bm = ImageManager.resize(bm, Constants.FISH_SIZE_INFANT, Constants.FISH_SIZE_INFANT);
        if (!this.goingRight)
             bm = ImageManager.flipHorizontally(bm);
        this.image = ImageManager.setPrimaryAndSecondaryColor(bm, primaryColor, secondaryColor);
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.hasFoundNearestFood = false;
        this.alive = true;
        this.pregnant = false;
        this.pregnantDays = 0;
        this.eggs = 0;
        this.generation = Integer.valueOf(mom.generation + 1);
        if (this.generation > myCallback.getCurrentGeneration())
            myCallback.statsUpdateGenerationReached(this.generation);
    }

    public int getId() {
        return id;
    }

    public boolean getAlive() {
        return alive;
    }

    public int getLifeStage() {
        if (this.stage == LifeStage.INFANT)
            return 0;
        else if (this.stage == LifeStage.TEEN)
            return 1;
        else if (this.stage == LifeStage.ADULT)
            return 2;
        else
            return 3;
    }

    public int getAge() {
        return age;
    }

    public boolean getGender() {
        return gender == Gender.MALE;
    }

    public boolean getPregnant() {
        return pregnant;
    }

    public int getHunger() {
        return hunger;
    }

    public int getVision() {
        return vision;
    }

    public int getSpeed() {
        return speedHorizontal;
    }

    public int getEggs() {
        return eggs;
    }

    public int getX () {
        return x;
    }

    public int getY () {
        return y;
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
            eatFood();
            food.remove(nearest);
            nearestFood = null;
            hasFoundNearestFood = false;
            myCallback.updateAdapter();
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
                if (!goingDown) {
                    goingDown = true;
                }
                y += speedVertical;
            } else {
                if (goingDown) {
                    goingDown = false;
                }
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

    public void eatFood() {
        if (hunger < Constants.MAX_HUNGER) {
            hunger++;
        }
    }

    // call on each new day cycle
    public void becomeHungry() {
        hunger--;
        if (hunger <= 0)
            stage = LifeStage.DEAD;
    }

    // call on each new day cycle
    public void growUp() {
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
            myCallback.statsUpdateFishDeaths();
            myCallback.statsUpdateCurrentlyAlive();
            myCallback.updateAdapter();
            alive = false;
            image = ImageManager.flipVertically(image);
        }
    }

    // call after fish enters new life stage
    public void growInSize(int newFishSize) {
        Bitmap bm = BitmapFactory.decodeResource(context.getResources(), Constants.FISH_IMAGE);
        bm = ImageManager.resize(bm, newFishSize, newFishSize);
        bm = ImageManager.setPrimaryAndSecondaryColor(bm, primaryColor, secondaryColor);
        if (!goingRight) {
            image = ImageManager.flipHorizontally(bm);
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

    // TODO FITNESS FUNCTION
    public void getPregnant(ArrayList<Fish> fish) {
        for (int i = 0; i < fish.size(); i++) {
            if (fish.get(i) != this
                    && (this.stage == LifeStage.TEEN || this.stage == LifeStage.ADULT)
                    && fish.get(i).gender == Gender.MALE
                    && (fish.get(i).stage == LifeStage.TEEN || fish.get(i).stage == LifeStage.ADULT)) {
                this.pregnant = true;
                this.coParent = fish.get(i);
                this.eggs++;
                if (random.nextInt(100) < Constants.PREGNANCY_TWINS_CHANCE)
                    this.eggs++;
                break;
            }
        }
    }

    // if you tapped on the glass
    public void gotScared() {
        if (hasFoundNearestFood)
            hasFoundNearestFood = false;
        changeHorizontalSwimmingDirection();
        changeVerticalSwimmingDirection();
    }

    public void changeHorizontalSwimmingDirection() {
        if (goingRight) {
            goingRight = false;
            image = ImageManager.flipHorizontally(image);
        } else {
            goingRight = true;
            image = ImageManager.flipHorizontally(image);
        }
    }

    public void changeVerticalSwimmingDirection() {
        goingDown = !goingDown;
    }


}
