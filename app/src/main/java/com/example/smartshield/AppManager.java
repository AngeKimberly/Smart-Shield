package com.example.smartshield;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import java.util.ArrayList;
import java.util.List;

public class AppManager {
    
    public static class InstalledApp {
        private String appName;
        private String packageName;
        private Drawable appIcon;
        
        public InstalledApp(String appName, String packageName, Drawable appIcon) {
            this.appName = appName;
            this.packageName = packageName;
            this.appIcon = appIcon;
        }
        
        public String getAppName() { return appName; }
        public void setAppName(String appName) { this.appName = appName; }
        
        public String getPackageName() { return packageName; }
        public void setPackageName(String packageName) { this.packageName = packageName; }
        
        public Drawable getAppIcon() { return appIcon; }
        public void setAppIcon(Drawable appIcon) { this.appIcon = appIcon; }
    }
    
    public static List<InstalledApp> getInstalledApps(Context context) {
        List<InstalledApp> installedApps = new ArrayList<>();
        
        PackageManager packageManager = context.getPackageManager();
        
        // Create intent to filter for launcher apps only
        Intent mainIntent = new Intent(Intent.ACTION_MAIN);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        
        try {
            // Query for activities that can handle the main intent
            List<ResolveInfo> resolveInfos = packageManager.queryIntentActivities(mainIntent, 0);
            
            for (ResolveInfo resolveInfo : resolveInfos) {
                try {
                    ApplicationInfo appInfo = resolveInfo.activityInfo.applicationInfo;
                    
                    // Skip system apps that shouldn't be in kiosk
                    if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                        continue;
                    }
                    
                    // Skip duplicates
                    String packageName = appInfo.packageName;
                    boolean alreadyAdded = installedApps.stream()
                        .anyMatch(app -> app.getPackageName().equals(packageName));
                    if (alreadyAdded) {
                        continue;
                    }
                    
                    String appName = packageManager.getApplicationLabel(appInfo).toString();
                    Drawable appIcon = packageManager.getApplicationIcon(appInfo);
                    
                    installedApps.add(new InstalledApp(appName, packageName, appIcon));
                    
                } catch (Exception e) {
                    // Skip problematic apps but continue processing others
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            android.util.Log.e("AppManager", "Error querying installed apps", e);
        }
        
        return installedApps;
    }
}
