package com.example.gympip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Activitate principală a aplicației după autentificare.
 * Permite navigarea către profilul utilizatorului, funcționalitatea de chat și delogare.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Metodă apelată la inițializarea activității.
     * Configurează interfața grafică și marginile sistemului.
     *
     * @param savedInstanceState Obiect ce conține starea anterioară a activității (dacă există).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Ajustează padding-ul layout-ului pentru a evita suprapunerea cu barele sistemului
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Metodă apelată la apăsarea butonului de profil.
     * Deschide activitatea {@link Profile}.
     *
     * @param v View-ul care a declanșat acțiunea.
     */
    public void profile(View v) {
        startActivity(new Intent(getApplicationContext(), Profile.class));
        finish(); // Opțional: termină activitatea actuală
    }

    /**
     * Metodă apelată la apăsarea butonului de logout.
     * Deconectează utilizatorul și deschide activitatea {@link Login}.
     *
     * @param v View-ul care a declanșat acțiunea.
     */
    public void logout(View v) {
        FirebaseAuth.getInstance().signOut(); // Delogare Firebase
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish(); // Termină activitatea pentru a preveni întoarcerea cu back
    }

    /**
     * Metodă apelată la apăsarea butonului de chat.
     * Deschide activitatea {@link Chat_feature}.
     *
     * @param v View-ul care a declanșat acțiunea.
     */
    public void chatcl(View v) {
        startActivity(new Intent(getApplicationContext(), Chat_feature.class));
    }
}
