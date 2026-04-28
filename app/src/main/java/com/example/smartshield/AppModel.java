package com.example.smartshield;

import com.google.gson.annotations.SerializedName;

public class AppModel {
    @SerializedName("id")
    private int id;

    @SerializedName("appName")
    private String appName;

    @SerializedName("packageName")
    private String packageName;

    @SerializedName("iconName")
    private String iconName;

    public AppModel() {}

    public AppModel(int id, String appName, String packageName, String iconName) {
        this.id = id;
        this.appName = appName;
        this.packageName = packageName;
        this.iconName = iconName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }
}
