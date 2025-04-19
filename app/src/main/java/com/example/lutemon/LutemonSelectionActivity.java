package com.example.lutemon;
import android.widget.ImageView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LutemonSelectionActivity extends AppCompatActivity {

    private ListView lutemonListView;
    private Button startBattleButton;
    private Button backButton;

    private Lutemon selectedLutemon = null;
    private List<Map<String, Object>> lutemonDataList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lutemon_selection);

        lutemonListView = findViewById(R.id.lutemonListView);
        startBattleButton = findViewById(R.id.startBattleButton);
        backButton = findViewById(R.id.backButton);

        populateLutemonList();

        lutemonListView.setOnItemClickListener((parent, view, position, id) -> {
            // Deselect all items
            for (Map<String, Object> item : lutemonDataList) {
                item.put("isSelected", false);
            }

            // Select the clicked item
            lutemonDataList.get(position).put("isSelected", true);
            selectedLutemon = (Lutemon) lutemonDataList.get(position).get("lutemon");

            // Refresh list view
            ((SimpleAdapter) lutemonListView.getAdapter()).notifyDataSetChanged();

            // Enable start button
            startBattleButton.setEnabled(true);
        });

        startBattleButton.setOnClickListener(v -> {
            if (selectedLutemon != null) {
                startBattle();
            } else {
                Toast.makeText(this, "Please select a Lutemon", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> finish());

        // Initially disable start button
        startBattleButton.setEnabled(false);
    }

    private void populateLutemonList() {
        List<Lutemon> lutemons = LutemonStorage.getInstance().getLutemons();
        lutemonDataList = new ArrayList<>();

        if (lutemons.isEmpty()) {
            Toast.makeText(this, "No Lutemons available. Create some first!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        for (Lutemon lutemon : lutemons) {
            Map<String, Object> item = new HashMap<>();
            item.put("lutemon", lutemon);
            item.put("name", lutemon.getName());
            item.put("element", "Element: " + lutemon.getElement());
            item.put("stats", "ATK: " + lutemon.getAttackDice() + "d" + lutemon.getAttackCount() +
                    " | DEF: " + lutemon.getDefense() +
                    " | HP: " + lutemon.getMaxHealth());
            item.put("experience", "XP: " + lutemon.getExperience());
            item.put("record", "W: " + LutemonStorage.getInstance().getLutemonById(lutemon.getId()).getWins() +
                    " | L: " + LutemonStorage.getInstance().getLutemonById(lutemon.getId()).getLosses());
            item.put("isSelected", false);

            // Get image resource ID
            int imageResId = getResources().getIdentifier(
                    lutemon.getImageResource(), "drawable", getPackageName());
            item.put("imageResId", imageResId != 0 ? imageResId : R.drawable.default_lutemon);

            lutemonDataList.add(item);
        }

        String[] from = {"name", "element", "stats", "experience", "record", "imageResId"};
        int[] to = {R.id.lutemonName, R.id.lutemonElement, R.id.lutemonStats,
                R.id.lutemonExperience, R.id.lutemonRecord, R.id.lutemonImage};

        SimpleAdapter adapter = new SimpleAdapter(this, lutemonDataList,
                R.layout.lutemon_selection_item, from, to) {
            public void setViewImage(ImageView v, Object value) {
                v.setImageResource((Integer) value);
            }

            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                boolean isSelected = (boolean) lutemonDataList.get(position).get("isSelected");
                view.setBackgroundColor(isSelected ?
                        getResources().getColor(R.color.selected_item) :
                        getResources().getColor(android.R.color.transparent));

                return view;
            }
        };

        lutemonListView.setAdapter(adapter);
    }

    private void startBattle() {
        // Choose a random AI opponent from the available Lutemons
        List<Lutemon> allLutemons = LutemonStorage.getInstance().getLutemons();
        List<Lutemon> possibleOpponents = new ArrayList<>();

        for (Lutemon lutemon : allLutemons) {
            if (lutemon.getId() != selectedLutemon.getId()) {
                possibleOpponents.add(lutemon);
            }
        }

        if (possibleOpponents.isEmpty()) {
            Toast.makeText(this, "No opponents available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Select random opponent
        Lutemon aiLutemon = possibleOpponents.get(new Random().nextInt(possibleOpponents.size()));

        // Start battle activity
        Intent intent = new Intent(this, BattleArenaFragment.class);
        intent.putExtra("playerLutemon", selectedLutemon);
        intent.putExtra("aiLutemon", aiLutemon);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            // Refresh the list to update stats after battle
            populateLutemonList();
        }
    }
}