package com.shang.gascheap;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Shang on 2018/5/6.
 */

//這個應該封裝在Setting的
public class SharedPreOperating {
    private static SharedPreOperating sharedPreOperating = null;
    public static SharedPreferences sp;
    public static SharedPreferences.Editor editor;

    public static SharedPreOperating newInstance(Context context) {
        if (sharedPreOperating == null) {
            sharedPreOperating = new SharedPreOperating();
            sp = context.getSharedPreferences(SharedPreConstant.DATA_KEY, Context.MODE_PRIVATE);
            editor = context.getSharedPreferences(SharedPreConstant.DATA_KEY, Context.MODE_PRIVATE).edit();
        }
        return sharedPreOperating;
    }

    public void putString(String key, String str) {
        editor.putString(key, str);
        editor.commit();
    }

    public void putBoolean(String key, Boolean flag) {
        editor.putBoolean(key, flag);
        editor.commit();
    }

    public String getString(String key, String defValue) {
        return sp.getString(key, defValue);
    }

    public Boolean getBoolean(String key,Boolean defValue) {
        return sp.getBoolean(key, defValue);
    }
}
