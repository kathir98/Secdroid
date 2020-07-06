package com.secureandroid.secdroid.Model;

import android.graphics.drawable.Drawable;

public class appdetials  {

    private String appName;
    private String appPermissions;
    private Drawable appIcon;
    private String allpermissions;
    private String packagesName;

    public appdetials() {
    }


    public appdetials(String appName, String appPermissions, Drawable appIcon,String allpermissions, String packagesName ) {
        this.appName = appName;
        this.appPermissions = appPermissions;
        this.appIcon = appIcon;
        this.allpermissions = allpermissions;
        this.packagesName = packagesName;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPermissions() {
        return appPermissions;
    }

    public void setAppPermissions(String appPermissions) {
        this.appPermissions = appPermissions;
    }

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getallPermissions() {
        return allpermissions;
    }

    public void setallPermissions(String allpermissions) {
        this.allpermissions = allpermissions;
    }

    public String getPackagesName() {
        return packagesName;
    }

    public void setPackagesName(String packageName) {
        this.packagesName = packageName;
    }
}
