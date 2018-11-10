package com.shang.gascheap.Data;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * Created by Shang on 2018/6/14.
 */

public class PermissionUnit {

    public static final int REQUESTCODE = 1;

    static public String[] PERMISSION = {
              Manifest.permission.CAMERA
            , Manifest.permission.WRITE_EXTERNAL_STORAGE
            , Manifest.permission.READ_EXTERNAL_STORAGE
            , Manifest.permission.ACCESS_FINE_LOCATION
            , Manifest.permission.ACCESS_COARSE_LOCATION};

    static public boolean checkSelfPermission(Activity activity, String[] permission) {  //檢查全縣
        boolean flag = true;
        for (int i = 0; i < permission.length; i++) {
            if (ActivityCompat.checkSelfPermission(activity, permission[i]) != PackageManager.PERMISSION_GRANTED) {
                flag = false;
            }
        }
        return flag;
    }

    static public void requestPermissions(Activity activity, String[] permission) {
        ActivityCompat.requestPermissions(activity, permission, REQUESTCODE);
    }
}
