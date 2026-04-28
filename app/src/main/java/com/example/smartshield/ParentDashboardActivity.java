package com.example.smartshield;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class ParentDashboardActivity extends AppCompatActivity {
    private RecyclerView appsRecyclerView;
    private Button activateChildModeButton;
    private InstalledAppAdapter appAdapter;
    private SmartShieldDatabase database;
    private AppRuleDao appRuleDao;
    private List<AppModel> installedApps;
    private List<String> whitelistedPackages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_dashboard);

        database = SmartShieldDatabase.getInstance(this);
        appRuleDao = database.appRuleDao();
        
        initViews();
        checkPermissions();
        loadInstalledApps();
        setupListeners();
    }

    private void initViews() {
        appsRecyclerView = findViewById(R.id.apps_recycler_view);
        activateChildModeButton = findViewById(R.id.activate_child_mode_button);
        
        appsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void checkPermissions() {
        if (!hasUsageAccessPermission()) {
            showPermissionDialog("Usage Access", 
                "SmartShield needs Usage Access to monitor which apps are being used.",
                Settings.ACTION_USAGE_ACCESS_SETTINGS);
        }
        
        if (!hasOverlayPermission()) {
            showPermissionDialog("Display Over Other Apps",
                "SmartShield needs to display over other apps to show the blocking screen.",
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        }
    }

    private boolean hasUsageAccessPermission() {
        try {
            android.app.usage.UsageStatsManager usageStatsManager = 
                (android.app.usage.UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
            long currentTime = System.currentTimeMillis();
            List<android.app.usage.UsageStats> stats = usageStatsManager.queryUsageStats(
                    android.app.usage.UsageStatsManager.INTERVAL_DAILY, currentTime - 1000, currentTime);
            return stats != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean hasOverlayPermission() {
        return Settings.canDrawOverlays(this);
    }

    private void showPermissionDialog(String title, String message, String settingsAction) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title + " Required")
                .setMessage(message)
                .setPositiveButton("Grant", (dialog, which) -> {
                    Intent intent = new Intent(settingsAction);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    Toast.makeText(this, "SmartShield cannot function without " + title, Toast.LENGTH_LONG).show();
                })
                .setCancelable(false)
                .show();
    }

    private void loadInstalledApps() {
        installedApps = getInstalledApps();
        whitelistedPackages = loadWhitelistedPackages();
        
        appAdapter = new InstalledAppAdapter(this, installedApps, whitelistedPackages);
        appsRecyclerView.setAdapter(appAdapter);
    }

    private List<String> loadWhitelistedPackages() {
        List<String> whitelisted = new ArrayList<>();
        try {
            List<AppRule> rules = appRuleDao.getWhitelistedRules();
            for (AppRule rule : rules) {
                whitelisted.add(rule.getPackageName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return whitelisted;
    }

    private List<AppModel> getInstalledApps() {
        List<AppModel> apps = new ArrayList<>();
        // Simplified app list for demo - in real implementation, use PackageManager
        apps.add(new AppModel(1, "SmartShield", "com.example.smartshield", "ic_launcher_foreground"));
        apps.add(new AppModel(2, "Calculator", "com.android.calculator2", "ic_launcher_foreground"));
        apps.add(new AppModel(3, "Camera", "com.android.camera", "ic_launcher_foreground"));
        apps.add(new AppModel(4, "Gallery", "com.android.gallery", "ic_launcher_foreground"));
        apps.add(new AppModel(5, "Messages", "com.android.messaging", "ic_launcher_foreground"));
        apps.add(new AppModel(6, "Phone", "com.android.phone", "ic_launcher_foreground"));
        apps.add(new AppModel(7, "Settings", "com.android.settings", "ic_launcher_foreground"));
        apps.add(new AppModel(8, "Chrome", "com.android.chrome", "ic_launcher_foreground"));
        apps.add(new AppModel(9, "YouTube", "com.google.android.youtube", "ic_launcher_foreground"));
        apps.add(new AppModel(10, "Gmail", "com.google.android.gm", "ic_launcher_foreground"));
        return apps;
    }

    private void setupListeners() {
        activateChildModeButton.setOnClickListener(v -> activateChildMode());
        
        appAdapter.setOnWhitelistToggleListener((packageName, isWhitelisted) -> {
            SmartShieldDatabase.executeInBackground(() -> {
                if (isWhitelisted) {
                    appRuleDao.insertOrUpdateRule(new AppRule(packageName, true));
                } else {
                    appRuleDao.deleteRule(packageName);
                }
            });
        });
    }

    private void activateChildMode() {
        Toast.makeText(this, "Child Mode Activated", Toast.LENGTH_SHORT).show();
        
        // Start WatchdogService
        Intent serviceIntent = new Intent(this, WatchdogService.class);
        startService(serviceIntent);
        
        // Go to home screen
        Intent homeIntent = new Intent(Intent.ACTION_MAIN);
        homeIntent.addCategory(Intent.CATEGORY_HOME);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(homeIntent);
    }
}
