package com.example.lutemon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class LutemonSelectionFragment extends Fragment {

    private ListView lutemonListView;
    private Button startBattleButton;
    private Button backButton;

    private Lutemon selectedLutemon = null;
    private List<Map<String, Object>> lutemonDataList;

    public LutemonSelectionFragment() {
        // Required empty public constructor
    }

    public static LutemonSelectionFragment newInstance() {
        return new LutemonSelectionFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lutemon_selection, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        lutemonListView = view.findViewById(R.id.lutemonListView);
        startBattleButton = view.findViewById(R.id.startBattleButton);
        backButton = view.findViewById(R.id.backButton);

        populateLutemonList();

        lutemonListView.setOnItemClickListener((parent, v, position, id) -> {
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
                Toast.makeText(requireContext(), "Please select a Lutemon", Toast.LENGTH_SHORT).show();
            }
        });

        backButton.setOnClickListener(v -> {
            // Navigate back to previous fragment
            getParentFragmentManager().popBackStack();
        });

        // Initially disable start button
        startBattleButton.setEnabled(false);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh the list when returning to this fragment
        populateLutemonList();
    }

    private void populateLutemonList() {
        List<Lutemon> lutemons = LutemonStorage.getInstance().getLutemons();
        lutemonDataList = new ArrayList<>();

        if (lutemons.isEmpty()) {
            Toast.makeText(requireContext(), "No Lutemons available. Create some first!", Toast.LENGTH_LONG).show();
            getParentFragmentManager().popBackStack();
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
            int imageResId = requireContext().getResources().getIdentifier(
                    lutemon.getImageResource(), "drawable", requireContext().getPackageName());
            item.put("imageResId", imageResId != 0 ? imageResId : R.drawable.default_lutemon);

            lutemonDataList.add(item);
        }

        String[] from = {"name", "element", "stats", "experience", "record", "imageResId"};
        int[] to = {R.id.lutemonName, R.id.lutemonElement, R.id.lutemonStats,
                R.id.lutemonExperience, R.id.lutemonRecord, R.id.lutemonImage};

        SimpleAdapter adapter = new SimpleAdapter(requireContext(), lutemonDataList,
                R.layout.lutemon_selection_item, from, to) {
            public void setViewImage(ImageView v, Object value) {
                v.setImageResource((Integer) value);
            }

            @Override
            public View getView(int position, View convertView, android.view.ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                boolean isSelected = (boolean) lutemonDataList.get(position).get("isSelected");
                view.setBackgroundColor(isSelected ?
                        requireContext().getResources().getColor(R.color.selected_item) :
                        requireContext().getResources().getColor(android.R.color.transparent));

                return view;
            }
        };

        lutemonListView.setAdapter(adapter);

        // Reset selection
        selectedLutemon = null;
        startBattleButton.setEnabled(false);
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
            Toast.makeText(requireContext(), "No opponents available", Toast.LENGTH_SHORT).show();
            return;
        }

        // Select random opponent
        Lutemon aiLutemon = possibleOpponents.get(new Random().nextInt(possibleOpponents.size()));

        // Create a new battle arena fragment and pass the Lutemons
        BattleArenaFragment battleFragment = BattleArenaFragment.newInstance(selectedLutemon, aiLutemon);

        // Navigate to the battle fragment
        getParentFragmentManager().beginTransaction()
                .replace(R.id.frameLayout, battleFragment)
                .addToBackStack(null)
                .commit();
    }
}