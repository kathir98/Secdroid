package com.secureandroid.secdroid.Model;

import android.graphics.drawable.Drawable;

public class TrafficList {

    private String tAppName;
    private Drawable tAppIcon;
    private String tDestIp;
    private String tDestPort;
    private String tProtocol;
    private String tTimeStamp;


    public TrafficList(String tAppName, Drawable tAppIcon, String tDestIp, String tDestPort, String tProtocol, String tTimeStamp) {
        this.tAppName = tAppName;
        this.tAppIcon = tAppIcon;
        this.tDestIp = tDestIp;
        this.tDestPort = tDestPort;
        this.tProtocol = tProtocol;
        this.tTimeStamp = tTimeStamp;
    }

    public String gettAppName() {
        return tAppName;
    }

    public void settAppName(String tAppName) {
        this.tAppName = tAppName;
    }

    public Drawable gettAppIcon() {
        return tAppIcon;
    }

    public void settAppIcon(Drawable tAppIcon) {
        this.tAppIcon = tAppIcon;
    }

    public String gettDestIp() {
        return tDestIp;
    }

    public void settDestIp(String tDestIp) {
        this.tDestIp = tDestIp;
    }

    public String gettDestPort() {
        return tDestPort;
    }

    public void settDestPort(String tDestPort) {
        this.tDestPort = tDestPort;
    }

    public String gettProtocol() {
        return tProtocol;
    }

    public void settProtocol(String tProtocol) {
        this.tProtocol = tProtocol;
    }

    public String gettTimeStamp() {
        return tTimeStamp;
    }

    public void settTimeStamp(String tTimeStamp) {
        this.tTimeStamp = tTimeStamp;
    }

}
