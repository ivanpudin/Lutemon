package com.example.lutemon.model;

import java.util.ArrayList;
import java.util.List;

// Use singleton pattern to have only one instance of lutemon storage across the app
public class LutemonStorage {
    private static LutemonStorage instance;
    private List<Lutemon> lutemons;

    private LutemonStorage() {
        this.lutemons = new ArrayList<>();
    }

    public static LutemonStorage getInstance() {
        if (instance == null) {
            instance = new LutemonStorage();
        }
        return instance;
    }

    public void addLutemon(Lutemon lutemon) {
        lutemons.add(lutemon);
    }

    public List<Lutemon> getLutemons() {
        return lutemons;
    }

    public void clearLutemons() {
        lutemons.clear();
    }
}
