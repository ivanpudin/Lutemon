package com.example.lutemon.model;

import java.lang.reflect.Field;
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

    public void removeLutemonById(int id) {
        lutemons.removeIf(l -> l.getId() == id);
    }

    public List<Lutemon> getLutemons() {
        return lutemons;
    }

    public void setLutemons(ArrayList<Lutemon> newLutemons) {
        lutemons = newLutemons;

        // Reset nextId to avoid ID collisions
        int maxId = newLutemons.stream().mapToInt(Lutemon::getId).max().orElse(0);
        try {
            Field nextIdField = Lutemon.class.getDeclaredField("nextId");
            nextIdField.setAccessible(true);
            nextIdField.setInt(null, maxId + 1);
        } catch (Exception e) {
            e.printStackTrace(); // Log
        }
    }
}
