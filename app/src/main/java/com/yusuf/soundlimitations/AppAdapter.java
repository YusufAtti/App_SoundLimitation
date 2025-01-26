package com.yusuf.soundlimitations;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AppAdapter extends ArrayAdapter<ApplicationInfo> {
    private final PackageManager packageManager;

    public AppAdapter(Context context, List<ApplicationInfo> apps) {
        super(context, 0, apps);
        packageManager = context.getPackageManager();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }

        ApplicationInfo appInfo = getItem(position);

        if (appInfo != null) {
            ImageView appIcon = convertView.findViewById(R.id.app_icon);
            TextView appName = convertView.findViewById(R.id.app_name);

            // Uygulama adını ve ikonunu ayarla
            Drawable icon = packageManager.getApplicationIcon(appInfo);
            String name = packageManager.getApplicationLabel(appInfo).toString();

            appIcon.setImageDrawable(icon);
            appName.setText(name);
        }

        return convertView;
    }
}
