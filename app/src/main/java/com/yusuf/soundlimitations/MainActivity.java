package com.yusuf.soundlimitations;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnListele, btnGoruntule;
    Button btnOpenAccessibility; // Yeni buton

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnListele = findViewById(R.id.btnListele);
        btnGoruntule = findViewById(R.id.btnGoruntule);
        btnOpenAccessibility = findViewById(R.id.btnOpenAccessibility);

        // 1) LİSTELE Butonuna tıklanınca ListeActivity aç
        btnListele.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ListeActivity.class);
            startActivity(intent);
        });

        // 2) GÖRÜNTÜLE Butonuna tıklanınca GoruntuleActivity aç
        btnGoruntule.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GoruntuleActivity.class);
            startActivity(intent);
        });

        // 3) Erişilebilirlik Ayarları butonu
        btnOpenAccessibility.setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity(intent);
        });
    }
}
