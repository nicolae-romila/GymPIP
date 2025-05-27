package com.example.gympip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Format {
    public Format(){};
    public String formatTime(String timestamp) {
        try {
            long timeInMillis = Long.parseLong(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
            return sdf.format(new Date(timeInMillis));
        } catch (NumberFormatException e) {
            return timestamp; // Return raw timestamp if parsing fails
        }
    }
}
