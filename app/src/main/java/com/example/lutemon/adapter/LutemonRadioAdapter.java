package com.example.lutemon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lutemon.R;
import com.example.lutemon.model.Lutemon;
import com.example.lutemon.util.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class LutemonRadioAdapter extends RecyclerView.Adapter<LutemonRadioAdapter.LutemonRadioViewHolder> {
    public interface OnLutemonSelectedListener {
        void onLutemonSelected(Lutemon lutemon);
    }

    private OnLutemonSelectedListener listener;
    private final Context context;
    private final List<Lutemon> lutemons;
    private int selectedPosition = -1; // set initial position to none
    public void setOnLutemonSelectedListener(OnLutemonSelectedListener listener) {
        this.listener = listener;
    }

    public LutemonRadioAdapter(Context context, List<Lutemon> lutemonsList) {
        this.context = context;
        this.lutemons = new ArrayList<>(lutemonsList);
    }

    @NonNull
    @Override
    public LutemonRadioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lutemon_radio, parent, false);
        return new LutemonRadioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LutemonRadioViewHolder holder, int position) {
        Lutemon lutemon = lutemons.get(position);
        holder.getLutemonRadioText().setText(lutemon.getName());

        // Set the RadioButton checked status based on the selected position
        holder.getLutemonRadioButton().setChecked(position == selectedPosition);

        // Handle the click event to update the selected position
        holder.getLutemonRadioButton().setOnClickListener(v -> {
            int previousPosition = selectedPosition;
            selectedPosition = position;

            notifyItemChanged(previousPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onLutemonSelected(lutemons.get(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return lutemons.size();
    }

    public Lutemon getSelectedLutemon() {
        if (selectedPosition >= 0 && selectedPosition < lutemons.size()) {
            return lutemons.get(selectedPosition);
        }
        return null;
    }

    public static class LutemonRadioViewHolder extends RecyclerView.ViewHolder {
        private final RadioButton lutemonRadioButton;
        private final TextView lutemonRadioText;

        public LutemonRadioViewHolder(@NonNull View itemView) {
            super(itemView);
            lutemonRadioButton = itemView.findViewById(R.id.lutemonRadioButton);
            lutemonRadioText = itemView.findViewById(R.id.lutemonRadioText);
        }

        public RadioButton getLutemonRadioButton() {
            return lutemonRadioButton;
        }

        public TextView getLutemonRadioText() {
            return lutemonRadioText;
        }
    }
}