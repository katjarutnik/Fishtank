package com.xd.akvarij;

public class Constants {
    public static final int MAX_FPS = 60;
    public static int GRAPHIC_QUALITY = 2; // 2 HIGH, 1 MEDIUM, 0 LOW
    public static int SCREEN_WIDTH;
    public static int SCREEN_HEIGHT;

    public static int DAY_LENGTH_IN_SECONDS = 5;

    public static int AGE_MAX_INFANT = 2; //3
    public static int AGE_MAX_TEEN = 5; //7
    public static int AGE_MAX_ADULT = 8; //15
    public static int AGE_MAX = 10; //20

    public static int PREGNANCY_DAYS = 2;
    public static int PREGNANCY_CHANCE = 45;
    public static int PREGNANCY_TWINS_CHANCE = 15;
    public static int MUTATION_CHANCE = 5;

    public static int FISH_SIZE_INFANT = 16;
    public static int FISH_SIZE_TEEN = 32;
    public static int FISH_SIZE_ADULT = 48;
    public static int FISH_SIZE_OLD = 64;

    public static int MAX_HORIZONTAL_SPEED = 5;
    public static int MIN_HORIZONTAL_SPEED = 1;
    public static int MAX_VERTICAL_SPEED = 3;
    public static int MIN_VERTICAL_SPEED = 1;
    public static int MED_VISION = 700;
    public static int MIN_VISION = 300;
    public static int MAX_VISION = MED_VISION + MIN_VISION;

    public static int MAX_HUNGER = 5;
    public static int HUNGER_AFTER_POOP = 2;

    public static int FOOD_SIZE = 10;
    public static int POOP_SIZE = 12;

    // alters accelerometer input
    public static int MAX_PERSISTENCE_X_UPPER = 5;
    public static int MIN_PERSISTENCE_X_UPPER = -5;
    public static int MAX_PERSISTENCE_X_LOWER = 3;
    public static int MIN_PERSISTENCE_X_LOWER = -3;
    public static int MAX_PERSISTENCE_Y_UPPER = 5;
    public static int MIN_PERSISTENCE_Y_UPPER = -5;
    public static int MAX_PERSISTENCE_Y_LOWER = 3;
    public static int MIN_PERSISTENCE_Y_LOWER = -3;

    public static int FISH_IMAGE = R.drawable.fish_test;

}
