package com.example.gympip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth; // Adaugă import pentru FirebaseAuth
import com.google.firebase.auth.FirebaseUser; // Adaugă import pentru FirebaseUser
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener; // Adaugă import pentru OnCompleteListener
import com.google.android.gms.tasks.Task; // Adaugă import pentru Task

import java.util.ArrayList;
import java.util.List;

public class Chat_feature extends AppCompatActivity {

    private DatabaseReference mdb; // Referința pentru mesaje
    private EditText txtinput;
    private RecyclerView messwindow;
    private MessageAdapter Ma;
    private List<Message> messlist;
    private Button bsend;

    // --- Modificări Minime Aici ---
    private FirebaseAuth mAuth; // Variabila pentru Firebase Authentication
    private String userNameFromDb; // Variabila unde vom stoca numele utilizatorului
    // --- Sfârșit Modificări Minime ---

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_feature);

        // Inițializare Views
        bsend = findViewById(R.id.bsend);
        txtinput = findViewById(R.id.wmessage);
        messwindow = findViewById(R.id.rec);

        // Inițializare Firebase
        mdb = FirebaseDatabase.getInstance().getReference("messages");

        // --- Modificări Minime Aici: Inițializare Auth și preluare nume ---
        mAuth = FirebaseAuth.getInstance(); // Inițializează FirebaseAuth

        FirebaseUser currentUser = mAuth.getCurrentUser(); // Obține utilizatorul curent
        if (currentUser != null) {
            // Creează o referință către numele utilizatorului în baza de date
            DatabaseReference userNameRef = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(currentUser.getUid())
                    .child("name");

            userNameRef.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (task.isSuccessful()) {
                        DataSnapshot dataSnapshot = task.getResult();
                        if (dataSnapshot.exists() && dataSnapshot.getValue() != null) {
                            userNameFromDb = dataSnapshot.getValue(String.class);
                            // Optional: Poți afișa un Toast pentru a confirma că numele a fost încărcat
                            // Toast.makeText(Chat_feature.this, "Nume încărcat: " + userNameFromDb, Toast.LENGTH_SHORT).show();
                        } else {
                            userNameFromDb = "Anonim"; // Nume implicit dacă nu e setat
                            Toast.makeText(Chat_feature.this, "Numele nu a fost găsit în profil. Folosesc 'Anonim'.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        userNameFromDb = "Eroare Nume"; // Nume implicit în caz de eroare
                        Toast.makeText(Chat_feature.this, "Eroare la încărcarea numelui: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            userNameFromDb = "Utilizator Necunoscut"; // Nume implicit dacă nu e logat
            Toast.makeText(this, "Nu sunteți autentificat pentru a trimite mesaje. Numele nu este disponibil.", Toast.LENGTH_LONG).show();
            // Poți adăuga aici o redirecționare către ecranul de login, dacă vrei.
            // startActivity(new Intent(this, LoginActivity.class));
            // finish();
        }
        // --- Sfârșit Modificări Minime ---

        // Setup RecyclerView
        messlist = new ArrayList<>();
        Ma = new MessageAdapter(messlist);
        messwindow.setLayoutManager(new LinearLayoutManager(this));
        messwindow.setAdapter(Ma);

        // Send message button click
        bsend.setOnClickListener(v -> {
            String txt = txtinput.getText().toString().trim();
            // --- Modificări Minime Aici: Folosește userNameFromDb ---
            String bid = userNameFromDb; // Acum 'bid' va conține numele preluat din baza de date
            // --- Sfârșit Modificări Minime ---

            if (!txt.isEmpty() && bid != null && !bid.isEmpty()) { // Adaugă și verificare pentru bid gol
                // Create message with timestamp
                String timestamp = String.valueOf(System.currentTimeMillis());
                Message message = new Message(txt, bid, timestamp); // bid este numele expeditorului

                // Push to Firebase
                mdb.push().setValue(message)
                        .addOnSuccessListener(aVoid -> txtinput.setText(""))
                        .addOnFailureListener(e -> Toast.makeText(Chat_feature.this,
                                "Eroare la trimiterea mesajului: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                if (txt.isEmpty()) {
                    Toast.makeText(Chat_feature.this, "Mesajul nu poate fi gol.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Chat_feature.this, "Numele utilizatorului nu a fost încărcat încă.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Load messages
        loadMessages();
    }

    private void loadMessages() {
        mdb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messlist.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Message message = dataSnapshot.getValue(Message.class);
                    if (message != null) {
                        messlist.add(message);
                    }
                }
                Ma.notifyDataSetChanged();
                if (!messlist.isEmpty()) {
                    messwindow.scrollToPosition(messlist.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Chat_feature.this,
                        "Eroare: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void chBack(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish(); // Adăugat finish() pentru a închide activitatea curentă
    }
}