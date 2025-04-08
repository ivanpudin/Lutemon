package com.example.lutemon.model;

public abstract class Lutemon {
    private String name;
    private String element;
    private int attackDice;
    private int attackCount;
    private int defense;
    private int level;
    private int experience;
    private int currentHealth;
    private int maxHealth;
    private int id;
    private String imageResource;

    public Lutemon(String name, String element, int attackDice, int attackCount, int maxHealth, String imageResource) {
        this.name = name;
        this.element = element;
        this.attackDice = attackDice;
        this.attackCount = attackCount;
        this.currentHealth = maxHealth;
        this.maxHealth = maxHealth;
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public String getElement() {
        return element;
    }

    public int getAttackDice() {
        return attackDice;
    }

    public int getAttackCount() {
        return attackCount;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public String getImageResource() {
        return imageResource;
    }
}
