package com.example.gympip;

import android.content.Intent;
import android.os.Bundle;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Chat_feature extends AppCompatActivity {

    private DatabaseReference mdb;
    private EditText txtinput;
    private RecyclerView messwindow;
    private MessageAdapter Ma;
    private List<Message> messlist;
    private Button bsend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_feature);

        // Initialize views
        bsend = findViewById(R.id.bsend);
        txtinput = findViewById(R.id.wmessage);
        messwindow = findViewById(R.id.rec);

        // Initialize Firebase
        mdb = FirebaseDatabase.getInstance().getReference("messages");

        // Setup RecyclerView
        messlist = new ArrayList<>();
        Ma = new MessageAdapter(messlist);
        messwindow.setLayoutManager(new LinearLayoutManager(this));
        messwindow.setAdapter(Ma);

        // Send message button click
        bsend.setOnClickListener(v -> {
            String txt = txtinput.getText().toString().trim();
            String bid = FirebaseAuth.getInstance().getUid();

            if (!txt.isEmpty() && bid != null) {
                // Create message with timestamp
                String timestamp = String.valueOf(System.currentTimeMillis());
                Message message = new Message(txt, bid, timestamp);

                // Push to Firebase
                mdb.push().setValue(message)
                        .addOnSuccessListener(aVoid -> txtinput.setText(""))
                        .addOnFailureListener(e -> Toast.makeText(Chat_feature.this,
                                "Failed to send: " + e.getMessage(), Toast.LENGTH_SHORT).show());
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
                        "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void chBack(View v){
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }



}