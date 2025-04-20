package com.example.lutemon;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.widget.TextView;

import com.example.lutemon.databinding.ActivityMainBinding;
import com.example.lutemon.model.CurrencyManager;

public class MainActivity extends AppCompatActivity implements CurrencyManager.CurrencyChangeListener {
    ActivityMainBinding binding;
    private TextView appCurrencyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // App currency updates every time its changed
        appCurrencyTextView = binding.appCurrencyTextView;
        updateCurrencyDisplay(CurrencyManager.getInstance().getCurrency());

        CurrencyManager.getInstance().addListener(this);

        replaceFragment(new HomeFragment());

        // Switch between 3 main fragments through bottom nav menu
        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if(itemId==R.id.home) {
                replaceFragment(new HomeFragment());
            }
            else if (itemId==R.id.trainingGround) {
                replaceFragment(new TrainingGroundFragment());
            } else if (itemId == R.id.battleArena) {
                replaceFragment(new LutemonSelectionFragment());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    private void updateCurrencyDisplay(int newCurrency) {
        appCurrencyTextView.setText(String.valueOf(newCurrency));
    }

    @Override
    public void onCurrencyChanged(int newCurrency) {
        runOnUiThread(() -> updateCurrencyDisplay(newCurrency));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CurrencyManager.getInstance().removeListener(this);
    }
}