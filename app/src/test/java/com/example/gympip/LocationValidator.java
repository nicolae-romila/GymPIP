package com.example.gympip;

public class LocationValidator {
    // Numele locatiei trebuie sa fie intre 4 si 25 de caractere, fara cifre
    public boolean isValidLocationName(String location) {
        if (location == null || location.trim().isEmpty()) {
            return false;
        }
        if (location.length() < 4 || location.length() > 25) {
            return false;
        }
        return location.matches("[a-zA-ZÀ-ÿ\\s\\-]+");
    }
}
