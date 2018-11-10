package com.shang.gascheap.Main;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;

import android.location.Location;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.shang.gascheap.Data.GasPrice;
import com.shang.gascheap.Data.GasStation;
import com.shang.gascheap.Data.GasStationUnits;
import com.shang.gascheap.Data.PermissionUnit;

import com.shang.gascheap.Data.Setting;
import com.shang.gascheap.DownLoadManager;
import com.shang.gascheap.GoogleMap.MyLocationGps;
import com.shang.gascheap.LoadingDialog;
import com.shang.gascheap.NetWorkDialog;
import com.shang.gascheap.OpencvCamera.OutputImage;
import com.shang.gascheap.R;
import com.shang.gascheap.SharedPreOperating;
import com.shang.gascheap.VolleyUnits.VolleyConstant;
import com.shang.gascheap.VolleyUnits.VolleyListenerInterface;
import com.shang.gascheap.VolleyUnits.VolleyRequestUnit;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.FlannBasedMatcher;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.xfeatures2d.SURF;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity {

    static {
        //我記得是要匯入.so檔用的
        System.loadLibrary("native-lib");
        if (OpenCVLoader.initDebug()) {
            Log.e("TAG", "Cannot load OpenCV library");
        }
    }

    private static final String TAG = "MainActivity";
    public static final int LOGIN_DIALOG = 0;
    public static final int ACTIVITY_RETURN = 2;
    public static final String RETURN = "RETURN";

    private BottomNavigationView mBottomNavigationView;
    private ViewPager mViewPager = null;
    private MyViewPagerAdapter myViewPagerAdapter = null;
    private static MyLocationGps myLocationGps;
    protected static HashMap<Integer, ArrayList<GasStation>> gasStationMap = new HashMap<>();
    private static LoadingDialog loadingDialog = null;


    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {

                case LOGIN_DIALOG:
                    Log.d(TAG, "MyLocationGps.getInstance");
                    myLocationGps = MyLocationGps.getInstance(MainActivity.this);
                    if (myLocationGps != null) {
                        myLocationGps.getMyLocation(MainActivity.this);
                    }
                    break;
                case MyLocationGps.MYLOCATION:
                    Log.d(TAG, "MyLocationGps.MYLOCATION");

                    gasStationMap.clear();
                    loadingDialog.show(getFragmentManager(), "loadingDialog");
                    Log.d(TAG,getFragmentManager().findFragmentByTag("loadingDialog").isAdded()+"");

                    GasStationUnits.getGasStaion(MainActivity.this, (Location) msg.obj, 1);
                    GasStationUnits.getGasStaion(MainActivity.this, (Location) msg.obj, 2);
                    GasStationUnits.getGasStaion(MainActivity.this, (Location) msg.obj, 3);

                    break;
                case GasStationUnits.GASSTION:
                    //1=距離 2=價格 3=最愛
                    gasStationMap.put(msg.arg1, (ArrayList<GasStation>) msg.obj);
                    if (gasStationMap.size() == 3) {
                        if (myViewPagerAdapter == null) {
                            Log.d(TAG, "ViewPager初始化");
                            myViewPagerAdapter = new MyViewPagerAdapter(getSupportFragmentManager(), gasStationMap);
                            mViewPager.setAdapter(myViewPagerAdapter);
                        } else {
                            Log.d(TAG, "ViewPager更新");
                            myViewPagerAdapter.update(gasStationMap);
                        }
                        for (int i = 1; i <= gasStationMap.size(); i++) {
                            Log.d(TAG, i + " " + gasStationMap.get(i).size() + "");
                        }
                        loadingDialog.dismiss();
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //第一次啟動 改了launchMode所以oncreate理論上 只會啟動一次
        LoginDialog loginDialog = LoginDialog.newInstance();
        //loginDialog.show(getFragmentManager(), LoginDialog.TAG);

        if (!PermissionUnit.checkSelfPermission(this, PermissionUnit.PERMISSION)) {   //檢查權限
            PermissionUnit.requestPermissions(this, PermissionUnit.PERMISSION);
        } else {
            init();
        }

    }

    private void init() {  //要取得權限後,才能做的事
        Log.d(TAG, "INIT");

        loadingDialog = new LoadingDialog();

        if (DownLoadManager.checkFile(this)) {
            new DownLoadManager(this).execute();
        }

        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.gaspriceItem:
                        mBottomNavigationView.getMenu().getItem(0).setEnabled(false);
                        BNV_Method.showGasPrice(MainActivity.this, mBottomNavigationView);
                        break;
                    case R.id.settingItem:
                        mBottomNavigationView.getMenu().getItem(1).setEnabled(false);
                        BNV_Method.showSetting(MainActivity.this, mBottomNavigationView);
                        break;
                    case R.id.googlemapItem:
                        BNV_Method.goGoogleMap(MainActivity.this);
                        break;
                }
                return true;
            }
        });


        Button button = (Button) findViewById(R.id.downloadBt);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("grantResults", "grantResults size:" + grantResults.length);  //按答應跟不答應 size都一樣 5
        //按是或否requseCode都匯回傳我設定的
        switch (requestCode) {
            case PermissionUnit.REQUESTCODE:
                boolean flag = true;
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        flag = false;
                    }
                }
                if (flag) {
                    Log.v(TAG, "權限全部通過");
                    init();
                } else {
                    //這裡還要做一些處理
                    Log.v(TAG, "權限未通過");
                }
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  //接收是否有開啟GPS的結果 和返回到MAIN時資料刷新用
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, requestCode + " " + resultCode);

        switch (requestCode) {
            case MyLocationGps.GPS_OPEN:
                if (resultCode == 0) {  //0=代表有打開
                    myLocationGps = MyLocationGps.getInstance(MainActivity.this);
                    if (myLocationGps != null) {
                        myLocationGps.getMyLocation(this);
                    }
                }
                break;
            case ACTIVITY_RETURN:
                Log.v(TAG, "ACTIVITY_RETURN ");
                if (data != null && data.getBooleanExtra(RETURN, true)) {
                    Message message = new Message();
                    if (myLocationGps == null || myLocationGps.mLocation == null) {
                        message.what = LOGIN_DIALOG;
                    } else {
                        message.what = MyLocationGps.MYLOCATION;
                        message.obj = myLocationGps.mLocation;
                    }
                    handler.sendMessage(message);
                }
                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (myLocationGps != null) {
            myLocationGps.requestLocationUpdates();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (myLocationGps != null) {
            myLocationGps.removeLocationUpdates();
        }
    }
}
