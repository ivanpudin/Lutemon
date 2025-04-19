package com.example.lutemon.model;

import java.io.Serializable;
import java.util.List;

public class LutemonSaveData implements Serializable {
    private List<Lutemon> lutemons;
    private int currency;

    public LutemonSaveData(List<Lutemon> lutemons, int currency) {
        this.lutemons = lutemons;
        this.currency = currency;
    }

    public List<Lutemon> getLutemons() {
        return lutemons;
    }

    public void setLutemons(List<Lutemon> lutemons) {
        this.lutemons = lutemons;
    }

    public int getCurrency() {
        return currency;
    }

    public void setCurrency(int currency) {
        this.currency = currency;
    }
}