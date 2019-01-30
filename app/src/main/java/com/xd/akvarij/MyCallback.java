package com.xd.akvarij;

public interface MyCallback {
    void updateTxtDays(String myString);
    void updateTxtInfoTop(String myString);
    void updateTxtMiddle();
    void updateAdapter();
    void removeFish(Fish f, int p);
    void statsUpdateStartingPopulation(int counter);
    void statsUpdateCurrentlyAlive();
    void statsUpdateFishDeaths();
    void statsUpdateFishOffspring();
    void statsUpdateGenerationReached(int counter);
    int getCurrentGeneration();
    void decreaseEnvironment();
}