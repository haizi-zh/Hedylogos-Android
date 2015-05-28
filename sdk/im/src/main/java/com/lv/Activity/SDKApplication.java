package com.lv.Activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by q on 2015/4/16.
 */
public class SDKApplication extends Application {
    public SDKApplication() {
    }

    public SDKApplication application;
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    private String CurrentUser;
    private ArrayList<String> acklist;
    private HashMap<String,Integer> lastMsgMap;
    private HashMap<String,Integer> CacheMsgMap;
    private boolean isBLOCK ;
    @Override
    public void onCreate() {
        super.onCreate();

    }



}