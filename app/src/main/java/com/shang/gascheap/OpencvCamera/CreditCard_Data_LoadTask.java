package com.shang.gascheap.OpencvCamera;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.shang.gascheap.Data.KryoUnits;
import com.shang.gascheap.LoadingDialog;

/**
 * Created by Shang on 2018/4/20.
 */

public class CreditCard_Data_LoadTask extends AsyncTask<Void, Void, Boolean>  {


    private final String TAG="CreditCard_Data_LoadTask";
    private LoadingDialog mLoadingDialog;
    private Context context;


    public CreditCard_Data_LoadTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

        mLoadingDialog=new LoadingDialog();
        mLoadingDialog.setCancelable(false);
        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.show(((Activity) context).getFragmentManager(),TAG);
            }
        });
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        OpencvCameraActivity.cardList.addAll(KryoUnits.load_from_sdcard(context));  //反序列化信用卡
        if (OpencvCameraActivity.cardList.size() == 0) {
            return false;
        }
        return true;
    }

    @Override
    protected void onPostExecute(Boolean flag) {

        ((Activity)context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mLoadingDialog.dismiss();
            }
        });

    }
}
