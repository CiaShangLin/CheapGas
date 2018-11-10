package com.shang.gascheap.OpencvCamera;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shang.gascheap.CreditCardData.CreditCard;
import com.shang.gascheap.Data.Setting;
import com.shang.gascheap.Main.MainActivity;
import com.shang.gascheap.R;
import com.shang.gascheap.Setting.SettingActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class OpencvCameraActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, OperatingCamera {

    private final String TAG = "OpencvCameraActivity";
    public final static int DATA_POST=1;

    public static ArrayList<CreditCard> cardList = new ArrayList<>();  //信用卡反序列化後的資料 有想要改換地方

    ImageView imageView;               //預定會刪除
    TextView tv;

    private MyView myView;
    private CameraBridgeViewBase mCVCamera;
    private Mat cameraMat;
    private FloatingActionButton mCameraButton;

    private double image_width, image_height;           //要擷取的範圍寬高
    private int scale_width, scale_height;                //座標
    Boolean cameraState = false;                       //可能會刪除 除非要留著讓使用者手動開啟掃描

    private ExecutorCompletionService completionService;    //考慮是否用成單例取得
    private ExecutorService executorService;
    private Recognition recognition;

    private int fail = 0;

    long start, end = 0;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            switch (msg.what) {
                case DATA_POST:
                    determineResult((ArrayList<Integer>)msg.obj);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opencv_camera);

        if (cardList.size() == 0)
            new CreditCard_Data_LoadTask(this).execute();    //跳出dialog 不能取消的  載入資料

        mCameraButton = (FloatingActionButton) findViewById(R.id.mCameraButton);
        mCameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cameraState == true) {
                    stopCamera();
                } else {
                    startCamera();
                }
            }
        });


        myView = (MyView) findViewById(R.id.myview);

        imageView = (ImageView) findViewById(R.id.imageView);
        tv = (TextView) findViewById(R.id.tv);

        mCVCamera = (CameraBridgeViewBase) findViewById(R.id.javaCamera);
        mCVCamera.enableView();
        mCVCamera.setCvCameraViewListener(this);

        start = System.currentTimeMillis();

    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        Log.v(TAG, "Camera: 寬:" + width + " 高:" + height);
        cameraMat = new Mat();

        image_width = myView.getMyViewWidth();                  //擷取的X長度
        image_height = myView.getMyViewHeight();                //擷取的Y長度
        Log.v(TAG, "MyView寬:" + image_width + " MyView高:" + image_height);

        scale_width = (int) ((width - image_width) / 2 );      //  相機預覽的寬減掉MyView的寬除2擷取的X座標
        scale_height = (int) ((height - image_height) / 2 );    //  相機預覽的高減掉MyView的高除2擷取的Y座標
        Log.v(TAG, "擷取X:" + scale_width + " 擷取Y:" + scale_height);

        //Log.v(TAG, "X:" + myView.getX() + " Y:" + myView.getY());
    }

    @Override
    public void onCameraViewStopped() {
        cameraMat.release();
        mCVCamera.disableView();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        cameraMat = inputFrame.rgba();

        if (cameraState && completionService!=null) {
            Mat copyMat = new Mat();
            cameraMat.copyTo(copyMat);  //複製MAT 不然下一個cameraMat會被洗掉
            final Mat c = copyMat.submat(new Rect(scale_width, scale_height,
                    (int) image_width , (int) image_height)); //切割完成

            end = System.currentTimeMillis();
            if (end - start >= 1000) {      //大概每一秒傳送一張
                start = end;
                //長86 寬54 縮小圖片,加快比對速度
                Imgproc.resize(c,c,new Size(430,270));
                completionService.submit(recognition.getRatioCallable(c));

                //new OutputImage().execute(c);
                Log.d(TAG, "發出線程");
            }

            Future future=completionService.poll();  //用take的話她會停來等到有結果再跑
            if(future!=null){
                Log.d(TAG,"FIND");
                try {
                    //不用hanlder的話 我怕相機會卡住
                    Message message=new Message();
                    message.what=DATA_POST;
                    message.obj=future.get();
                    handler.sendMessage(message);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
    }

        return cameraMat;
    }

    //線程鎖預防同時進入
    private synchronized void  determineResult(final ArrayList<Integer> highRatio) {
        Log.d(TAG,"Fail:"+fail);
        if (fail >= 10) {
            stopCamera();
            Snackbar.make(mCVCamera, "可能沒有您的信用卡", Snackbar.LENGTH_INDEFINITE)
                    .setAction("關閉", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            closeCamera();
                        }
                    })
                    .show();
            return;
        }

        if (highRatio.size() > 0) {
            Log.d(TAG, highRatio.size() + " " + highRatio.get(0) + " " + OpencvCameraActivity.cardList.get(highRatio.get(0)).getName());

            CardDialog cardDialog = CardDialog.newInstance(highRatio, OpencvCameraActivity.this);
            cardDialog.show(getFragmentManager(), "CARD");
            stopCamera();
        } else {
            Log.d(TAG, "0");
            fail++;
        }
    }

    @Override
    public void startCamera() {  //打開相機 打開線城池

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraButton.setImageResource(R.drawable.ic_camera);
                tv.setText("掃描中");
                mCVCamera.enableView();
            }
        });

        recognition = Recognition.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        completionService = new ExecutorCompletionService(executorService);
        cameraState = true;
    }

    @Override
    public void stopCamera() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mCameraButton.setImageResource(R.drawable.ic_nocamera);
                tv.setText("關閉中");
                mCVCamera.disableView();
            }
        });

        fail++;
        cameraState = false;
        executorService.shutdownNow();
        completionService = null;
    }

    @Override
    public void closeCamera() {
        mCVCamera.disableView();
        executorService.shutdownNow();
        completionService = null;

        Intent intent = new Intent(OpencvCameraActivity.this, SettingActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");

        mCVCamera.disableView();
        if(executorService!=null){
            executorService.shutdownNow();
        }
        completionService = null;
    }

}
