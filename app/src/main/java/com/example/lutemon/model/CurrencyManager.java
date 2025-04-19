package com.example.lutemon.model;

import java.util.ArrayList;
import java.util.List;

// Class to hold and dynamically update app currency
public class CurrencyManager {
    private static CurrencyManager instance;
    private int currency = 1000;

    public interface CurrencyChangeListener {
        void onCurrencyChanged(int newCurrency);
    }

    private final List<CurrencyChangeListener> listeners = new ArrayList<>();

    private CurrencyManager() {}

    public static CurrencyManager getInstance() {
        if (instance == null) {
            instance = new CurrencyManager();
        }
        return instance;
    }

    public int getCurrency() {
        return currency;
    }

    public void addCurrency(int amount) {
        currency += amount;
        notifyListeners();
    }

    public void subtractCurrency(int amount) {
        currency -= amount;
        notifyListeners();
    }

    public void setCurrency(int amount) {
        currency = amount;
        notifyListeners();
    }

    public void addListener(CurrencyChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(CurrencyChangeListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners() {
        for (CurrencyChangeListener listener : listeners) {
            listener.onCurrencyChanged(currency);
        }
    }
}