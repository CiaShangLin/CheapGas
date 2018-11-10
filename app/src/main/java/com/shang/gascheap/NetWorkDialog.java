package com.shang.gascheap;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by Shang on 2018/11/10.
 */

public class NetWorkDialog  {

    public static String TAG = "NetWorkDialog";

    public static void show(Context context,Dialog.OnClickListener listener){
        AlertDialog.Builder alertDialog=new AlertDialog.Builder(context);
        alertDialog.setTitle("網路狀態")
                .setMessage("請檢查網路是否開啟")
                .setPositiveButton("重試",listener)
        .create().show();
    }

    public static Boolean checkNetwokerStatus(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) { //=null的話就是沒網路
            return networkInfo.isConnected();
        }
        return false;
    }

}
