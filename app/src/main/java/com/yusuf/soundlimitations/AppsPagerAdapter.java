package com.yusuf.soundlimitations;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import java.util.List;

public class AppsPagerAdapter extends PagerAdapter {
    private final Context context;
    private final List<List<ApplicationInfo>> pages;

    public AppsPagerAdapter(Context context, List<List<ApplicationInfo>> pages) {
        this.context = context;
        this.pages = pages;
    }

    @Override
    public int getCount() {
        return pages.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        GridView gridView = new GridView(context);
        gridView.setNumColumns(4);
        gridView.setHorizontalSpacing(20);
        gridView.setVerticalSpacing(20);
        gridView.setPadding(20,20,20,20);

        List<ApplicationInfo> appsInPage = pages.get(position);
        GridAdapter adapter = new GridAdapter(context, appsInPage);
        gridView.setAdapter(adapter);

        container.addView(gridView);
        return gridView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    private static class GridAdapter extends BaseAdapter {
        private final Context mContext;
        private final List<ApplicationInfo> mApps;

        public GridAdapter(Context context, List<ApplicationInfo> apps) {
            mContext = context;
            mApps = apps;
        }

        @Override
        public int getCount() {
            return mApps.size();
        }

        @Override
        public Object getItem(int position) {
            return mApps.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(R.layout.grid_item, parent, false);
                holder = new ViewHolder();
                holder.icon = convertView.findViewById(R.id.item_icon);
                holder.name = convertView.findViewById(R.id.item_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            ApplicationInfo appInfo = mApps.get(position);
            Drawable icon = mContext.getPackageManager().getApplicationIcon(appInfo);
            String label = mContext.getPackageManager().getApplicationLabel(appInfo).toString();

            holder.icon.setImageDrawable(icon);
            holder.name.setText(label);

            return convertView;
        }

        static class ViewHolder {
            ImageView icon;
            TextView name;
        }
    }
}
