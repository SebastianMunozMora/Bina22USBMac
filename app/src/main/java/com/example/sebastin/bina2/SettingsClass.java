package com.example.sebastin.bina2;

import android.app.Application;

/**
 * Created by Sebastian on 15/03/2016.
 */
public class SettingsClass extends Application{
    public int sampleRate ;
    public int bitDepth ;
    private static SettingsClass instance;
    public static SettingsClass getInstance() {
        if (instance == null)
            instance = new SettingsClass();
        return instance;
    }
    public void setSampleRate(int sampleRate){
        this.sampleRate = sampleRate;
    }
    public void setBitDepth(int bitDepth){
        this.bitDepth = bitDepth;
    }
    public int getSampleRate(){
        return sampleRate;
    }
    public int getBitDepth(){
        return bitDepth;
    }
}
