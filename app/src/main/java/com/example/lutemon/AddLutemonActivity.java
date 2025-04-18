package com.example.lutemon;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lutemon.model.Earth;
import com.example.lutemon.model.Fire;
import com.example.lutemon.model.Ice;
import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonStorage;
import com.example.lutemon.model.Thunder;
import com.example.lutemon.model.Water;
import com.example.lutemon.model.Wind;
import com.example.lutemon.util.ErrorHandler;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AddLutemonActivity extends AppCompatActivity {
    private EditText inputText;
    private RadioGroup elementRadioGroup;
    private Button submitButton;
    private Lutemon lutemon;
    private FloatingActionButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lutemon);

        inputText = findViewById(R.id.inputText);
        elementRadioGroup = findViewById(R.id.elementRadioGroup);
        submitButton = findViewById(R.id.submitButton);
        backButton = findViewById(R.id.addLutemonViewBackButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Validate form fields
                String lutemonName = inputText.getText().toString().trim();

                // Check if the name is empty
                if (lutemonName.isEmpty()) {
                    Toast.makeText(AddLutemonActivity.this, "Please enter Lutemon name", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Check which radio button is selected
                int selectedElementId = elementRadioGroup.getCheckedRadioButtonId();
                if (selectedElementId == -1) {
                    // If no element is selected
                    Toast.makeText(AddLutemonActivity.this, "Please select an element", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Get the selected RadioButton
                RadioButton selectedRadioButton = findViewById(selectedElementId);
                String selectedElement = selectedRadioButton.getText().toString();

                // Call the corresponding class based on the selected element
                try {
                    callElementMethod(selectedElement, lutemonName);
                    LutemonStorage.getInstance().addLutemon(lutemon);
                    Toast.makeText(AddLutemonActivity.this, "Lutemon created successfully", Toast.LENGTH_SHORT).show();

                    setResult(RESULT_OK);
                    finish(); // End the activity after successful Lutemon creation
                } catch (Exception e) {
                    ErrorHandler.handleError(AddLutemonActivity.this, e, "An error occurred while creating Lutemon.");
                }
            }
        });
    }

    // Call the designated class method based on selected element
    private void callElementMethod(String element, String lutemonName) {
        switch (element) {
            case "Fire":
                lutemon = new Fire(lutemonName, 8, 1, 40, 1, "fire_lutemon");
                break;
            case "Ice":
                lutemon = new Ice(lutemonName, 6, 1, 50, 2, "ice_lutemon");
                break;
            case "Thunder":
                lutemon = new Thunder(lutemonName, 8, 1, 40, 1, "thunder_lutemon");
                break;
            case "Wind":
                lutemon = new Wind(lutemonName, 4, 2, 25, 1, "wind_lutemon");
                break;
            case "Earth":
                lutemon = new Earth(lutemonName, 6, 1, 50, 3, "earth_lutemon");
                break;
            case "Water":
                lutemon = new Water(lutemonName, 6, 1, 40, 1, "water_lutemon");
                break;
            default:
                // If no matching element is found, show error and log the issue
                ErrorHandler.showError(this, "No element selected.");
                ErrorHandler.logError("AddLutemonActivity", "No element: " + element, null);
                throw new IllegalArgumentException("No element: " + element);
        }
    }
}