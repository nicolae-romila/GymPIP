package com.example.gympip;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity {
    // UI Components
    private EditText fullname, emaila, password, conp;
    private Button regb, logb;

    // Firebase Authentication instance
    private FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Enable EdgeToEdge to handle system bars nicely
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);

        // Set padding for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize UI components by linking them to XML views
        fullname = findViewById(R.id.flname);
        emaila = findViewById(R.id.emailadress);
        password = findViewById(R.id.pswd);
        conp = findViewById(R.id.cpswd);
        regb = findViewById(R.id.cacc);
        logb = findViewById(R.id.aha);

        // Initialize Firebase Authentication instance
        fauth = FirebaseAuth.getInstance();

        // If the user is already logged in, redirect to MainActivity and close this one
        if (fauth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        // Button to switch to Login screen
        logb.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Login.class)));

        // Register button logic
        regb.setOnClickListener(v -> {
            // Retrieve input values and trim spaces
            String email = emaila.getText().toString().trim();
            String pas = password.getText().toString().trim();
            String cp = conp.getText().toString().trim();
            String name = fullname.getText().toString().trim();

            // Basic validation of inputs
            if (TextUtils.isEmpty(name)) {
                fullname.setError("Numele este obligatoriu");
                fullname.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(email)) {
                emaila.setError("Email este obligatoriu");
                emaila.requestFocus();
                return;
            }
            if (TextUtils.isEmpty(pas)) {
                password.setError("Parola este obligatorie");
                password.requestFocus();
                return;
            }
            if (pas.length() < 6) {
                password.setError("Parola trebuie să aibă minim 6 caractere");
                password.requestFocus();
                return;
            }
            if (!cp.equals(pas)) {
                conp.setError("Parolele nu coincid");
                conp.requestFocus();
                return;
            }

            // Create user with email and password in Firebase Authentication
            fauth.createUserWithEmailAndPassword(email, pas)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(Register.this, "Înregistrare cu succes!", Toast.LENGTH_SHORT).show();
                            // Optionally, here you could save the full name to Realtime Database or Firestore if needed

                            // Redirect to MainActivity after successful registration
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(Register.this, "Eroare: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }
}
