package com.example.gympip;
public class MessageValidator {

    public boolean isValid(String message) {
        if (message == null) return false;

        String trimmed = message.trim();

        // mesajul nu trebuie să fie gol și să aibă max 1000 caractere
        return !trimmed.isEmpty() && trimmed.length() <= 1000;
    }
}
