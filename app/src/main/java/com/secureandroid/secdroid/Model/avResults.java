package com.secureandroid.secdroid.Model;

import android.graphics.drawable.Drawable;

public class avResults {

    private String avName;
    private String vName;
    private Drawable symbol;
    private Boolean state;

    public avResults() {
    }


    public avResults(String avName, String vName, Drawable symbol, Boolean state) {
        this.avName = avName;
        this.vName = vName;
        this.symbol = symbol;
        this.state = state;
    }

    public String getAvName() {
        return avName;
    }

    public void setAvName(String avName) {
        this.avName = avName;
    }

    public String getVName() {
        return vName;
    }

    public void setVName(String vName) {
        this.vName = vName;
    }

    public Drawable getSymbol() {
        return symbol;
    }

    public void setSymbol(Drawable symbol) {
        this.symbol = symbol;
    }

    public Boolean getState() {
        return state;
    }

    public void setState(Boolean state) {
        this.state = state;
    }

}
