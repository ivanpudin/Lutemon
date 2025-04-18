package com.example.lutemon.model;

public class Wind extends Lutemon {
    public Wind(String name, int attackDice, int attackCount, int maxHealth, int defense, String imageResource) {
        super(name, "Wind", attackDice, attackCount, maxHealth, defense, imageResource, "element_wind");
    }
}
