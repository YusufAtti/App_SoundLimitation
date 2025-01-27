package com.yusuf.soundlimitations;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

public class VolumeAccessibilityService extends AccessibilityService {

    private String currentPackage = "";
    private ContentObserver volumeObserver;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        if (event.getPackageName() != null) {
            currentPackage = event.getPackageName().toString();
        }
    }

    @Override
    public void onInterrupt() {
        // Servis kesilirse
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_UP) {
            int keyCode = event.getKeyCode();
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP || keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                applyVolumeLimitIfNeeded();
            }
        }
        return false; // veya super.onKeyEvent(event)
    }

    /**
     * Her ses değişiminde çağrılır (hem tuş, hem dokunmatik slider).
     */
    private void applyVolumeLimitIfNeeded() {
        // SharedPreferences'tan bu paket için bir limit var mı?
        // Mesela VolumeLimitActivity.SHARED_PREFS_NAME = "VolumeLimits"
        SharedPreferences sp = getSharedPreferences("VolumeLimits", MODE_PRIVATE);
        int defaultLimit = 15;
        int maxVolumeAllowed = sp.getInt(currentPackage, defaultLimit);

        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

        if (currentVolume > maxVolumeAllowed) {
            audioManager.setStreamVolume(
                    AudioManager.STREAM_MUSIC,
                    maxVolumeAllowed,
                    AudioManager.FLAG_SHOW_UI
            );
            Toast.makeText(this,
                    "Maksimum ses sınırını aştınız! (" + maxVolumeAllowed + ")",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();

        // 1) Servisin hangi Accessibility event'lerini dinleyeceğini belirtelim
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
                | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
                | AccessibilityEvent.TYPE_VIEW_CLICKED
                | AccessibilityEvent.TYPE_VIEW_FOCUSED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        info.packageNames = null; // Tüm paketleri dinle
        info.notificationTimeout = 100;
        setServiceInfo(info);

        // 2) ContentObserver tanımlayıp kayıt olalım
        volumeObserver = new ContentObserver(new Handler()) {
            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                // Sistem sesi değişti --> max sınırı aşıldı mı kontrol edelim
                applyVolumeLimitIfNeeded();
            }
        };

        // "Settings.System.CONTENT_URI" - Bu, sistem ayarlarında bir değişiklik olduğunda tetiklenir
        //  - Volume slider dokunulduğunda da genellikle burada onChange alırız.
        getContentResolver().registerContentObserver(
                Settings.System.CONTENT_URI,
                true,
                volumeObserver
        );
    }

    @Override
    public boolean onUnbind(android.content.Intent intent) {
        // Servis devreden çıkarken observer'ı kaldır
        if (volumeObserver != null) {
            getContentResolver().unregisterContentObserver(volumeObserver);
            volumeObserver = null;
        }
        return super.onUnbind(intent);
    }
}
