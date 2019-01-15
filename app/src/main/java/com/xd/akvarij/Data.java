package com.xd.akvarij;

// PROJEKTNI DEL ZA PREDMET
// STROJNO UČENJE IN ISKANJE NOVEGA ZNANJA
// STEP 1 GATHER DATA AT THE END OF EACH DAY CYCLE (GameView.class)
// STEP 2 PROCESS DATA
// STEP 3 DISPLAY RESULTS

public class Data {
    public int alive; // 1 - yes, 0 - no
    public int lifeStage; // 0 - infant, 1 - teen, 2 - adult, 3 - old
    public int age; // 0 <= x <= 20
    public int gender; // 0 - male, 1 - female
    public int hunger; // 0 - starving <= x <= 20 - full
    public int vision; // 300 <= x <= 1000

    public Data(Fish fish) {
        this.alive = fish.getAlive();
        this.lifeStage = fish.getLifeStage();
        this.age = fish.getAge();
        this.gender = fish.getGender();
        this.hunger = fish.getHunger();
        this.vision = fish.getVision();
    }
}
