package com.example.sebastin.bina2;

import android.widget.EditText;

/**
 * Created by Sebastián on 12/02/2016.
 */

public class ejemploJava {
    int tWater = 200;
    public ejemploJava(){
        //Default constructor
    }
    public ejemploJava (int waterAmount){
        tWater = waterAmount;

    }

    public  int add (int waterAmount){
        tWater = tWater + waterAmount;
        return tWater;
        }

    public  int subs (int waterAmount){
        tWater = tWater - waterAmount;
        return tWater;
        }
    public int getWater (){
        return tWater;
    }
}

