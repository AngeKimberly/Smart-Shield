package com.example.smartshield;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

public class WatchdogService extends Service {
    private static final String TAG = "WatchdogService";
    private static final int CHECK_INTERVAL = 500; // 500ms
    
    private Handler handler;
    private Runnable watchdogRunnable;
    private UsageStatsManager usageStatsManager;
    private SmartShieldDatabase database;
    private AppRuleDao appRuleDao;
    private String lastForegroundPackage = "";
    
    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "WatchdogService created");
        
        handler = new Handler(Looper.getMainLooper());
        usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        
        database = SmartShieldDatabase.getInstance(this);
        appRuleDao = database.appRuleDao();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "WatchdogService started");
        
        startWatchdogMonitoring();
        
        return START_STICKY;
    }
    
    private void startWatchdogMonitoring() {
        watchdogRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    checkForegroundApp();
                } catch (Exception e) {
                    Log.e(TAG, "Error in watchdog monitoring", e);
                }
                
                handler.postDelayed(this, CHECK_INTERVAL);
            }
        };
        
        handler.post(watchdogRunnable);
    }
    
    private void checkForegroundApp() {
        String currentForegroundPackage = getForegroundApp();
        
        if (currentForegroundPackage != null && !currentForegroundPackage.equals(lastForegroundPackage)) {
            Log.d(TAG, "Foreground app changed: " + lastForegroundPackage + " -> " + currentForegroundPackage);
            lastForegroundPackage = currentForegroundPackage;
            
            if (!isAppAllowed(currentForegroundPackage)) {
                Log.w(TAG, "Unauthorized app detected: " + currentForegroundPackage);
                
                // Launch BlockerActivity
                Intent lockIntent = new Intent(this, BlockerActivity.class);
                lockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(lockIntent);
            }
        }
    }
    
    private String getForegroundApp() {
        if (usageStatsManager == null) {
            return null;
        }
        
        long currentTime = System.currentTimeMillis();
        
        List<UsageStats> stats = usageStatsManager.queryUsageStats(
                UsageStatsManager.INTERVAL_DAILY, currentTime - 10000, currentTime);
        
        if (stats == null || stats.isEmpty()) {
            return null;
        }
        
        SortedMap<Long, UsageStats> sortedStats = new TreeMap<>();
        for (UsageStats usageStats : stats) {
            if (usageStats.getLastTimeUsed() > 0) {
                sortedStats.put(usageStats.getLastTimeUsed(), usageStats);
            }
        }
        
        if (!sortedStats.isEmpty()) {
            return sortedStats.get(sortedStats.lastKey()).getPackageName();
        }
        
        return null;
    }
    
    private boolean isAppAllowed(String packageName) {
        // Allow SmartShield itself
        if ("com.example.smartshield".equals(packageName)) {
            return true;
        }
        
        // Allow system launcher (home screen)
        if (isLauncherApp(packageName)) {
            return true;
        }
        
        // Check database whitelist
        try {
            List<AppRule> whitelistedRules = appRuleDao.getWhitelistedRules();
            for (AppRule rule : whitelistedRules) {
                if (rule.getPackageName().equals(packageName)) {
                    return true;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking whitelist", e);
        }
        
        return false;
    }
    
    private boolean isLauncherApp(String packageName) {
        return packageName != null && (
                packageName.contains("launcher") ||
                packageName.contains("home") ||
                packageName.equals("com.sec.android.app.launcher") ||
                packageName.equals("com.android.launcher") ||
                packageName.equals("com.google.android.apps.nexuslauncher")
        );
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "WatchdogService destroyed");
        
        if (handler != null && watchdogRunnable != null) {
            handler.removeCallbacks(watchdogRunnable);
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
