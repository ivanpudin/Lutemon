package com.example.lutemon;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.lutemon.adapter.LutemonRadioAdapter;
import com.example.lutemon.model.CurrencyManager;
import com.example.lutemon.model.LevelingSystem;
import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonStorage;

import java.util.List;

public class TrainingGroundFragment extends Fragment {
    private List<Lutemon> lutemons;
    private RecyclerView trainingGroundRecyclerView;
    private LutemonRadioAdapter lutemonRadioAdapter;
    private Button trainLutemonButton;

    private Lutemon selectedLutemon; // Track selected Lutemon

    public TrainingGroundFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_training_ground, container, false);
        lutemons = LutemonStorage.getInstance().getLutemons();

        trainingGroundRecyclerView = rootView.findViewById(R.id.trainingGroundRecyclerView);
        trainLutemonButton = rootView.findViewById(R.id.trainLutemonButton);

        trainingGroundRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lutemonRadioAdapter = new LutemonRadioAdapter(getContext(), lutemons);
        trainingGroundRecyclerView.setAdapter(lutemonRadioAdapter);

        // Set listener for Lutemon selection
        lutemonRadioAdapter.setOnLutemonSelectedListener(lutemon -> {
            selectedLutemon = lutemon;
            updateTrainButton(selectedLutemon);

            // Add listener to update training cost when it changes
            selectedLutemon.addTrainingCostChangeListener(new Lutemon.TrainingCostChangeListener() {
                @Override
                public void onTrainingCostChanged(int newTrainingCost) {
                    updateTrainButton(selectedLutemon);  // Update the button when the training cost changes
                }
            });
        });

        trainLutemonButton.setOnClickListener(v -> {
            if (selectedLutemon != null) {
                CurrencyManager currencyManager = CurrencyManager.getInstance();
                int currentCurrency = currencyManager.getCurrency();
                int trainingCost = selectedLutemon.getTrainingCost();

                if (currentCurrency >= trainingCost) {
                    currencyManager.subtractCurrency(trainingCost);
                    LevelingSystem.rewardTraining(selectedLutemon);

                    // After training, update the button text and training cost
                    updateTrainButton(selectedLutemon);

                    Toast.makeText(getContext(), "Trained: " + selectedLutemon.getName(), Toast.LENGTH_SHORT).show();
                } else {
                    // Not enough currency
                    Toast.makeText(getContext(), "Not enough currency to train Lutemon.", Toast.LENGTH_SHORT).show();
                }
            } else {
                // No Lutemon selected
                Toast.makeText(getContext(), "Please select a Lutemon to train!", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    // Method to update the button text and icon based on the selected Lutemon's training cost
    private void updateTrainButton(Lutemon selectedLutemon) {
        // Set the button text to the current training cost of the selected Lutemon
        trainLutemonButton.setText("Train Lutemon (" + selectedLutemon.getTrainingCost() + ")");

        // Set the coin icon next to the button text
        Drawable coinIcon = ContextCompat.getDrawable(requireContext(), R.drawable.coin_svgrepo_com);
        if (coinIcon != null) {
            int sizeInDp = 24;
            int sizeInPx = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    sizeInDp,
                    getResources().getDisplayMetrics()
            );
            coinIcon.setBounds(0, 0, sizeInPx, sizeInPx);
            trainLutemonButton.setCompoundDrawables(coinIcon, null, null, null);
            trainLutemonButton.setCompoundDrawablePadding(16); // Space between icon and text
        }
    }
}