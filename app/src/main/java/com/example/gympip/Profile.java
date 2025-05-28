package com.example.gympip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

/**
 * Activitatea pentru gestionarea profilului utilizatorului.
 * Permite selectarea imaginii de profil, introducerea numelui și orașului, salvarea acestora în Firebase.
 */
public class Profile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private ImageView profileImageView;
    private AutoCompleteTextView cityDropdown;
    private EditText nameEditText;

    // Firebase instances
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference userDatabaseRef;

    /**
     * Inițializează activitatea și componentele sale, configurează Firebase și încarcă datele curente ale utilizatorului.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        // Inițializare Firebase
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        } else {
            Toast.makeText(this, "Nu sunteți autentificat. Vă rugăm să vă autentificați.", Toast.LENGTH_LONG).show();
            return;
        }

        // Inițializare componente UI
        profileImageView = findViewById(R.id.profileImageView);
        cityDropdown = findViewById(R.id.city_dropdown);
        nameEditText = findViewById(R.id.nameEditText);

        setupCityDropdown();
        setupProfileImage();
        loadUserProfileData();
    }

    /**
     * Configurează AutoCompleteTextView pentru orașe.
     */
    private void setupCityDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.orase_romania)
        );
        cityDropdown.setAdapter(adapter);
        cityDropdown.setThreshold(1);

        cityDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = (String) parent.getItemAtPosition(position);
            Toast.makeText(Profile.this, "Oraș selectat: " + selectedCity, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * Configurează click listener-ul pentru imaginea de profil.
     */
    private void setupProfileImage() {
        profileImageView.setOnClickListener(v -> openImageChooser());
        loadProfileImage();
    }

    /**
     * Deschide selectorul de imagini.
     */
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    /**
     * Primește rezultatul din selectorul de imagini.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImageView);
            uploadImageToFirebase();
        }
    }

    /**
     * Încarcă imaginea selectată în Firebase Storage și salvează URL-ul în Realtime Database.
     */
    private void uploadImageToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (imageUri != null && currentUser != null) {
            String userId = currentUser.getUid();
            StorageReference fileReference = storageReference.child("profile_images/" + userId + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(Profile.this, "Imagine încărcată cu succes", Toast.LENGTH_SHORT).show();
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            if (userDatabaseRef != null) {
                                userDatabaseRef.child("profileImageUrl").setValue(imageUrl)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(Profile.this, "URL imagine salvat!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(Profile.this, "Eroare la salvare URL: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        });
                        loadProfileImage();
                    })
                    .addOnFailureListener(e -> Toast.makeText(Profile.this, "Încărcare eșuată: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else if (currentUser == null) {
            Toast.makeText(this, "Nu sunteți autentificat.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nicio imagine selectată.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Încarcă imaginea de profil a utilizatorului din Firebase Storage.
     */
    private void loadProfileImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        profileImageView.setImageResource(R.drawable.avataricon); // Imagine implicită

        if (currentUser != null) {
            String userId = currentUser.getUid();
            StorageReference profileImageRef = storageReference.child("profile_images/" + userId + ".jpg");

            profileImageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImageView));
        }
    }

    /**
     * Salvează numele și orașul utilizatorului în Firebase.
     * Apelată din layout cu android:onClick="saveUserProfileData".
     *
     * @param view Butonul care a declanșat metoda.
     */
    public void saveUserProfileData(View view) {
        String name = nameEditText.getText().toString().trim();
        String city = cityDropdown.getText().toString().trim();

        if (name.isEmpty()) {
            nameEditText.setError("Numele este obligatoriu!");
            nameEditText.requestFocus();
            return;
        }

        if (city.isEmpty()) {
            cityDropdown.setError("Orașul este obligatoriu!");
            cityDropdown.requestFocus();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && userDatabaseRef != null) {
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("name", name);
            userUpdates.put("city", city);

            userDatabaseRef.updateChildren(userUpdates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(Profile.this, "Datele de profil au fost salvate!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Profile.this, "Eroare la salvare: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Nu sunteți autentificat.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Încarcă datele utilizatorului din Firebase și le afișează în UI.
     */
    private void loadUserProfileData() {
        if (userDatabaseRef != null) {
            userDatabaseRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful() && task.getResult().exists()) {
                    String name = task.getResult().child("name").getValue(String.class);
                    String city = task.getResult().child("city").getValue(String.class);

                    if (name != null) {
                        nameEditText.setText(name);
                    }
                    if (city != null) {
                        cityDropdown.setText(city, false);
                    }
                } else {
                    Toast.makeText(Profile.this, "Eroare la încărcarea datelor: " +
                            (task.getException() != null ? task.getException().getMessage() : ""), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Navighează înapoi la MainActivity și închide activitatea curentă.
     *
     * @param v View-ul care a declanșat metoda.
     */
    public void back(View v) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
