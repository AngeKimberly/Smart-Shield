package com.example.smartshield;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface AppRuleDao {
    
    @Query("SELECT * FROM app_rules")
    List<AppRule> getAllRules();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdateRule(AppRule appRule);
    
    @Query("SELECT * FROM app_rules WHERE packageName = :pkgName LIMIT 1")
    AppRule findRuleByPackage(String pkgName);
    
    @Query("SELECT * FROM app_rules WHERE isWhitelisted = 1")
    List<AppRule> getWhitelistedRules();
    
    @Query("DELETE FROM app_rules WHERE packageName = :pkgName")
    void deleteRule(String pkgName);
    
    @Query("DELETE FROM app_rules")
    void deleteAllRules();
}
