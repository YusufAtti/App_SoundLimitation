package com.yusuf.soundlimitations;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ListeActivity extends AppCompatActivity {

    private AppAdapter adapter;
    private List<ApplicationInfo> allApps;
    private List<ApplicationInfo> filteredApps;
    private ProgressBar progressBar;
    private ListView listView; // referansı dışarı da tutalım

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste);

        // Arama, liste, progressBar bağlan
        SearchView searchView = findViewById(R.id.searchView);
        listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);

        // Paket yöneticisi
        PackageManager packageManager = getPackageManager();
        allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        // Kullanıcı uygulamalarını filtrele (system flags yok)
        filteredApps = new ArrayList<>();
        for (ApplicationInfo appInfo : allApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                filteredApps.add(appInfo);
            }
        }

        // A'dan Z'ye sıralama
        sortApps(filteredApps);

        // Adapter
        adapter = new AppAdapter(this, filteredApps);
        listView.setAdapter(adapter);

        // Arama tıklandığında genişlesin
        searchView.setOnClickListener(v -> searchView.setIconified(false));

        // Arama dinleyicisi
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                progressBar.setVisibility(View.VISIBLE);

                // Arka planda filtreleyip sıralayacağız
                new Thread(() -> {
                    // 1) Geçici listede filtreleme+sıralama
                    List<ApplicationInfo> tempList = filterAndSortApps(newText);

                    // 2) UI thread'de asıl listeyi güncelle
                    runOnUiThread(() -> {
                        filteredApps.clear();
                        filteredApps.addAll(tempList);
                        adapter.notifyDataSetChanged();
                        progressBar.setVisibility(View.GONE);
                    });
                }).start();

                return true;
            }
        });

        // ListView item tıklama olayı
        listView.setOnItemClickListener((parent, view, position, id) -> {
            // Tıklanan uygulamayı alalım
            ApplicationInfo selectedApp = filteredApps.get(position);

            // Yeni ekrana geç
            Intent intent = new Intent(ListeActivity.this, VolumeLimitActivity.class);

            // Bu uygulamanın paket adını ve adını (label) gönderelim
            intent.putExtra("EXTRA_PACKAGE_NAME", selectedApp.packageName);
            String label = getPackageManager()
                    .getApplicationLabel(selectedApp)
                    .toString();
            intent.putExtra("EXTRA_APP_LABEL", label);

            startActivity(intent);
        });
    }

    /**
     * Kullanıcıdan alınan query ile filtreleme + sıralama.
     * Bunu geçici bir listede yapıyoruz ki "ConcurrentModificationException" olmasın.
     */
    private List<ApplicationInfo> filterAndSortApps(String query) {
        List<ApplicationInfo> tempList = new ArrayList<>();

        for (ApplicationInfo appInfo : allApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {

                String appName = getPackageManager()
                        .getApplicationLabel(appInfo)
                        .toString()
                        .toLowerCase(Locale.getDefault());

                if (appName.contains(query.toLowerCase(Locale.getDefault()))) {
                    tempList.add(appInfo);
                }
            }
        }

        // Şimdi tempList'i sıralıyoruz
        sortApps(tempList);

        return tempList;
    }

    // Mevcut bir listeyi A'dan Z'ye sıralar
    private void sortApps(List<ApplicationInfo> apps) {
        Collections.sort(apps, (app1, app2) -> {
            String name1 = getPackageManager().getApplicationLabel(app1).toString().toLowerCase(Locale.getDefault());
            String name2 = getPackageManager().getApplicationLabel(app2).toString().toLowerCase(Locale.getDefault());
            return name1.compareTo(name2);
        });
    }
}
