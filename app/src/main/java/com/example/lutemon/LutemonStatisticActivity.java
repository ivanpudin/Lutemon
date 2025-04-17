package com.example.lutemon;

import android.os.Bundle;

import com.example.lutemon.model.Lutemon;
import com.example.lutemon.util.ErrorHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;



public class LutemonStatisticActivity extends AppCompatActivity {
    // Intent extras
    public static final String EXTRA_LUTEMON = "extra_lutemon";
    private ImageView statisticViewImage;
    private TextView statisticViewAttack, statisticViewHealth;
    private FloatingActionButton backButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lutemon_statistic);

        statisticViewImage = findViewById(R.id.statisticViewImage);
        statisticViewAttack = findViewById(R.id.statisticViewAttack);
        statisticViewHealth = findViewById(R.id.statisticViewHealth);
        backButton = findViewById(R.id.statisticViewBackButton);

        displayLutemonDetails();

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void displayLutemonDetails() {
        try {
            // Get lutemon from the intent
            Lutemon lutemon = (Lutemon) getIntent().getSerializableExtra(EXTRA_LUTEMON);

            if (lutemon == null) {
                throw new IllegalArgumentException("Lutemon not found");
            }

            // Display lutemon details
            String attackDice = lutemon.getAttackCount() + "d" + lutemon.getAttackDice();
            statisticViewAttack.setText(attackDice);
            String currentHealth = lutemon.getCurrentHealth() + "/" + lutemon.getMaxHealth();
            statisticViewHealth.setText(currentHealth);

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