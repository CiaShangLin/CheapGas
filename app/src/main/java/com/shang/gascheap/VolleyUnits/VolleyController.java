package com.shang.gascheap.VolleyUnits;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.shang.gascheap.NetWorkDialog;

import org.opencv.dnn.Net;

/**
 * Created by Shang on 2018/5/25.
 */

public class VolleyController{

    private static final String TAG = "VolleyController";
    private static RequestQueue mRequestQueue=null;
    private static VolleyController mInstance=null;
    private static Context context=null;


    public static synchronized VolleyController getInstance(Context context2){
        if(context!=context2){
            context=context2;
            mInstance=new VolleyController();
            mRequestQueue = Volley.newRequestQueue(context);
        }
        return mInstance;
    }


    public synchronized void addtoRequestQueue(final Request<?> request){
        if(NetWorkDialog.checkNetwokerStatus(context)){
            request.setTag(TAG);
            mRequestQueue.add(request);
        }else {
            Dialog.OnClickListener listener=new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    addtoRequestQueue(request);
                }
            };
            //NetWorkDialog.show(context,listener);  //有BUG 在從設定會回傳時 loadingDialog會後啟動,這個dialog會被蓋過去
        }
    }

    public void cancelPendingRequest(String tag){
        if(mRequestQueue!=null){
            mRequestQueue.cancelAll(tag);
        }
    }


}
