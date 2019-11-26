package ricky.boxlauncher.ui;

import android.content.Context;
import android.graphics.drawable.Drawable;

import java.util.HashMap;

public class IconCache {

    private Context mContext;
    private final HashMap<String, Drawable> iconMap = new HashMap<>();

    public IconCache(Context mContext) {
        this.mContext = mContext;
    }

    public Drawable getIconForPackage(String packageName) {
        if (iconMap.get(packageName) != null) {
            return iconMap.get(packageName);
        }
        try {
            Drawable icon = mContext.getPackageManager().getApplicationIcon(packageName);
            iconMap.put(packageName, icon);
            return icon;
        } catch (Exception e) {
            return null;
        }
    }
}
