package com.example.lutemon.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;

// Adapted from Janne's code
public class ErrorHandler {
    private static final String TAG = "ErrorHandler";

    /**
     * Handles common errors that may occur in the app
     * @param context The context to show toast messages
     * @param throwable The exception that occurred
     * @param fallbackMessage Default message if error type isn't specifically handled
     * @return true if the error was handled, false otherwise
     */
    public static boolean handleError(@NonNull Context context, @Nullable Throwable throwable, String fallbackMessage) {
        if (throwable == null) {
            showError(context, fallbackMessage);
            return true;
        }

        Log.e(TAG, "Error occurred: " + throwable.getMessage(), throwable);

        // Handle specific error types
        if (throwable instanceof FileNotFoundException) {
            showError(context, "Required file was not found. Please reinstall the app.");
            return true;
        } else if (throwable instanceof JSONException) {
            showError(context, "Error reading lutemon data. Data format is invalid.");
            return true;
        } else if (throwable instanceof IOException) {
            showError(context, "Error accessing lutemon data. Please try again.");
            return true;
        } else if (throwable instanceof Resources.NotFoundException) {
            showError(context, "Required resource was not found. Please reinstall the app.");
            return true;
        } else if (throwable instanceof NullPointerException) {
            showError(context, "An unexpected error occurred. Please try again.");
            return true;
        } else if (throwable instanceof IndexOutOfBoundsException) {
            showError(context, "Invalid data index. Please report this issue.");
            return true;
        } else if (throwable instanceof IllegalArgumentException) {
            showError(context, "Invalid operation. Please report this issue.");
            return true;
        }

        // Use fallback for unhandled exceptions
        showError(context, fallbackMessage);
        return false;
    }

    /**
     * Shows an error message to the user as a toast
     * @param context The context to show the toast
     * @param message The error message to display
     */
    public static void showError(@NonNull Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * Logs an error without showing a user-facing message
     * @param tag The tag to use for logging
     * @param message The message to log
     * @param throwable The exception that occurred (optional)
     */
    public static void logError(String tag, String message, @Nullable Throwable throwable) {
        if (throwable != null) {
            Log.e(tag, message, throwable);
        } else {
            Log.e(tag, message);
        }
    }

    /**
     * Safely get resource ID for a drawable by filename
     * @param context The context to use for resource lookup
     * @param resourceName The resource name (without extension)
     * @param defaultResourceId The default resource ID to return if not found
     * @return The resource ID or the default resource ID if not found
     */
    public static int getDrawableResourceId(Context context, String resourceName, int defaultResourceId) {
        try {
            if (resourceName == null || resourceName.isEmpty()) {
                return defaultResourceId;
            }

            int resourceId = context.getResources().getIdentifier(
                    resourceName, "drawable", context.getPackageName());

            if (resourceId == 0) {
                Log.w(TAG, "Resource not found: " + resourceName);
                return defaultResourceId;
            }

            // Also check if the default resource exists
            if (defaultResourceId != 0) {
                try {
                    context.getResources().getResourceName(defaultResourceId);
                } catch (Resources.NotFoundException e) {
                    Log.w(TAG, "Default resource not found: " + defaultResourceId);
                    // Create a color drawable as a last resort
                    return android.R.color.darker_gray;
                }
            }

            return resourceId;
        } catch (Exception e) {
            Log.e(TAG, "Error finding resource: " + resourceName, e);
            return defaultResourceId;
        }
    }
}
