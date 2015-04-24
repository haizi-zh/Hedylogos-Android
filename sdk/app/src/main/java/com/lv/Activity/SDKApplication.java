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
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    private String CurrentUser;
    private ArrayList<String> acklist;
    private HashMap<String,Integer> lastMsgMap;
    private boolean isBLOCK ;
    @Override
    public void onCreate() {
        super.onCreate();
        acklist = new ArrayList<String>();
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("username", "qyb");
        editor.commit();
    }

    public static SharedPreferences.Editor getSPeditor() {
        if (editor != null) {
            return editor;
        }
        return null;
    }

    public static SharedPreferences getSP() {
        if (sharedPreferences != null) {
            return sharedPreferences;
        }
        return null;
    }

    public String getCurrentUser() {
        return CurrentUser;
    }

    public void setCurrentUser(String currentUser) {
        CurrentUser = currentUser;
    }

    public ArrayList<String> getackList() {
        return acklist;
    }

    public int getackListsize() {
        return acklist.size();
    }

    public void add2ackLit(String id) {
        acklist.add(id);
    }

    public void clearackList() {
        acklist.clear();
    }

    public boolean isBLOCK() {
        return isBLOCK;
    }

    public void setBLOCK(boolean isBLOCK) {
        this.isBLOCK = isBLOCK;
    }

    public int getLastMsg(String fri_id) {
        return lastMsgMap.get(fri_id);
    }

    public void setLastMsg (String fri_Id,int msgId) {
        lastMsgMap.put(fri_Id,msgId);
    }
}