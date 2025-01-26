package com.yusuf.soundlimitations;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

public class GoruntuleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goruntule);

        ViewPager viewPager = findViewById(R.id.viewPager);

        // Kullanıcı uygulamalarını çek
        List<ApplicationInfo> userApps = getUserInstalledApps();
        // Uygulamaları sayfalara böl
        List<List<ApplicationInfo>> pages = splitIntoPages(userApps, 12);

        // Adaptör
        AppsPagerAdapter pagerAdapter = new AppsPagerAdapter(this, pages);
        viewPager.setAdapter(pagerAdapter);
    }

    private List<ApplicationInfo> getUserInstalledApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> allApps = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        List<ApplicationInfo> filtered = new ArrayList<>();
        for (ApplicationInfo app : allApps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0 &&
                    (app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                filtered.add(app);
            }
        }
        return filtered;
    }

    private List<List<ApplicationInfo>> splitIntoPages(List<ApplicationInfo> apps, int pageSize) {
        List<List<ApplicationInfo>> pages = new ArrayList<>();
        for (int i = 0; i < apps.size(); i += pageSize) {
            int end = Math.min(i + pageSize, apps.size());
            pages.add(apps.subList(i, end));
        }
        return pages;
    }
}
