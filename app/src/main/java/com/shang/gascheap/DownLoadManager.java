package com.shang.gascheap;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.shang.gascheap.CreditCardData.CreditCardConstant;
import com.shang.gascheap.CreditCardData.CreditCardName;
import com.shang.gascheap.VolleyUnits.VolleyConstant;

import java.io.File;

import static android.content.Context.DOWNLOAD_SERVICE;
import static com.shang.gascheap.CreditCardData.CreditCardConstant.CreditCard_Amount;

/**
 * Created by Shang on 2018/10/13.
 */

public class DownLoadManager extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "DownLoadManager";

    private Activity mActivity;
    private DownloadDialog mDownloadDialog;

    public DownLoadManager(Activity activity) {
        mActivity = activity;
        mDownloadDialog = DownloadDialog.getInstance();
        mDownloadDialog.show(activity.getFragmentManager(), "DownloadDialog");
    }

    public static Boolean checkFile(Context context) {
        Boolean flag = false; // true的話代表有缺少或是根本沒有
        for (int i = 1; i <= CreditCardConstant.CreditCard_Amount; i++) {
            File file = new File(CreditCardConstant.getCC(context) + i + ".bin");
            if (!file.exists()) {
                flag = true;
                break;
            }
            Log.v(TAG, file.getAbsolutePath() + " " + file.getName() + " " + file.exists());
        }
        Log.v(TAG, "flag:"+flag);
        return flag;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        // sdcard/android/com.shang.gascheap/files/CreditCardDir/     android studio放的位置
        DownloadManager downloadManager = (DownloadManager) mActivity.getSystemService(DOWNLOAD_SERVICE);
        for (int i = 1; i <= CreditCard_Amount; i++) {
            File file = new File(CreditCardConstant.getCC(mActivity) + i + ".bin");
            if (!file.exists()) {
                String URL = VolleyConstant.DOWNLOAD_CREDIT_URL + i + ".bin";
                String fileName = CreditCardConstant.CreditCard + i + ".bin";
                String dirName = CreditCardConstant.CreditCardDir;

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(URL));
                request.setDestinationInExternalFilesDir(mActivity, dirName, fileName);
                request.setTitle(fileName);
                request.setDescription("下載中...");
                downloadManager.enqueue(request);

                Log.d(TAG,"downloadManager:"+fileName);
            }
            mDownloadDialog.setProgressBar(i);
        }


        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        mDownloadDialog.dismiss();

        Log.d(TAG, "result:" + aBoolean);
    }
}
