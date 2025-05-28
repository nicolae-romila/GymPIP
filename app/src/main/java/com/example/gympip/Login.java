package com.example.gympip;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Activitate pentru autentificarea utilizatorilor folosind Firebase Authentication.
 */
public class Login extends AppCompatActivity {

    /**
     * Metoda apelată la crearea activității.
     * Configurează UI-ul și logica de autentificare.
     *
     * @param savedInstanceState Obiect ce conține starea anterioară a activității (dacă există).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Aplică margini pentru layout complet
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inițializare Firebase Authentication
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Legare UI
        EditText emailEditText = findViewById(R.id.editTextTextEmailAddress);
        EditText passwordEditText = findViewById(R.id.editTextTextPassword);
        Button loginButton = findViewById(R.id.button);

        // Setare acțiune pe butonul de login
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            // Verificare câmpuri goale
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(Login.this, "All fields required", Toast.LENGTH_SHORT).show();
                return;
            }

            // Încercare de autentificare Firebase
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Autentificare reușită, deschide MainActivity
                    Intent intent = new Intent(Login.this, MainActivity.class);
                    startActivity(intent);
                    finish(); // Termină activitatea curentă pentru a preveni revenirea la login
                } else {
                    // Autentificare eșuată, afișează mesaj de eroare
                    Toast.makeText(Login.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
