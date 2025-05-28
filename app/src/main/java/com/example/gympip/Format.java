package com.example.gympip;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Clasă utilitară pentru formatarea timestamp-urilor în format oră și dată.
 */
public class Format {

    /**
     * Constructor implicit.
     * Nu efectuează nicio inițializare suplimentară.
     */
    public Format() {}

    /**
     * Formatează un timestamp (în milisecunde, sub formă de string) într-un format ușor de citit.
     * Formatul rezultat este "HH:mm dd/MM", ex: "14:35 28/05".
     *
     * @param timestamp Timpul în milisecunde, sub formă de string.
     * @return String formatat al timestamp-ului, sau valoarea brută dacă parsingul eșuează.
     */
    public String formatTime(String timestamp) {
        try {
            long timeInMillis = Long.parseLong(timestamp);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd/MM", Locale.getDefault());
            return sdf.format(new Date(timeInMillis));
        } catch (NumberFormatException e) {
            return timestamp; // Returnează valoarea brută dacă timestamp-ul nu e valid
        }
    }
}
