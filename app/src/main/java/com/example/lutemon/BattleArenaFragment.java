package com.example.lutemon;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonStorage;

import java.util.ArrayList;
import java.util.Random;

public class BattleArenaFragment extends Fragment {
    private CardView battleControlsCard;
    private CardView battleResultLayout;
    private Lutemon playerLutemon;
    private Lutemon enemyLutemon;
    private Random random = new Random();

    // UI elements
    private ImageView playerImage, enemyImage;
    private TextView playerName, enemyName;
    private TextView playerHealth, enemyHealth;
    private TextView playerStatus, enemyStatus;
    private TextView battleLog;
    private ProgressBar playerFatalityBar, enemyFatalityBar, playerHealthBar, enemyHealthBar;
    private Button attackButton, specialButton, fatalityButton;

    // Cooldown trackers
    private int playerSpecialCooldown = 0;
    private int enemySpecialCooldown = 0;
    private int playerFatalityCharge = 0;
    private int enemyFatalityCharge = 0;
    private boolean isPlayerTurn = true;

    // Status effects
    private int playerStatusTurns = 0;
    private String playerStatusEffect = "";
    private int enemyStatusTurns = 0;
    private String enemyStatusEffect = "";
    private boolean end = false;

    public static BattleArenaFragment newInstance(Lutemon player, Lutemon enemy) {
        BattleArenaFragment fragment = new BattleArenaFragment();
        Bundle args = new Bundle();
        args.putSerializable("player", player);
        args.putSerializable("enemy", enemy);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerLutemon = (Lutemon) getArguments().getSerializable("player");
            enemyLutemon = (Lutemon) getArguments().getSerializable("enemy");

            // Refresh the Lutemons from storage to get latest stats
            playerLutemon = LutemonStorage.getInstance().getLutemonById(playerLutemon.getId());
            enemyLutemon = LutemonStorage.getInstance().getLutemonById(enemyLutemon.getId());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle_arena, container, false);

        // Initialize UI elements
        playerImage = view.findViewById(R.id.playerLutemonImage);
        enemyImage = view.findViewById(R.id.aiLutemonImage);
        playerName = view.findViewById(R.id.playerLutemonName);
        enemyName = view.findViewById(R.id.aiLutemonName);
        playerHealth = view.findViewById(R.id.playerHealthText);
        enemyHealth = view.findViewById(R.id.aiHealthText);
        playerHealthBar = view.findViewById(R.id.playerHealthBar);
        enemyHealthBar = view.findViewById(R.id.aiHealthBar);
        playerStatus = view.findViewById(R.id.playerStatusText);
        enemyStatus = view.findViewById(R.id.aiStatusText);
        battleLog = view.findViewById(R.id.battleLogText);
        playerFatalityBar = view.findViewById(R.id.playerFatalityBar);
        enemyFatalityBar = view.findViewById(R.id.aiFatalityBar);
        attackButton = view.findViewById(R.id.attackButton);
        specialButton = view.findViewById(R.id.specialButton);
        fatalityButton = view.findViewById(R.id.fatalityButton);
        battleResultLayout = view.findViewById(R.id.battleResultLayout);
        battleControlsCard = view.findViewById(R.id.battleControlsCard);

        // Set initial UI state
        updateUI();

        // Button listeners
        attackButton.setOnClickListener(v -> {
            if (isPlayerTurn) {
                performAttack();
                endPlayerTurn();
            }
        });

        specialButton.setOnClickListener(v -> {
            if (isPlayerTurn) {
                performSpecial();
                endPlayerTurn();
            }
        });

        fatalityButton.setOnClickListener(v -> {
            if (isPlayerTurn && playerFatalityCharge >= 10) {
                performFatality();
                endPlayerTurn();
            } else {
                addToBattleLog("Fatality not charged yet! (" + playerFatalityCharge + "/10)");
            }
        });

        return view;
    }

    private void performAttack() {
        int damage = 8; // Base attack damage
        enemyLutemon.setCurrentHealth(enemyLutemon.getCurrentHealth() - damage);
        addToBattleLog(playerLutemon.getName() + " attacks for " + damage + " damage!");

        // Charge fatality meter
        playerFatalityCharge = Math.min(playerFatalityCharge + 1, 10);
        playerFatalityBar.setProgress(playerFatalityCharge * 10);

        checkBattleEnd();
        updateUI();
    }


    private void performSpecial() {
        if (playerSpecialCooldown > 0) {
            addToBattleLog("Special ability is on cooldown! (" + playerSpecialCooldown + " turns left)");
            return;
        }

        String element = playerLutemon.getElement();
        int damage = 0;
        String effect = "";
        int effectTurns = 0;

        switch (element.toLowerCase()) {
            case "water":
                damage = 6;
                effect = "Wet";
                effectTurns = 3;
                enemyLutemon.setCurrentHealth(enemyLutemon.getCurrentHealth() - damage);
                enemyStatusEffect = effect;
                enemyStatusTurns = effectTurns;
                playerStatusEffect = effect;
                playerStatusTurns = effectTurns;
                addToBattleLog(playerLutemon.getName() + " splashes water everywhere! Both are now Wet for " + effectTurns + " turns!");
                break;
            case "fire":
                damage = 4;
                effect = "OnFire";
                effectTurns = 3;
                enemyLutemon.setCurrentHealth(enemyLutemon.getCurrentHealth() - damage);
                enemyStatusEffect = effect;
                enemyStatusTurns = effectTurns;
                addToBattleLog(playerLutemon.getName() + " engulfs the enemy in flames! " + damage + " damage and OnFire for " + effectTurns + " turns!");
                break;
            case "ice":
                damage = 4;
                effect = "Frozen";
                effectTurns = 2;
                enemyLutemon.setCurrentHealth(enemyLutemon.getCurrentHealth() - damage);
                enemyStatusEffect = effect;
                enemyStatusTurns = effectTurns;
                isPlayerTurn = true; // Skip enemy turn
                addToBattleLog(playerLutemon.getName() + " freezes the enemy solid! " + damage + " damage and Frozen for " + effectTurns + " turns!");
                break;
            case "wind":
                damage = 4;
                effect = "Bleeding";
                effectTurns = 4;
                enemyLutemon.setCurrentHealth(enemyLutemon.getCurrentHealth() - damage);
                enemyStatusEffect = effect;
                enemyStatusTurns = effectTurns;
                addToBattleLog(playerLutemon.getName() + " slices the enemy with wind blades! " + damage + " damage and Bleeding for " + effectTurns + " turns!");
                break;
            case "earth":
                damage = 4;
                effect = "Constrained";
                effectTurns = 2;
                enemyLutemon.setCurrentHealth(enemyLutemon.getCurrentHealth() - damage);
                enemyStatusEffect = effect;
                enemyStatusTurns = effectTurns;
                isPlayerTurn = true; // Skip enemy turn
                addToBattleLog(playerLutemon.getName() + " traps the enemy in earth! " + damage + " damage and Constrained for " + effectTurns + " turns!");
                break;
            case "thunder":
                damage = 4;
                effect = "Electrified";
                effectTurns = 3;
                enemyLutemon.setCurrentHealth(enemyLutemon.getCurrentHealth() - damage);
                enemyStatusEffect = effect;
                enemyStatusTurns = effectTurns;
                addToBattleLog(playerLutemon.getName() + " zaps the enemy with lightning! " + damage + " damage and Electrified for " + effectTurns + " turns!");
                break;
        }

        // Set cooldown
        playerSpecialCooldown = 3;
        specialButton.setText("Special (" + playerSpecialCooldown + ")");

        // Charge fatality meter
        playerFatalityCharge = Math.min(playerFatalityCharge + 2, 10);
        playerFatalityBar.setProgress(playerFatalityCharge * 10);

        checkBattleEnd();
        updateUI();
    }

    private void performFatality() {
        if (random.nextDouble() < 0.7) { // 70% chance to kill enemy
            enemyLutemon.setCurrentHealth(0);
            addToBattleLog(playerLutemon.getName() + " performs a FATALITY on " + enemyLutemon.getName() + "!");
        } else { // 30% chance to kill self
            playerLutemon.setCurrentHealth(0);
            addToBattleLog(playerLutemon.getName() + "'s FATALITY backfires horribly!");
        }

        playerFatalityCharge = 0;
        playerFatalityBar.setProgress(0);

        checkBattleEnd();
        updateUI();
    }

    private void endPlayerTurn() {
        if (enemyLutemon.getCurrentHealth() <= 0 || playerLutemon.getCurrentHealth() <= 0) {
            return; // Battle already ended
        }

        // Apply status effects
        applyStatusEffects();

        if (enemyStatusEffect.equals("Frozen") || enemyStatusEffect.equals("Constrained")) {
            enemyStatusTurns--;
            if (enemyStatusTurns <= 0) {
                addToBattleLog(enemyLutemon.getName() + " is no longer " + enemyStatusEffect + "!");
                enemyStatusEffect = "";
            }
            return; // Skip enemy turn if frozen or constrained
        }

        isPlayerTurn = false;
        disableButtons();

        // AI performs its move after a short delay
        new Handler().postDelayed(() -> {
            performEnemyMove();
            isPlayerTurn = true;
            enableButtons();

            // Apply status effects again after enemy turn
            applyStatusEffects();

            // Update cooldowns
            updateCooldowns();

            updateUI();
        }, 1500);
    }

    private void performEnemyMove() {
        if (end) { return; }
        if (enemyFatalityCharge >= 10 && random.nextDouble() < 0.5) {
            // 50% chance AI will use fatality if available
            if (random.nextDouble() < 0.7) { // 70% chance to kill player
                playerLutemon.setCurrentHealth(0);
                addToBattleLog(enemyLutemon.getName() + " performs a FATALITY on " + playerLutemon.getName() + "!");
            } else { // 30% chance to kill self
                enemyLutemon.setCurrentHealth(0);
                addToBattleLog(enemyLutemon.getName() + "'s FATALITY backfires horribly!");
            }
            enemyFatalityCharge = 0;
        } else if (enemySpecialCooldown <= 0 && random.nextDouble() < 0.6) {
            // 60% chance to use special if available
            performEnemySpecial();
        } else {
            // Default to normal attack
            int damage = 8;
            playerLutemon.setCurrentHealth(playerLutemon.getCurrentHealth() - damage);
            addToBattleLog(enemyLutemon.getName() + " attacks for " + damage + " damage!");

            // Charge fatality meter
            enemyFatalityCharge = Math.min(enemyFatalityCharge + 1, 10);
            enemyFatalityBar.setProgress(enemyFatalityCharge * 10);
        }

        checkBattleEnd();
    }

    private void performEnemySpecial() {
        String element = enemyLutemon.getElement();
        int damage = 0;
        String effect = "";
        int effectTurns = 0;

        switch (element.toLowerCase()) {
            case "water":
                damage = 6;
                effect = "Wet";
                effectTurns = 3;
                playerLutemon.setCurrentHealth(playerLutemon.getCurrentHealth() - damage);
                playerStatusEffect = effect;
                playerStatusTurns = effectTurns;
                enemyStatusEffect = effect;
                enemyStatusTurns = effectTurns;
                addToBattleLog(enemyLutemon.getName() + " splashes water everywhere! Both are now Wet for " + effectTurns + " turns!");
                break;
            case "fire":
                damage = 4;
                effect = "OnFire";
                effectTurns = 3;
                playerLutemon.setCurrentHealth(playerLutemon.getCurrentHealth() - damage);
                playerStatusEffect = effect;
                playerStatusTurns = effectTurns;
                addToBattleLog(enemyLutemon.getName() + " engulfs you in flames! " + damage + " damage and OnFire for " + effectTurns + " turns!");
                break;
            case "ice":
                damage = 4;
                effect = "Frozen";
                effectTurns = 2;
                playerLutemon.setCurrentHealth(playerLutemon.getCurrentHealth() - damage);
                playerStatusEffect = effect;
                playerStatusTurns = effectTurns;
                isPlayerTurn = false; // Skip player turn
                addToBattleLog(enemyLutemon.getName() + " freezes you solid! " + damage + " damage and Frozen for " + effectTurns + " turns!");
                break;
            case "wind":
                damage = 4;
                effect = "Bleeding";
                effectTurns = 4;
                playerLutemon.setCurrentHealth(playerLutemon.getCurrentHealth() - damage);
                playerStatusEffect = effect;
                playerStatusTurns = effectTurns;
                addToBattleLog(enemyLutemon.getName() + " slices you with wind blades! " + damage + " damage and Bleeding for " + effectTurns + " turns!");
                break;
            case "earth":
                damage = 4;
                effect = "Constrained";
                effectTurns = 2;
                playerLutemon.setCurrentHealth(playerLutemon.getCurrentHealth() - damage);
                playerStatusEffect = effect;
                playerStatusTurns = effectTurns;
                isPlayerTurn = false; // Skip player turn
                addToBattleLog(enemyLutemon.getName() + " traps you in earth! " + damage + " damage and Constrained for " + effectTurns + " turns!");
                break;
            case "thunder":
                damage = 4;
                effect = "Electrified";
                effectTurns = 3;
                playerLutemon.setCurrentHealth(playerLutemon.getCurrentHealth() - damage);
                playerStatusEffect = effect;
                playerStatusTurns = effectTurns;
                addToBattleLog(enemyLutemon.getName() + " zaps you with lightning! " + damage + " damage and Electrified for " + effectTurns + " turns!");
                break;
        }

        // Set cooldown
        enemySpecialCooldown = 3;

        // Charge fatality meter
        enemyFatalityCharge = Math.min(enemyFatalityCharge + 2, 10);
        enemyFatalityBar.setProgress(enemyFatalityCharge * 10);

        checkBattleEnd();
    }

    private void applyStatusEffects() {
        // Apply player status effects to enemy
        if (!playerStatusEffect.isEmpty() && playerStatusTurns > 0) {
            playerStatusTurns--;
            if (playerStatusEffect.equals("OnFire") || playerStatusEffect.equals("Bleeding") || playerStatusEffect.equals("Electrified")) {
                enemyLutemon.setCurrentHealth(enemyLutemon.getCurrentHealth() - 2); // 2 damage per turn
                addToBattleLog(enemyLutemon.getName() + " takes 2 damage from " + playerStatusEffect + "!");
            }
            if (playerStatusTurns <= 0) {
                addToBattleLog(playerLutemon.getName() + " is no longer " + playerStatusEffect + "!");
                playerStatusEffect = "";
            }
        }

        // Apply enemy status effects to player
        if (!enemyStatusEffect.isEmpty() && enemyStatusTurns > 0) {
            enemyStatusTurns--;
            if (enemyStatusEffect.equals("OnFire") || enemyStatusEffect.equals("Bleeding") || enemyStatusEffect.equals("Electrified")) {
                playerLutemon.setCurrentHealth(playerLutemon.getCurrentHealth() - 2); // 2 damage per turn
                addToBattleLog(playerLutemon.getName() + " takes 2 damage from " + enemyStatusEffect + "!");
            }
            if (enemyStatusTurns <= 0) {
                addToBattleLog(enemyLutemon.getName() + " is no longer " + enemyStatusEffect + "!");
                enemyStatusEffect = "";
            }
        }
    }

    private void updateCooldowns() {
        if (playerSpecialCooldown > 0) {
            playerSpecialCooldown--;
            specialButton.setText(playerSpecialCooldown > 0 ? "Special (" + playerSpecialCooldown + ")" : "Special");
        }

        if (enemySpecialCooldown > 0) {
            enemySpecialCooldown--;
        }
    }

    private void checkBattleEnd() {
        if (playerLutemon.getCurrentHealth() <= 0) {
            playerLutemon.setCurrentHealth(0);
            addToBattleLog(playerLutemon.getName() + " has been defeated!");
            disableButtons();
            end = true;

            // Update win/loss records
            enemyLutemon.won();
            playerLutemon.lost();

            // Save to storage
            LutemonStorage.getInstance().setLutemons((ArrayList<Lutemon>)LutemonStorage.getInstance().getLutemons());

            // Show return button
            showBattleEnd();
        } else if (enemyLutemon.getCurrentHealth() <= 0) {
            enemyLutemon.setCurrentHealth(0);
            addToBattleLog(enemyLutemon.getName() + " has been defeated!");
            disableButtons();
            end = true;

            // Update win/loss records
            playerLutemon.won();
            enemyLutemon.lost();

            // Save to storage
            LutemonStorage.getInstance().setLutemons((ArrayList<Lutemon>)LutemonStorage.getInstance().getLutemons());

            // Show return button
            new Handler().postDelayed(() -> {
                showBattleResult();
            }, 1000);
        }
    }

    private void showBattleResult() {
        if (getView() == null) return;

        // Initialize views if not already done
        if (battleControlsCard == null) {
            battleControlsCard = getView().findViewById(R.id.battleControlsCard);
        }
        if (battleResultLayout == null) {
            battleResultLayout = getView().findViewById(R.id.battleResultLayout);
        }

        // Hide battle controls
        battleControlsCard.setVisibility(View.GONE);

        // Show result layout
        battleResultLayout.setVisibility(View.VISIBLE);

        // Set result message
        TextView resultMessage = battleResultLayout.findViewById(R.id.resultMessage);
        if (playerLutemon.getCurrentHealth() <= 0) {
            resultMessage.setText("You lost the battle!");
        } else {
            resultMessage.setText("You won the battle!");
        }

        // Set up return button
        Button returnButton = battleResultLayout.findViewById(R.id.homeButton);
        returnButton.setOnClickListener(v -> {
            requireActivity().getSupportFragmentManager().popBackStack();
        });

        // Set up new battle button
        Button newBattleButton = battleResultLayout.findViewById(R.id.newBattleButton);
        newBattleButton.setOnClickListener(v -> {
            battleResultLayout.setVisibility(View.GONE);
            battleControlsCard.setVisibility(View.VISIBLE);
            resetBattle();
        });
    }

    private void resetBattle() {
        // Reset health
        playerLutemon.setCurrentHealth(playerLutemon.getMaxHealth());
        enemyLutemon.setCurrentHealth(enemyLutemon.getMaxHealth());

        // Reset status effects
        playerStatusEffect = "";
        enemyStatusEffect = "";
        playerStatusTurns = 0;
        enemyStatusTurns = 0;

        // Reset cooldowns
        playerSpecialCooldown = 0;
        enemySpecialCooldown = 0;

        // Reset fatality
        playerFatalityCharge = 0;
        enemyFatalityCharge = 0;

        // Reset turn
        isPlayerTurn = true;

        // Update UI
        updateUI();
        enableButtons();

        // Clear battle log
        battleLog.setText("");
        addToBattleLog("New battle started!");
    }

    private void showBattleEnd() {
        // Replace action buttons with a return button
        attackButton.setVisibility(View.GONE);
        specialButton.setVisibility(View.GONE);
        fatalityButton.setVisibility(View.GONE);

        //Button returnButton = getView().findViewById(R.id.returnButton);
        //returnButton.setVisibility(View.VISIBLE);
        //returnButton.setOnClickListener(v -> {
        //    requireActivity().getSupportFragmentManager().popBackStack();
        //});
    }

    private void disableButtons() {
        attackButton.setEnabled(false);
        specialButton.setEnabled(false);
        fatalityButton.setEnabled(false);
    }

    private void enableButtons() {
        attackButton.setEnabled(true);
        specialButton.setEnabled(playerSpecialCooldown <= 0);
        fatalityButton.setEnabled(playerFatalityCharge >= 10);
    }

    private void updateUI() {
        // Player info
        playerName.setText(playerLutemon.getName());
        playerHealth.setText("HP: " + playerLutemon.getCurrentHealth() + "/" + playerLutemon.getMaxHealth());
        playerHealthBar.setProgress(playerLutemon.getCurrentHealth() * 10);
        playerStatus.setText(playerStatusEffect.isEmpty() ? "" : playerStatusEffect + " (" + playerStatusTurns + ")");

        // Enemy info
        enemyName.setText(enemyLutemon.getName());
        enemyHealth.setText("HP: " + enemyLutemon.getCurrentHealth() + "/" + enemyLutemon.getMaxHealth());
        enemyHealthBar.setProgress(enemyLutemon.getCurrentHealth() * 10);
        enemyStatus.setText(enemyStatusEffect.isEmpty() ? "" : enemyStatusEffect + " (" + enemyStatusTurns + ")");

        // Update button states
        specialButton.setText(playerSpecialCooldown > 0 ? "Special (" + playerSpecialCooldown + ")" : "Special");
        fatalityButton.setEnabled(playerFatalityCharge >= 10);

        // Update fatality bars
        playerFatalityBar.setProgress(playerFatalityCharge * 10);
        enemyFatalityBar.setProgress(enemyFatalityCharge * 10);

        // Set images
        int playerImageRes = getResources().getIdentifier(playerLutemon.getImageResource(), "drawable", requireContext().getPackageName());
        int enemyImageRes = getResources().getIdentifier(enemyLutemon.getImageResource(), "drawable", requireContext().getPackageName());

        playerImage.setImageResource(playerImageRes != 0 ? playerImageRes : R.drawable.placeholder_image);
        enemyImage.setImageResource(enemyImageRes != 0 ? enemyImageRes : R.drawable.placeholder_image);
    }

    private void addToBattleLog(String message) {
        battleLog.append(message + "\n");
        // Auto-scroll to bottom
        final int scrollAmount = battleLog.getLayout().getLineTop(battleLog.getLineCount()) - battleLog.getHeight();
        if (scrollAmount > 0) {
            battleLog.scrollTo(0, scrollAmount);
        } else {
            battleLog.scrollTo(0, 0);
        }
    }
}