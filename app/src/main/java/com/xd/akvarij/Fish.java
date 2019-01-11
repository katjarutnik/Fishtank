package com.xd.akvarij;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
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

    // constructor for new tank fish, no parents
    public Fish(int id, Bitmap image, int x, int y, boolean goingRight, boolean goingDown,
                int speedHorizontal, int speedVertical, int vision, int hunger, int age,
                Gender gender, Paint paint) {
        this.id = id;
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
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
        } else if (age < Constants.AGE_MAX_TEEN) {
            this.stage = LifeStage.TEEN;
        } else if (age < Constants.AGE_MAX_ADULT) {
            this.stage = LifeStage.ADULT;
        } else {
            this.stage = LifeStage.OLD;
        }
        if (!goingRight) {
            this.image = flipHorizontally(image);
        }
        hasFoundNearestFood = false;
        random = new Random();
        alive = true;
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, x, y, paint);
    }

    public void update(ArrayList<Food> food, ArrayList<Fish> fish, ArrayList<Fish> graveyard,
                       int dayNightCycle) {
        if (alive) {
            if (dayNightCycle == 2) {
                increaseHunger();
                growUp(graveyard);
            }
            SwimFreelyAndLookForFood(food);
        } else {
            floatToTop(fish);
        }
    }

    public void SwimFreelyAndLookForFood(ArrayList<Food> food) {
        //hunger < Constants.MAX_HUNGER
        if (!hasFoundNearestFood && lookAroundForFood(food)) {
            if (nearestFood != null) {
                goAfterFood(food, nearestFood);
            }
        }
        else {
            RandomHorizontalMove();
        }
    }

    public boolean lookAroundForFood(ArrayList<Food> food) {
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

    public void goAfterFood(ArrayList<Food> food, Food nearest) {
        // ce se je zaletela v hrano
        if (nearest.getX() >= x && nearest.getX() <= x+width &&
                nearest.getY() >= y && nearest.getY() <= y+height) {
            decreaseHunger();
            food.remove(nearest);
            nearestFood = null;
            hasFoundNearestFood = false;
        } else { // drugac se premikaj proti hrani
            if (x < nearest.getX()) {
                if (!goingRight) {
                    goingRight = true;
                    image = flipHorizontally(image);
                }
                x += speedHorizontal;
            } else {
                if (goingRight) {
                    goingRight = false;
                    image = flipHorizontally(image);
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

    public void RandomHorizontalMove() {
        if (x < Constants.SCREEN_WIDTH - width && goingRight) {
            x += speedHorizontal;
            if (x >= Constants.SCREEN_WIDTH - width) {
                goingRight = false;
                image = flipHorizontally(image);
            }
        } else {
            x -= speedHorizontal;
            if (x <= 0) {
                goingRight = true;
                image = flipHorizontally(image);
            }
        }
        if (random.nextInt(100) > 35)
            RandomVerticalMove();
    }

    public void RandomVerticalMove() {
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
    public void decreaseHunger() {
        hunger++;
        if (hunger > Constants.MAX_HUNGER) {
            if (speedHorizontal > 1 && speedVertical > 1) { // ce se se vedno premika pol jo upocasni
                speedHorizontal -= 1;
                speedVertical -= 1;
            }
        }
    }

    // call this on each new day cycle
    public void increaseHunger() {
        hunger--;
        if (hunger <= 0)
            stage = LifeStage.DEAD;
    }

    // call this on each new day cyle
    public void growUp(ArrayList<Fish> graveyard) {
        age++;
        if (age == Constants.AGE_MAX_INFANT || age == Constants.AGE_MAX_TEEN ||
                age == Constants.AGE_MAX_ADULT) {
            stage = stage.getNext();
        }
        if (stage == stage.DEAD && alive) {
            alive = false;
            image = flipVertically(image);
            graveyard.add(this);
        }
    }

    public void floatToTop(ArrayList<Fish> fish) {
        if (y > 0) {
            y--;
        } else {
            fish.remove(this);
        }
    }

    public Bitmap flipHorizontally(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postScale(-1, 1, source.getWidth()/2f, source.getHeight());
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    
    public Bitmap flipVertically(Bitmap source) {
        Matrix matrix = new Matrix();
        matrix.postScale(1, -1, source.getWidth(), source.getHeight()/2f);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    /*public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
            bm = null;
        }
        return resizedBitmap;
    }*/

}
