package com.example.lutemon;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import androidx.fragment.app.Fragment;

import com.example.lutemon.model.LevelingSystem;
import com.example.lutemon.model.Lutemon;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BattleArenaFragment extends Fragment {

    private Lutemon playerLutemon;
    private Lutemon aiLutemon;

    private TextView playerNameText;
    private TextView aiNameText;
    private ProgressBar playerHealthBar;
    private ProgressBar aiHealthBar;
    private TextView playerHealthText;
    private TextView aiHealthText;
    private ImageView playerImage;
    private ImageView aiImage;
    private TextView battleLogText;
    private Button attackButton;
    private Button defendButton;
    private Button retreatButton;
    private View battleResultLayout;
    private Button newBattleButton;
    private Button homeButton;

    private Random random = new Random();
    private boolean isPlayerTurn = true;
    private boolean isBattleActive = false;
    private AtomicBoolean isAnimating = new AtomicBoolean(false);
    private Handler handler = new Handler(Looper.getMainLooper());

    public BattleArenaFragment() {
        // Required empty public constructor
    }

    public static BattleArenaFragment newInstance(Lutemon playerLutemon, Lutemon aiLutemon) {
        BattleArenaFragment fragment = new BattleArenaFragment();
        Bundle args = new Bundle();
        args.putSerializable("playerLutemon", playerLutemon);
        args.putSerializable("aiLutemon", aiLutemon);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            playerLutemon = (Lutemon) getArguments().getSerializable("playerLutemon");
            aiLutemon = (Lutemon) getArguments().getSerializable("aiLutemon");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_battle_arena, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initializeViews(view);

        if (playerLutemon == null || aiLutemon == null) {
            Toast.makeText(getContext(), "Error loading Lutemons", Toast.LENGTH_SHORT).show();
            // Navigate back to previous screen
            getParentFragmentManager().popBackStack();
            return;
        }

        setupBattle();

        attackButton.setOnClickListener(v -> {
            if (isBattleActive && isPlayerTurn && !isAnimating.get()) {
                performAttack(playerLutemon, aiLutemon, true);
            }
        });

        defendButton.setOnClickListener(v -> {
            if (isBattleActive && isPlayerTurn && !isAnimating.get()) {
                defendPlayer();
            }
        });

        retreatButton.setOnClickListener(v -> {
            if (!isAnimating.get()) {
                endBattle(false);
            }
        });
    }

    private void initializeViews(View view) {
        playerNameText = view.findViewById(R.id.playerLutemonName);
        aiNameText = view.findViewById(R.id.aiLutemonName);
        playerHealthBar = view.findViewById(R.id.playerHealthBar);
        aiHealthBar = view.findViewById(R.id.aiHealthBar);
        playerHealthText = view.findViewById(R.id.playerHealthText);
        aiHealthText = view.findViewById(R.id.aiHealthText);
        playerImage = view.findViewById(R.id.playerLutemonImage);
        aiImage = view.findViewById(R.id.aiLutemonImage);
        battleLogText = view.findViewById(R.id.battleLogText);
        attackButton = view.findViewById(R.id.attackButton);
        defendButton = view.findViewById(R.id.defendButton);
        retreatButton = view.findViewById(R.id.retreatButton);
        battleResultLayout = view.findViewById(R.id.battleResultLayout);
        newBattleButton = view.findViewById(R.id.newBattleButton);
        homeButton = view.findViewById(R.id.homeButton);
    }

    private void setupBattle() {
        // Reset Lutemons to full health
        playerLutemon.heal();
        aiLutemon.heal();

        // Set UI elements
        playerNameText.setText(playerLutemon.getName());
        aiNameText.setText(aiLutemon.getName());

        updateHealthDisplay();

        // Load images
        int playerImgResId = getResources().getIdentifier(
                playerLutemon.getImageResource(), "drawable", requireContext().getPackageName());
        int aiImgResId = getResources().getIdentifier(
                aiLutemon.getImageResource(), "drawable", requireContext().getPackageName());

        playerImage.setImageResource(playerImgResId != 0 ? playerImgResId : R.drawable.placeholder_image);
        aiImage.setImageResource(aiImgResId != 0 ? aiImgResId : R.drawable.placeholder_image);

        // Start battle
        isPlayerTurn = random.nextBoolean(); // Randomly determine who goes first
        isBattleActive = true;

        appendToBattleLog("Battle begins between " + playerLutemon.getName() +
                " and " + aiLutemon.getName() + "!");

        if (!isPlayerTurn) {
            appendToBattleLog(aiLutemon.getName() + " goes first!");
            handler.postDelayed(this::performAiTurn, 1500);
        } else {
            appendToBattleLog(playerLutemon.getName() + " goes first!");
            appendToBattleLog("Your turn! Choose your action.");
        }

        updateButtonStates();
    }

    private void updateHealthDisplay() {
        playerHealthBar.setMax(playerLutemon.getMaxHealth());
        playerHealthBar.setProgress(playerLutemon.getCurrentHealth());
        playerHealthText.setText(playerLutemon.getCurrentHealth() + "/" + playerLutemon.getMaxHealth());

        aiHealthBar.setMax(aiLutemon.getMaxHealth());
        aiHealthBar.setProgress(aiLutemon.getCurrentHealth());
        aiHealthText.setText(aiLutemon.getCurrentHealth() + "/" + aiLutemon.getMaxHealth());
    }

    private void performAttack(Lutemon attacker, Lutemon defender, boolean isPlayerAttacking) {
        isAnimating.set(true);
        updateButtonStates();

        appendToBattleLog(attacker.getName() + " attacks!");

        // Calculate attack damage using dice rolls
        int totalDamage = 0;
        StringBuilder damageLog = new StringBuilder();

        for (int i = 0; i < attacker.getAttackCount(); i++) {
            int roll = random.nextInt(attacker.getAttackDice()) + 1;
            totalDamage += roll;
            damageLog.append("Roll ").append(i + 1).append(": ").append(roll);

            if (i < attacker.getAttackCount() - 1) {
                damageLog.append(", ");
            }
        }

        // Apply defender's defense
        int finalDamage = Math.max(1, totalDamage - defender.getDefense());

        // Apply damage
        int newHealth = Math.max(0, defender.getCurrentHealth() - finalDamage);
        defender.setCurrentHealth(newHealth);

        appendToBattleLog(damageLog.toString());
        appendToBattleLog(attacker.getName() + " deals " + finalDamage + " damage!");

        // Animate the attack
        ImageView attackerImage = isPlayerAttacking ? playerImage : aiImage;
        float moveDirection = isPlayerAttacking ? 100f : -100f;

        attackerImage.animate()
                .translationXBy(moveDirection)
                .setDuration(200)
                .withEndAction(() ->
                        attackerImage.animate()
                                .translationXBy(-moveDirection)
                                .setDuration(200)
                                .start()
                )
                .start();

        handler.postDelayed(() -> {
            updateHealthDisplay();

            if (defender.getCurrentHealth() <= 0) {
                handleVictory(attacker, defender);
            } else {
                // Switch turns
                isPlayerTurn = !isPlayerTurn;
                isAnimating.set(false);
                updateButtonStates();

                if (isPlayerTurn) {
                    appendToBattleLog("Your turn! Choose your action.");
                } else {
                    appendToBattleLog(aiLutemon.getName() + "'s turn!");
                    handler.postDelayed(this::performAiTurn, 1500);
                }
            }
        }, 500);
    }

    private void defendPlayer() {
        isAnimating.set(true);
        updateButtonStates();

        // Temporarily boost defense
        int originalDefense = playerLutemon.getDefense();
        int defenseBoost = originalDefense / 2;
        playerLutemon.setDefense(originalDefense + defenseBoost);

        appendToBattleLog(playerLutemon.getName() + " takes a defensive stance!");
        appendToBattleLog("Defense increased by " + defenseBoost + " for this turn!");

        // End player's turn
        isPlayerTurn = false;

        handler.postDelayed(() -> {
            isAnimating.set(false);
            updateButtonStates();
            appendToBattleLog(aiLutemon.getName() + "'s turn!");

            // AI takes turn after a short delay
            handler.postDelayed(() -> {
                performAiTurn();

                // Reset defense after AI's turn
                handler.postDelayed(() -> {
                    playerLutemon.setDefense(originalDefense);
                    appendToBattleLog(playerLutemon.getName() + "'s defense returns to normal.");
                }, 500);

            }, 1000);
        }, 500);
    }

    private void performAiTurn() {
        isAnimating.set(true);
        updateButtonStates();

        // Simple AI: 80% chance to attack, 20% chance to defend
        boolean aiAttacks = random.nextInt(10) < 8;

        if (aiAttacks) {
            performAttack(aiLutemon, playerLutemon, false);
        } else {
            // AI defends
            appendToBattleLog(aiLutemon.getName() + " takes a defensive stance!");

            // Temporarily boost defense for next turn
            int originalDefense = aiLutemon.getDefense();
            int defenseBoost = originalDefense / 2;
            aiLutemon.setDefense(originalDefense + defenseBoost);

            appendToBattleLog(aiLutemon.getName() + "'s defense increased by " + defenseBoost + " for the next turn!");

            handler.postDelayed(() -> {
                // Switch turns
                isPlayerTurn = true;
                isAnimating.set(false);
                updateButtonStates();
                appendToBattleLog("Your turn! Choose your action.");

                // Schedule defense reset after player's turn
                handler.postDelayed(() -> {
                    aiLutemon.setDefense(originalDefense);
                    appendToBattleLog(aiLutemon.getName() + "'s defense returns to normal.");
                }, 2000);
            }, 500);
        }
    }

    private void handleVictory(Lutemon winner, Lutemon loser) {
        isBattleActive = false;
        isAnimating.set(true);

        appendToBattleLog(winner.getName() + " has won the battle!");

        // Add experience to winner
        winner.setExperience(winner.getExperience() + 1);
        appendToBattleLog(winner.getName() + " gained 1 experience point!");

        // Reset loser
        loser.heal();

        // Update storage with battle results
        LevelingSystem.rewardWin(winner);
        LevelingSystem.rewardLoss(loser);

        // End battle after a delay
        handler.postDelayed(() -> {
            isAnimating.set(false);
            updateButtonStates();

            // Show result buttons
            battleResultLayout.setVisibility(View.VISIBLE);

            newBattleButton.setOnClickListener(v -> {
                // Return to lutemon selection screen
                getParentFragmentManager().popBackStack();
            });

            homeButton.setOnClickListener(v -> {
                // Return to home screen (clear back stack)
                getParentFragmentManager().popBackStack(null, 0);
            });
        }, 2000);
    }

    private void endBattle(boolean complete) {
        if (!complete) {
            appendToBattleLog("Battle ended early!");
        }

        // Ensure both Lutemons are healed
        playerLutemon.heal();
        aiLutemon.heal();

        // Return to previous screen
        getParentFragmentManager().popBackStack();
    }

    private void appendToBattleLog(String message) {
        if (battleLogText != null) {
            battleLogText.append(message + "\n\n");

            // Auto scroll to bottom
            if (battleLogText.getLayout() != null) {
                final int scrollAmount = battleLogText.getLayout().getLineTop(battleLogText.getLineCount()) - battleLogText.getHeight();
                if (scrollAmount > 0) {
                    battleLogText.scrollTo(0, scrollAmount);
                } else {
                    battleLogText.scrollTo(0, 0);
                }
            }
        }
    }

    private void updateButtonStates() {
        boolean enabled = isBattleActive && isPlayerTurn && !isAnimating.get();
        attackButton.setEnabled(enabled);
        defendButton.setEnabled(enabled);

        // Retreat is always available unless animating
        retreatButton.setEnabled(!isAnimating.get());
    }
}