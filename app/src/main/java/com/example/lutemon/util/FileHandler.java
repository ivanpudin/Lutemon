package com.example.lutemon.util;

import android.content.Context;

import com.example.lutemon.model.Lutemon;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;


public class FileHandler {
    private static final String FILE_NAME = "lutemons.data";

    public static void saveLutemons(Context context, ArrayList<Lutemon> lutemons) {
        try (FileOutputStream fos = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(lutemons);
        } catch (Exception e) {
            ErrorHandler.handleError(context, e, "Failed to save Lutemons.");
        }
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Lutemon> loadLutemons(Context context) {
        try (FileInputStream fis = context.openFileInput(FILE_NAME);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            return (ArrayList<Lutemon>) ois.readObject();
        } catch (Exception e) {
            ErrorHandler.handleError(context, e, "Failed to load Lutemons.");
            return new ArrayList<>(); // fallback
        }
    }
}
