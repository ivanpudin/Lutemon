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
    private String imageResource;
    private String elementIconResource;

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

    public int getDefense() {
        return defense;
    }

    public int getLevel() {
        return level;
    }

    public int getExperience() {
        return experience;
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

    public String getElementIconResource() {
        return elementIconResource;
    }

    public void setAttackDice(int attackDice) {
        this.attackDice = attackDice;
    }

    public void setElement(String element) {
        this.element = element;
    }

    public void setAttackCount(int attackCount) {
        this.attackCount = attackCount;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void setCurrentHealth(int currentHealth) {
        this.currentHealth = currentHealth;
    }

    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }

    public void setElementIconResource(String elementIconResource) {
        this.elementIconResource = elementIconResource;
    }
}
