package com.example.lutemon.model;
import com.example.lutemon.model.LevelingSystem;

public class LevelingSystem {

    // Constants for XP gains
    public static final int TRAINING_XP = 3;
    public static final int WIN_XP = 2;
    public static final int LOSS_XP = 1;

    // Constants for stat increases per XP
    private static final int XP_HEALTH_GAIN = 5;
    private static final int XP_ATTACK_GAIN = 2;
    private static final int XP_DEFENSE_GAIN = 1;

    public static void rewardTraining(Lutemon lutemon) {
        addExperience(lutemon, TRAINING_XP);
        lutemon.heal();
        lutemon.increaseTrainingCost();
    }

    public static void rewardWin(Lutemon lutemon) {
        addExperience(lutemon, WIN_XP);
        lutemon.heal();
    }

    public static void rewardLoss(Lutemon lutemon) {
        addExperience(lutemon, LOSS_XP);
        lutemon.heal();
    }

    private static void addExperience(Lutemon lutemon, int xp) {
        int totalXp = lutemon.getExperience() + xp;
        lutemon.setExperience(totalXp);

        // Calculate and update the Lutemon's stats based on XP
        lutemon.setMaxHealth(lutemon.getBaseMaxHealth() + totalXp * XP_HEALTH_GAIN);
        lutemon.setAttackDice(lutemon.getBaseAttackDice() + totalXp * XP_ATTACK_GAIN);
        lutemon.setDefense(lutemon.getBaseDefense() + totalXp * XP_DEFENSE_GAIN);
    }
}