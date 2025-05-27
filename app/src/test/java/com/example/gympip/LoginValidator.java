package com.example.gympip;

/**
 * Clasa LoginValidator ofera metode de validare pentru email si parola.
 * Este utilizata pentru a verifica daca un email si o parola respecta reguli simple de validare.
 */
public class LoginValidator {

    /**
     * Verifica daca o adresa de email este valida.
     * Se considera valida daca contine caracterul '@' si un punct '.'.
     *
     * @param email Adresa de email de validat
     * @return true daca email-ul este valid, altfel false
     */
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    /**
     * Verifica daca o parola este valida.
     * O parola este considerata valida daca are cel putin 6 caractere.
     *
     * @param password Parola de verificat
     * @return true daca parola este valida, altfel false
     */
    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
