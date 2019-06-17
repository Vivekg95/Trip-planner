package com.example.application;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Mayur on 11-10-2018.
 */

public class InitializeSharedPreferences {

    private String sharedPrefFile = "com.example.tripplanning";

    protected SharedPreferences sharedPreferences;
    protected SharedPreferences.Editor editor;
    private Context context;

    protected InitializeSharedPreferences(Context context){
        this.context = context;
        sharedPreferences = context.getSharedPreferences(sharedPrefFile,Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }
}
