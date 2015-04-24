package com.lv.Activity;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by q on 2015/4/16.
 */
public class SDKApplication extends Application {
    static SharedPreferences sharedPreferences;
    static SharedPreferences.Editor editor;
    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        editor.putString("username","qyb");
        editor.commit();
    }
    public static SharedPreferences.Editor  getSPeditor(){
        if (editor!=null){
            return editor;
        }
        return null;
    }
    public static SharedPreferences  getSP(){
        if (sharedPreferences!=null){
            return sharedPreferences;
        }
        return null;
    }
}
