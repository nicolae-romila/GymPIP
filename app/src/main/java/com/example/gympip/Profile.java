package com.example.gympip;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText; // Required for the name input field
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

import java.util.HashMap; // Required for saving multiple fields
import java.util.Map;     // Required for saving multiple fields

public class Profile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    // UI Components
    private ImageView profileImageView;
    private AutoCompleteTextView cityDropdown;
    private EditText nameEditText; // EditText for user's name

    // Firebase instances
    private Uri imageUri;
    private FirebaseAuth mAuth;
    private StorageReference storageReference;
    private DatabaseReference userDatabaseRef; // Reference to the current user's data in Realtime Database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);

        // Apply window insets for system bars (status bar, navigation bar)
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
                    return insets;
                });

        // --- Firebase Initialization (MUST be done first) ---
        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        // Get the current authenticated user
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Check if a user is logged in. If not, handle the scenario (e.g., redirect to login).
        if (currentUser != null) {
            // Initialize userDatabaseRef to point to the current user's specific node
            // Path: "users" -> [user's UID]
            userDatabaseRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
        } else {
            // No user logged in. Display a message and potentially redirect.
            Toast.makeText(this, "Nu sunteți autentificat. Vă rugăm să vă autentificați.", Toast.LENGTH_LONG).show();
            // Example: Redirect to a login activity
            // startActivity(new Intent(this, LoginActivity.class));
            // finish(); // Close this activity
            return; // Stop further execution in onCreate as user data won't be available
        }

        // --- UI Component Initialization ---
        profileImageView = findViewById(R.id.profileImageView);
        cityDropdown = findViewById(R.id.city_dropdown);
        nameEditText = findViewById(R.id.nameEditText); // Link to the EditText in XML

        // --- Setup UI elements and load existing data ---
        setupCityDropdown();
        setupProfileImage();
        loadUserProfileData(); // Load existing name and city from Firebase
    }

    /**
     * Sets up the ArrayAdapter for the city dropdown (AutoCompleteTextView).
     */
    private void setupCityDropdown() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                getResources().getStringArray(R.array.orase_romania) // Ensure this array is defined in strings.xml
        );
        cityDropdown.setAdapter(adapter);
        cityDropdown.setThreshold(1); // Start showing suggestions after 1 character

        // Optional: Add a listener if you want to do something immediately when a city is selected
        cityDropdown.setOnItemClickListener((parent, view, position, id) -> {
            String selectedCity = (String) parent.getItemAtPosition(position);
            Toast.makeText(Profile.this, "Oraș selectat: " + selectedCity, Toast.LENGTH_SHORT).show();
            // You could automatically save the city here, or wait for a "Save" button click.
        });
    }

    /**
     * Sets up the click listener for the profile image and loads the existing image.
     */
    private void setupProfileImage() {
        profileImageView.setOnClickListener(v -> openImageChooser());
        loadProfileImage(); // Load the profile image from Firebase Storage
    }

    /**
     * Opens the image chooser intent to select an image from the device.
     */
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*"); // Specifies that we want image files
        intent.setAction(Intent.ACTION_GET_CONTENT); // Action to pick a piece of data
        startActivityForResult(intent, PICK_IMAGE_REQUEST); // Start the activity and wait for a result
    }

    /**
     * Handles the result from the image chooser activity.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request code matches, result is OK, and data is not null
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imageUri = data.getData(); // Get the URI of the selected image
            Picasso.get().load(imageUri).into(profileImageView); // Display the selected image in ImageView
            uploadImageToFirebase(); // Upload the selected image to Firebase Storage
        }
    }

    /**
     * Uploads the selected image to Firebase Storage.
     */
    private void uploadImageToFirebase() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Ensure an image is selected and a user is logged in
        if (imageUri != null && currentUser != null) {
            String userId = currentUser.getUid();
            // Define the storage path for the profile image: "profile_images" -> [user's UID].jpg
            StorageReference fileReference = storageReference.child("profile_images/" + userId + ".jpg");

            fileReference.putFile(imageUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        Toast.makeText(Profile.this, "Imagine încărcată cu succes", Toast.LENGTH_SHORT).show();

                        // Get the download URL of the uploaded image
                        fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                            String imageUrl = uri.toString();
                            if (userDatabaseRef != null) {
                                // Save the image URL in Realtime Database under the user's node
                                userDatabaseRef.child("profileImageUrl").setValue(imageUrl)
                                        .addOnSuccessListener(aVoid -> Toast.makeText(Profile.this, "URL imagine salvat în baza de date!", Toast.LENGTH_SHORT).show())
                                        .addOnFailureListener(e -> Toast.makeText(Profile.this, "Eroare la salvarea URL-ului imaginii: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            }
                        }).addOnFailureListener(e -> {
                            Toast.makeText(Profile.this, "Eroare la obținerea URL-ului imaginii: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });

                        loadProfileImage(); // Reload the image to ensure the latest version is displayed
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(Profile.this, "Încărcare eșuată: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else if (currentUser == null) {
            Toast.makeText(this, "Nu sunteți autentificat pentru a încărca imaginea.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Nicio imagine selectată.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads the user's profile image from Firebase Storage using Picasso.
     * Sets a default avatar if no image is found or an error occurs.
     */
    private void loadProfileImage() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        // Always set a default image first, in case loading fails or no image exists
        profileImageView.setImageResource(R.drawable.avataricon);

        if (currentUser != null) {
            String userId = currentUser.getUid();
            StorageReference profileImageRef = storageReference.child("profile_images/" + userId + ".jpg");

            profileImageRef.getDownloadUrl()
                    .addOnSuccessListener(uri -> Picasso.get().load(uri).into(profileImageView))
                    .addOnFailureListener(e -> {
                        // If there's an error (e.g., image doesn't exist), the default avataricon remains.
                        // You can log the error for debugging: Log.e("Profile", "Error loading profile image: " + e.getMessage());
                    });
        }
    }

    /**
     * Saves the user's name and city to Firebase Realtime Database.
     * This method is typically called when a "Save" button is clicked.
     * Add `android:onClick="saveUserProfileData"` to your save button in `activity_profile.xml`.
     */
    public void saveUserProfileData(View view) {
        String name = nameEditText.getText().toString().trim();
        String city = cityDropdown.getText().toString().trim();

        // Basic validation
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
            // Create a HashMap to store the data to be updated
            Map<String, Object> userUpdates = new HashMap<>();
            userUpdates.put("name", name);
            userUpdates.put("city", city);

            // Use updateChildren() to add or update specific fields without overwriting the whole node
            userDatabaseRef.updateChildren(userUpdates)
                    .addOnSuccessListener(aVoid -> Toast.makeText(Profile.this, "Datele de profil au fost salvate!", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Profile.this, "Eroare la salvarea datelor: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "Nu sunteți autentificat sau referința bazei de date nu este disponibilă.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Loads the user's name and city from Firebase Realtime Database
     * and populates the respective UI fields when the activity starts.
     */
    private void loadUserProfileData() {
        if (userDatabaseRef != null) {
            userDatabaseRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    // Check if data exists for the current user
                    if (task.getResult().exists()) {
                        // Retrieve name and city as String objects
                        String name = task.getResult().child("name").getValue(String.class);
                        String city = task.getResult().child("city").getValue(String.class);

                        // Populate EditText and AutoCompleteTextView if data is found
                        if (name != null) {
                            nameEditText.setText(name);
                        }
                        if (city != null) {
                            // Use setText(text, false) for AutoCompleteTextView to prevent showing suggestions immediately
                            cityDropdown.setText(city, false);
                        }
                    }
                } else {
                    // Handle errors during data retrieval
                    Toast.makeText(Profile.this, "Eroare la încărcarea datelor: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Handles the back button click, navigating to MainActivity and finishing the current activity.
     */
    public void back(View v) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish(); // Finish current activity to prevent going back to it from MainActivity
    }
}
