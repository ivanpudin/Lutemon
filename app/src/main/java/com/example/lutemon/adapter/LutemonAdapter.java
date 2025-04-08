package com.example.lutemon.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lutemon.R;
import com.example.lutemon.model.Lutemon;
import com.example.lutemon.util.ErrorHandler;

import java.util.ArrayList;
import java.util.List;

public class LutemonAdapter extends RecyclerView.Adapter<LutemonAdapter.LutemonViewHolder> {
    private final Context context;
    private final List<Lutemon> lutemons;

    public LutemonAdapter(Context context, List<Lutemon> lutemonsList) {
        this.context = context;
        this.lutemons = new ArrayList<>();

        lutemons.addAll(lutemonsList);
    }

    @NonNull
    @Override
    public LutemonAdapter.LutemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view = inflater.inflate(R.layout.lutemon_card, parent, false);
        return new LutemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LutemonAdapter.LutemonViewHolder holder, int position) {
        try {
            Lutemon lutemon = lutemons.get(position);

            holder.getCardViewName().setText(lutemon.getName());

            String attackDice = lutemon.getAttackCount() + "d" + lutemon.getAttackDice();
            holder.getCardViewAttack().setText(attackDice);

            String currentHealth = lutemon.getCurrentHealth() + "/" + lutemon.getMaxHealth();
            holder.getCardViewHealth().setText(currentHealth);

            // Image handling
            try {
                // Try to find image, if failed set placeholder image from local res/drawable
                int imageResourceId = ErrorHandler.getDrawableResourceId(
                        context, lutemon.getImageResource(), R.drawable.placeholder_image);
                holder.getCardImageView().setImageResource(imageResourceId);
            } catch (Exception e) {
                // if all failed, set default background image to some color
                holder.getCardImageView().setBackgroundColor(
                        context.getResources().getColor(android.R.color.darker_gray));
                ErrorHandler.logError("Lutemon", "Error loading image", e);
            }
        } catch (Exception e) {
            ErrorHandler.logError("LutemonAdapter", "Error binding view at position " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return lutemons != null ? lutemons.size() : 0;
    }

    public static class LutemonViewHolder extends RecyclerView.ViewHolder {
        private final ImageView cardImageView;
        private final TextView cardViewAttack, cardViewHealth, cardViewName;

        public LutemonViewHolder(@NonNull View itemView) {
            super(itemView);

            cardImageView = itemView.findViewById(R.id.cardImageView);
            cardViewAttack = itemView.findViewById(R.id.cardViewAttack);
            cardViewHealth = itemView.findViewById(R.id.cardViewHealth);
            cardViewName = itemView.findViewById(R.id.cardViewName);
        }

        public ImageView getCardImageView() {
            return cardImageView;
        }

        public TextView getCardViewAttack() {
            return cardViewAttack;
        }

        public TextView getCardViewHealth() {
            return cardViewHealth;
        }

        public TextView getCardViewName() {
            return cardViewName;
        }
    }
}
