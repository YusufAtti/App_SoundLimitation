package com.yusuf.soundlimitations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.database.ContentObserver;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class VolumeLimitActivity extends AppCompatActivity {

    public static final String SHARED_PREFS_NAME = "VolumeLimits"; // SP adı

    private ImageView appIconDetail;
    private TextView appNameDetail;
    private TextView txtDeviceMediaVolume;
    private EditText etMaxVolume;
    private Button btnOnayla;

    private AudioManager audioManager;
    private VolumeObserver volumeObserver;

    private String packageName; // Hangi uygulama için limit konulacak
    private String label;       // Uygulama adı (UI gösterim için)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volume_limit);

        // 1) XML referansları
        appIconDetail = findViewById(R.id.app_icon_detail);
        appNameDetail = findViewById(R.id.app_name_detail);
        txtDeviceMediaVolume = findViewById(R.id.txtDeviceMediaVolume);
        etMaxVolume = findViewById(R.id.etMaxVolume);
        btnOnayla = findViewById(R.id.btnOnayla);

        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // 2) ListeActivity'den gelen verileri al
        Intent intent = getIntent();
        packageName = intent.getStringExtra("EXTRA_PACKAGE_NAME");
        label = intent.getStringExtra("EXTRA_APP_LABEL");

        // 3) Uygulamanın ikonunu + adını göster
        if (packageName != null) {
            try {
                PackageManager pm = getPackageManager();
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                Drawable icon = pm.getApplicationIcon(appInfo);
                appIconDetail.setImageDrawable(icon);

                if (label != null) {
                    appNameDetail.setText(label);
                } else {
                    // Yedek
                    appNameDetail.setText(pm.getApplicationLabel(appInfo));
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        // 4) Cihazın mevcut medya ses düzeyini göster
        updateMediaVolumeDisplay();

        // 5) Ses değişimlerini gözlemleyebilmek için ContentObserver
        volumeObserver = new VolumeObserver(new Handler());
        getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI, true, volumeObserver
        );

        // 6) "Onayla" butonu
        btnOnayla.setOnClickListener(v -> {
            String maxVolStr = etMaxVolume.getText().toString().trim();
            if (maxVolStr.isEmpty()) {
                Toast.makeText(this, "Lütfen bir değer giriniz!", Toast.LENGTH_SHORT).show();
                return;
            }
            // parse int
            int maxVolume;
            try {
                maxVolume = Integer.parseInt(maxVolStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Geçersiz sayı girdiniz!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (maxVolume < 0) maxVolume = 0; // min 0
            if (maxVolume > 15) maxVolume = 15; // Android müzik akışı genelde 0-15 arası

            // SharedPreferences: packageName -> maxVolume
            SharedPreferences sp = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(packageName, maxVolume);
            editor.apply();

            Toast.makeText(this,
                    label + " için max ses: " + maxVolume + " ayarlandı",
                    Toast.LENGTH_SHORT).show();

            // ListeActivity'e dön
            finish();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Observer'ı bırak
        if (volumeObserver != null) {
            getContentResolver().unregisterContentObserver(volumeObserver);
        }
    }

    // Mevcut medya sesini TextView'e yansıtma
    private void updateMediaVolumeDisplay() {
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        txtDeviceMediaVolume.setText("Medya Ses Düzeyi: " + currentVolume);
    }

    // Ses değişimlerini algılamak için ContentObserver sınıfı
    private class VolumeObserver extends ContentObserver {
        public VolumeObserver(Handler handler) {
            super(handler);
        }
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            updateMediaVolumeDisplay();
        }
    }
}
