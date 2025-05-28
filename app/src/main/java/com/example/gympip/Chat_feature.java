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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Activitate responsabilă pentru funcționalitatea de chat.
 * Permite utilizatorilor să trimită și să primească mesaje în timp real folosind Firebase.
 */
public class Chat_feature extends AppCompatActivity {

    /** Referința la baza de date Firebase pentru mesaje */
    private DatabaseReference mdb;

    /** Câmpul de text pentru introducerea mesajului */
    private EditText txtinput;

    /** Fereastra care afișează lista de mesaje */
    private RecyclerView messwindow;

    /** Adapterul folosit pentru RecyclerView */
    private MessageAdapter Ma;

    /** Lista de mesaje afișate */
    private List<Message> messlist;

    /** Butonul pentru trimiterea mesajelor */
    private Button bsend;

    /** Obiect pentru autentificare Firebase */
    private FirebaseAuth mAuth;

    /** Numele utilizatorului obținut din baza de date */
    private String userNameFromDb;

    /**
     * Metodă apelată la inițializarea activității.
     * Configurează Firebase, interfața și funcționalitățile de trimitere/afișare a mesajelor.
     *
     * @param savedInstanceState Stare salvată anterioară (neutilizată aici)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_feature);

        bsend = findViewById(R.id.bsend);
        txtinput = findViewById(R.id.wmessage);
        messwindow = findViewById(R.id.rec);

        mdb = FirebaseDatabase.getInstance().getReference("messages");
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
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
                        } else {
                            userNameFromDb = "Anonim";
                            Toast.makeText(Chat_feature.this, "Numele nu a fost găsit în profil. Folosesc 'Anonim'.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        userNameFromDb = "Eroare Nume";
                        Toast.makeText(Chat_feature.this, "Eroare la încărcarea numelui: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            userNameFromDb = "Utilizator Necunoscut";
            Toast.makeText(this, "Nu sunteți autentificat pentru a trimite mesaje. Numele nu este disponibil.", Toast.LENGTH_LONG).show();
        }

        messlist = new ArrayList<>();
        Ma = new MessageAdapter(messlist);
        messwindow.setLayoutManager(new LinearLayoutManager(this));
        messwindow.setAdapter(Ma);

        bsend.setOnClickListener(v -> {
            String txt = txtinput.getText().toString().trim();
            String bid = userNameFromDb;

            if (!txt.isEmpty() && bid != null && !bid.isEmpty()) {
                String timestamp = String.valueOf(System.currentTimeMillis());
                Message message = new Message(txt, bid, timestamp);

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

        loadMessages();
    }

    /**
     * Încarcă mesajele din baza de date Firebase și le actualizează în interfață.
     * Se ascultă modificările în timp real.
     */
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

    /**
     * Metodă apelată la apăsarea butonului de "Back" din UI.
     * Revine la activitatea principală (MainActivity).
     *
     * @param v View-ul pe care s-a făcut click.
     */
    public void chBack(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
