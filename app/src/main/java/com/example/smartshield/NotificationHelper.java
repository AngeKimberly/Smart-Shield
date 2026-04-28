package com.example.smartshield;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    private static final String CHANNEL_ID = "security_alerts";
    private static final String CHANNEL_NAME = "Security Alerts";
    private static final String CHANNEL_DESCRIPTION = "Security alerts for SmartShield app";
    
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);
            channel.enableLights(true);
            channel.enableVibration(true);
            
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    
    public static void showSecurityAlertNotification(Context context) {
        // Create intent to open ParentDashboardActivity when notification is tapped
        Intent intent = new Intent(context, ParentDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_lock) // Use system lock icon
                .setContentTitle("Device Managed by SmartShield")
                .setContentText("Navigation restricted.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Show the notification
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(1001, builder.build());
        }
    }
    
    public static void showAccessRestrictedNotification(Context context) {
        // Create intent to open ParentDashboardActivity when notification is tapped
        Intent intent = new Intent(context, ParentDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_lock_lock) // Use system lock icon
                .setContentTitle("Access Restricted")
                .setContentText("This app is not on your allowed list.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Show the notification
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(1002, builder.build());
        }
    }
    
    public static void clearSecurityAlertNotification(Context context) {
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.cancel(1001); // Navigation restricted notification
        notificationManager.cancel(1002); // Access restricted notification
        notificationManager.cancel(1003); // Security error notification
    }
    
    public static void showSecurityErrorNotification(Context context) {
        // Create intent to open ParentDashboardActivity when notification is tapped
        Intent intent = new Intent(context, ParentDashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        PendingIntent pendingIntent = PendingIntent.getActivity(
            context, 
            0, 
            intent, 
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );
        
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_alert) // Use alert icon for errors
                .setContentTitle("Security Error")
                .setContentText("Could not initialize Child Mode. Check Database settings.")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setCategory(NotificationCompat.CATEGORY_ERROR)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC);
        
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        
        // Show the notification
        if (notificationManager.areNotificationsEnabled()) {
            notificationManager.notify(1003, builder.build());
        }
    }
}
