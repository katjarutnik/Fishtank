package com.xd.akvarij;

public enum LifeStage {
    INFANT,
    TEEN,
    ADULT,
    OLD,
    DEAD;

    public LifeStage getNext() {
        return this.ordinal() < LifeStage.values().length - 1 ? LifeStage.values()[this.ordinal() + 1] : LifeStage.DEAD;
    }
}
