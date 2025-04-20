package com.example.lutemon;

import android.os.Bundle;

import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonStorage;
import com.example.lutemon.util.ErrorHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class LutemonStatisticActivity extends AppCompatActivity {
    // Intent extras
    public static final String EXTRA_LUTEMON_ID = "extra_lutemon_id";
    private ImageView statisticViewImage, statisticViewElement;
    private TextView statisticViewName, statisticViewAttack, statisticViewHealth, statisticViewDefence, statisticViewExp, abilityName1, abilityName2, abilityDesc1, abilityDesc2;
    private FloatingActionButton backButton, deleteButton;
    private Lutemon lutemon;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lutemon_statistic);

        statisticViewImage = findViewById(R.id.statisticViewImage);
        statisticViewElement = findViewById(R.id.statisticViewElement);
        statisticViewName = findViewById(R.id.statisticViewName);
        statisticViewAttack = findViewById(R.id.statisticViewAttack);
        statisticViewHealth = findViewById(R.id.statisticViewHealth);
        statisticViewDefence = findViewById(R.id.statisticViewDefence);
        statisticViewExp = findViewById(R.id.statisticViewExp);
        backButton = findViewById(R.id.statisticViewBackButton);
        deleteButton = findViewById(R.id.statisticViewDeleteButton);
        abilityName1 = findViewById(R.id.statisticViewText1);
        abilityDesc1 = findViewById(R.id.statisticViewText2);
        abilityName2 = findViewById(R.id.statisticViewText3);
        abilityDesc2 = findViewById(R.id.statisticViewText4);

        displayLutemonDetails();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        deleteButton.setOnClickListener(v -> {
            if (lutemon != null) {
                LutemonStorage.getInstance().removeLutemonById(lutemon.getId());
                Toast.makeText(this, "Lutemon deleted.", Toast.LENGTH_SHORT).show();

                setResult(RESULT_OK);
                finish(); // Close activity
            } else {
                Toast.makeText(this, "Failed to delete lutemon.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayLutemonDetails() {
        try {
            // Get Lutemon ID from the intent
            int lutemonId = getIntent().getIntExtra(EXTRA_LUTEMON_ID, -1);
            if (lutemonId == -1) {
                throw new IllegalArgumentException("Lutemon ID not found");
            }

            // Find the Lutemon using the ID
            lutemon = LutemonStorage.getInstance().getLutemonById(lutemonId);

            // Display lutemon details
            statisticViewName.setText(lutemon.getName());
            String attackDice = lutemon.getAttackCount() + "d" + lutemon.getAttackDice();
            statisticViewAttack.setText(attackDice);
            String currentHealth = lutemon.getCurrentHealth() + "/" + lutemon.getMaxHealth();
            statisticViewHealth.setText(currentHealth);
            statisticViewDefence.setText(String.valueOf(lutemon.getDefense()));
            statisticViewExp.setText(String.valueOf(lutemon.getExperience()));

            // Load image
            try {
                int imageResourceId = ErrorHandler.getDrawableResourceId(
                        this, lutemon.getImageResource(), R.drawable.placeholder_image);
                statisticViewImage.setImageResource(imageResourceId);
            } catch (Exception e) {
                // If all else fails, set a background color
                statisticViewImage.setBackgroundColor(
                        getResources().getColor(android.R.color.darker_gray));
            }

            try {
                int imageResourceId = ErrorHandler.getDrawableResourceId(
                        this, lutemon.getElementIconResource(), R.drawable.element_not_found);
                statisticViewElement.setImageResource(imageResourceId);
            } catch (Exception e) {
                // If all else fails, set a background color
                statisticViewElement.setBackgroundColor(
                        getResources().getColor(android.R.color.darker_gray));
            }
            abilityName1.setText(lutemon.getElement());
            switch (lutemon.getElement()) {
                case "Fire":
                    abilityDesc1.setText("Sets a Lutemon on fire, dealing 2 damage per turn for 3 turns");
                    break;

                case "Water":
                    abilityDesc1.setText("Deals 6 damage to the enemy, while soaking both Lutemon");
                    break;

                case "Ice":
                    abilityDesc1.setText("Deals 4 damage and freezes enemy Lutemon for 1 turn");
                    break;

                case "Wind":
                    abilityDesc1.setText("Deals 2 damage per turn for 3 turns");
                    break;

                case "Earth":
                    abilityDesc1.setText("Deals 2 damage per turn for 3 turns");
                    break;

                case "Thunder":
                    abilityDesc1.setText("Deals 2 damage per turn for 3 turns");
                    break;
            }

            // Set unique ability
            abilityName2.setText("Fatality");
            abilityDesc2.setText("A risky move that could win or lose the game for the Lutemon using it.");

        } catch (Exception e) {
            ErrorHandler.handleError(
                    this,
                    e,
                    "Failed to display item details. Please try again."
            );
            // Finish activity if we can't display details
            finish();
        }
    }
}