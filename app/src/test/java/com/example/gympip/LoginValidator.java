package com.example.gympip;

public class LoginValidator{
    public boolean isValidEmail(String email) {
        return email != null && email.contains("@") && email.contains(".");
    }

    public boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }
}
