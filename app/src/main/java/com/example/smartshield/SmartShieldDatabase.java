package com.example.smartshield;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {AppRule.class}, version = 1, exportSchema = false)
public abstract class SmartShieldDatabase extends RoomDatabase {
    
    private static volatile SmartShieldDatabase INSTANCE;
    private static final String DATABASE_NAME = "smartshield_database";
    
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(4);
    
    public abstract AppRuleDao appRuleDao();
    
    public static SmartShieldDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (SmartShieldDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            SmartShieldDatabase.class,
                            DATABASE_NAME
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
    
    public static void executeInBackground(Runnable runnable) {
        databaseWriteExecutor.execute(runnable);
    }
}
