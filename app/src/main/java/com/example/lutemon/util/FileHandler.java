package com.example.lutemon.util;

import android.content.Context;
import android.widget.Toast;

import com.example.lutemon.model.CurrencyManager;
import com.example.lutemon.model.Lutemon;
import com.example.lutemon.model.LutemonSaveData;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileHandler {
    private static final String FILE_NAME = "lutemons_data.dat";

    public static boolean saveLutemonsAndCurrency(Context context, LutemonSaveData saveData) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(saveData);
            Toast.makeText(context, "Lutemons and currency saved successfully!", Toast.LENGTH_SHORT).show();
            return true;
        } catch (Exception e) {
            ErrorHandler.handleError(context, e, "Failed to save Lutemons and currency.");
            return false;
        }
    }

    public static LutemonSaveData loadLutemonsAndCurrency(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            LutemonSaveData data = (LutemonSaveData) ois.readObject();
            Toast.makeText(context, "Lutemons and currency loaded successfully!", Toast.LENGTH_SHORT).show();
            return data;
        } catch (Exception e) {
            ErrorHandler.handleError(context, e, "Failed to load Lutemons and currency.");
            int currentCurrency = CurrencyManager.getInstance().getCurrency();
            return new LutemonSaveData(new ArrayList<>(), currentCurrency);
        }
    }
}