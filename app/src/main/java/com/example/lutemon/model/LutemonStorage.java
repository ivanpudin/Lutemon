package com.example.lutemon.model;

import java.util.ArrayList;
import java.util.List;

public class LutemonStorage {
    private List<Lutemon> lutemons;

    public LutemonStorage() {
        this.lutemons = new ArrayList<>();
    }

    public void addLutemon(Lutemon lutemon) {
        lutemons.add(lutemon);
    }
}
