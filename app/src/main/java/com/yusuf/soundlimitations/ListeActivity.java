package com.yusuf.soundlimitations;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste); // yeni layout

        // Arama, liste, progressBar bağlan
        SearchView searchView = findViewById(R.id.searchView);
        ListView listView = findViewById(R.id.listView);
        progressBar = findViewById(R.id.progressBar);

        // Paket yöneticisi
        PackageManager packageManager = getPackageManager();
        allApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);

        // Kullanıcı uygulamalarını filtrele
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
                new Thread(() -> {
                    filterApps(newText);
                    runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        adapter.notifyDataSetChanged();
                    });
                }).start();
                return true;
            }
        });
    }

    // Filtreleme
    private void filterApps(String query) {
        filteredApps.clear();
        for (ApplicationInfo appInfo : allApps) {
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    (appInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                String appName = getPackageManager().getApplicationLabel(appInfo).toString().toLowerCase(Locale.getDefault());
                if (appName.contains(query.toLowerCase(Locale.getDefault()))) {
                    filteredApps.add(appInfo);
                }
            }
        }
        sortApps(filteredApps);
    }

    // Sıralama
    private void sortApps(List<ApplicationInfo> apps) {
        Collections.sort(apps, (app1, app2) -> {
            String name1 = getPackageManager().getApplicationLabel(app1).toString().toLowerCase(Locale.getDefault());
            String name2 = getPackageManager().getApplicationLabel(app2).toString().toLowerCase(Locale.getDefault());
            return name1.compareTo(name2);
        });
    }
}
