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
    EditText fullname,emaila,password,conp;
    Button regb,logb;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fullname=findViewById(R.id.flname);
        emaila=findViewById(R.id.emailadress);
        password=findViewById(R.id.pswd);
        regb=findViewById(R.id.cacc);
        logb=findViewById(R.id.aha);
        conp=findViewById(R.id.cpswd);
        fauth=FirebaseAuth.getInstance();

        if(fauth.getCurrentUser()!=null){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
        }

        logb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Login.class));
            }
        });

        regb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=emaila.getText().toString().trim();
                String pas=password.getText().toString().trim();
                String cp=conp.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emaila.setError("Email required");
                    return;
                }
                if(TextUtils.isEmpty(pas)){
                    password.setError("Password required");
                    return;
                }
                if(pas.length()<6){
                    password.setError("Password must be 6 or more characters");
                    return;
                }

                if(!(cp.equals(pas))){
                    conp.setError("Passwords don't match");
                    return;
                }
                fauth.createUserWithEmailAndPassword(email,pas).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, "Register successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(Register.this, "Error! "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}