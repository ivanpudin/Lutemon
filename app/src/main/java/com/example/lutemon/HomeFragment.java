package com.example.lutemon;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lutemon.adapter.LutemonAdapter;
import com.example.lutemon.model.CurrencyManager;
import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonSaveData;
import com.example.lutemon.model.LutemonStorage;
import com.example.lutemon.model.Thunder;
import com.example.lutemon.util.ErrorHandler;
import com.example.lutemon.util.FileHandler;

import java.util.ArrayList;
import java.util.List;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import android.app.Activity;

public class HomeFragment extends Fragment implements LutemonAdapter.OnItemClickListener {
    private List<Lutemon> lutemons;
    private RecyclerView homeRecyclerView;
    private LutemonAdapter lutemonAdapter;
    private TextView missingDataTextView;
    private final ActivityResultLauncher<Intent> customLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    // Refresh the lutemon list and adapter after deletion
                    lutemons = LutemonStorage.getInstance().getLutemons();
                    lutemonAdapter = new LutemonAdapter(getContext(), lutemons, this);
                    homeRecyclerView.setAdapter(lutemonAdapter);
                    updateVisibility();
                }
            });

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        lutemons = LutemonStorage.getInstance().getLutemons();

        homeRecyclerView = rootView.findViewById(R.id.homeRecyclerView);
        missingDataTextView = rootView.findViewById(R.id.missingDataTextView);

        homeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        lutemonAdapter = new LutemonAdapter(getContext(), lutemons, this);
        homeRecyclerView.setAdapter(lutemonAdapter);

        updateVisibility();

        // Set up FloatingActionButton click listener
        rootView.findViewById(R.id.addLutemonButton).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddLutemonActivity.class);

            customLauncher.launch(intent);
        });

        rootView.findViewById(R.id.saveLutemonsButton).setOnClickListener(v -> {
            LutemonSaveData saveData = new LutemonSaveData(
                    new ArrayList<>(lutemons),
                    CurrencyManager.getInstance().getCurrency()
            );
            FileHandler.saveLutemonsAndCurrency(getContext(), saveData);
        });

        rootView.findViewById(R.id.loadLutemonsButton).setOnClickListener(v -> {
            LutemonSaveData loadedData = FileHandler.loadLutemonsAndCurrency(getContext());
            LutemonStorage.getInstance().setLutemons(new ArrayList<>(loadedData.getLutemons()));
            CurrencyManager.getInstance().setCurrency(loadedData.getCurrency());

            // Refresh fragment
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new HomeFragment());
            fragmentTransaction.commit();
        });

        return rootView;
    }

    // Render TextView with missing data message in case lutemon list is empty
    private void updateVisibility() {
        if (lutemons.isEmpty()) {
            homeRecyclerView.setVisibility(View.GONE);
            missingDataTextView.setVisibility(View.VISIBLE);
        } else {
            homeRecyclerView.setVisibility(View.VISIBLE);
            missingDataTextView.setVisibility(View.GONE);
        }
    }

    public void onItemClick(int position) {
        try {
            Lutemon lutemon = lutemonAdapter.getLutemon(position);
            if (lutemon != null) {
                // Launch detail activity
                Intent intent = new Intent(requireContext(), LutemonStatisticActivity.class);
                intent.putExtra(LutemonStatisticActivity.EXTRA_LUTEMON_ID, lutemon.getId());

                customLauncher.launch(intent);
            }
        } catch (Exception e) {
            ErrorHandler.handleError(
                    requireContext(),
                    e,
                    "Failed to open item details. Please try again."
            );
        }
    }
}