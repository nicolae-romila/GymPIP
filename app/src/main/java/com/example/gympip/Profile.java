package com.example.gympip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Profile extends AppCompatActivity {

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
        setupCityDropdown();
    }

    private void setupCityDropdown() {
        AutoCompleteTextView cityDropdown = findViewById(R.id.city_dropdown);

        if (cityDropdown == null) {
            throw new RuntimeException("AutoCompleteTextView cu ID-ul city_dropdown nu a fost găsit în layout!");
        }

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

    public void back(View v) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}