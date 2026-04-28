package com.example.smartshield;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "app_rules")
public class AppRule {
    @PrimaryKey
    @NonNull
    private String packageName;
    
    private boolean isWhitelisted;
    
    public AppRule() {
        // Default constructor required by Room
    }
    
    @androidx.room.Ignore
    public AppRule(@NonNull String packageName, boolean isWhitelisted) {
        this.packageName = packageName;
        this.isWhitelisted = isWhitelisted;
    }
    
    @NonNull
    public String getPackageName() {
        return packageName;
    }
    
    public void setPackageName(@NonNull String packageName) {
        this.packageName = packageName;
    }
    
    public boolean isWhitelisted() {
        return isWhitelisted;
    }
    
    public void setWhitelisted(boolean whitelisted) {
        isWhitelisted = whitelisted;
    }
}
