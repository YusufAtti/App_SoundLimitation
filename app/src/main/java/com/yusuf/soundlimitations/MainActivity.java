package com.yusuf.soundlimitations;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnListele, btnGoruntule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); // Sadece iki butonlu layout

        btnListele = findViewById(R.id.btnListele);
        btnGoruntule = findViewById(R.id.btnGoruntule);

        btnListele.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListeActivity.class);
            startActivity(intent);
        });

        btnGoruntule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GoruntuleActivity.class);
            startActivity(intent);
        });
    }
}
