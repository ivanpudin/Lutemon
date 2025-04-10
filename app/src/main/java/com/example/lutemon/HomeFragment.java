package com.example.lutemon;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lutemon.adapter.LutemonAdapter;
import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonStorage;
import com.example.lutemon.model.Thunder;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {
    private List<Lutemon> lutemons;
    private RecyclerView homeRecyclerView;
    private LutemonAdapter lutemonAdapter;
    private TextView missingDataTextView;

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
        lutemonAdapter = new LutemonAdapter(getContext(), lutemons);
        homeRecyclerView.setAdapter(lutemonAdapter);

        updateVisibility();

        // Set up FloatingActionButton click listener
        rootView.findViewById(R.id.addLutemonButton).setOnClickListener(v -> {
            // int previousSize = LutemonStorage.getInstance().getLutemons().size();
            LutemonStorage.getInstance().addLutemon(new Thunder("Thunder Lutemon", 8, 1, 40, "thunder_lutemon"));

            // Refresh fragment after adding the lutemon
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frameLayout, new HomeFragment());
            fragmentTransaction.commit();

            // Does not update the recycler view for some reason...
            // Refresh the RecyclerView data after adding the lutemon
            // lutemons = LutemonStorage.getInstance().getLutemons();
            // lutemonAdapter.notifyItemInserted(previousSize);
            // lutemonAdapter.notifyDataSetChanged();
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
}