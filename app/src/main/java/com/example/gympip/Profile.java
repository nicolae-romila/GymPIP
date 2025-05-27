package com.example.gympip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class Profile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView profileImageView;
    private AutoCompleteTextView cityDropdown;
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;

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

        // Inițializare componente
        profileImageView = findViewById(R.id.profileImageView);
        cityDropdown = findViewById(R.id.city_dropdown);

        setupCityDropdown();
        setupProfileImage();
    }

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
            // Aici poți adăuga logica pentru orașul selectat
        });
    }

    private void setupProfileImage() {
        profileImageView.setOnClickListener(v -> openImageChooser());
        loadProfileImage();
    }

    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData();
            Picasso.get().load(imageUri).into(profileImageView);
            uploadImageToFirebase();
        }
    }

    private void uploadImageToFirebase() {
        if (imageUri != null && mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference fileReference = storageReference.child("profile_images/" + userId + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(Profile.this, "Imagine încărcată cu succes", Toast.LENGTH_SHORT).show();
                        loadProfileImage();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(Profile.this, "Încărcare eșuată: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }

    private void loadProfileImage() {
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();
            StorageReference profileImageRef = storageReference.child("profile_images/" + userId + ".jpg");

            // Setează imaginea implicită înainte de încărcare
            profileImageView.setImageResource(R.drawable.avataricon);

            profileImageRef.getDownloadUrl().addOnSuccessListener(uri ->
                            Picasso.get().load(uri).into(profileImageView))
                    .addOnFailureListener(e -> {
                        // Păstrează imaginea implicită dacă încărcarea eșuează
                        profileImageView.setImageResource(R.drawable.avataricon);
                    });
        } else {
            // Dacă nu e autentificat, folosește imaginea implicită
            profileImageView.setImageResource(R.drawable.avataricon);
        }
    }

    public void back(View v) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}