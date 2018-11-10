package com.shang.gascheap.OpencvCamera;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Environment;
import android.util.Log;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.Features2d;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.xfeatures2d.SURF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8UC3;

/**
 * Created by Shang on 2018/9/14.
 */

public class OutputImage extends AsyncTask<Mat,Void,Boolean> {


    @Override
    protected Boolean doInBackground(Mat... mats) {

        long time=System.currentTimeMillis();


        Bitmap bitmap=Bitmap.createBitmap(mats[0].width(),mats[0].height(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mats[0],bitmap);
        File file=new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                .getAbsolutePath()+"/"+time+".jpeg");
       //C:\Users\Shang\Documents\AndroidStudio\DeviceExplorer\asus-asus_z01kd-HAAZCY0219558WL\sdcard\Pictures
        try {
            FileOutputStream fileOutputStream=new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        if(file.exists())
            return true;
        return false;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);

        Log.d("OutputImage",aBoolean+"");

    }


}
