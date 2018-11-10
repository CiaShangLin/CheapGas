package com.shang.gascheap.GoogleMap;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.shang.gascheap.LoadingDialog;
import com.shang.gascheap.Main.MainActivity;

/**
 * Created by Shang on 2018/7/7.
 */

//改設計成只能取到經緯度 不要跟UI有太大的關係
public class MyLocationGps {

    private static final String TAG = "MyLocationGps";
    public static final int GPS_OPEN=1;
    public static final int MYLOCATION = 1;

    private static final MyLocationGps myLocationGps = new MyLocationGps();
    private static FusedLocationProviderClient mFusedLocationClient;
    private static LocationRequest mLocationRequest;              //location的要求參數
    private static SettingsClient mSettingsClient;                //檢查配置用
    private static LocationSettingsRequest.Builder builder;
    private static LocationSettingsRequest mLocationSettingsRequest;  //設定配置
    public static Location mLocation = null;   //我的經緯度
    private static LoadingDialog loadingDialog=null;

    private static Activity mActivity;

    public static MyLocationGps getInstance(Activity activity) {
        if(isGPSOpen(activity)){
            myLocationGps.initFusedLocationProviderClient(activity);
            return myLocationGps;
        }
        return null;
    }

    private static Boolean isGPSOpen(final Activity activity) {   //檢查GPS有沒有打開
        mActivity=activity;
        if (((LocationManager)activity.getBaseContext().getSystemService(Context.LOCATION_SERVICE))
                .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Log.i(TAG, "定位GPS已打開");
            return true;
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle("GPS定位")
                    .setMessage("是否前往打開GPS定位")
                    .setPositiveButton("前往", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            activity.startActivityForResult(intent, GPS_OPEN);
                        }
                    }).show();
        }
        return false;
    }

    //初始化FusedLocationProviderClient
    public void initFusedLocationProviderClient(final Activity activity) {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);

        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);        //更新時間
        mLocationRequest.setFastestInterval(1000);  //更新速率
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);  //高精準度

        builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest);
        mLocationSettingsRequest = builder.build();

        mSettingsClient = LocationServices.getSettingsClient(activity);
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(
                new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) { //條件都滿足  模擬器會失敗不知道為什麼
                        Log.d(TAG, "onSuccess setting:" + locationSettingsResponse.getLocationSettingsStates().isGpsUsable());
                        requestLocationUpdates(); //註冊更新位置
                        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                if(location!=null) {
                                    Log.d(TAG, "onSuccess setting:" + mLocation.getLongitude()+" "+mLocation.getLatitude());
                                    mLocation = location;
                                }
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure setting:" + e.getMessage());
            }
        });

    }


    private LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            if (locationResult.getLastLocation() != null) {
                mLocation = locationResult.getLastLocation();
                Log.d(TAG, "mLocationCallback:" + mLocation.getLongitude() + " " + mLocation.getLatitude());
            }
        }

        @Override
        public void onLocationAvailability(LocationAvailability locationAvailability) {
            super.onLocationAvailability(locationAvailability);
            Log.v(TAG, locationAvailability.toString());
        }
    };


    @SuppressLint("MissingPermission")
    public void getMyLocation(final Context context) {   //取得位置 這個麻煩的是他取到的值不一定會有
        mFusedLocationClient.getLastLocation().addOnSuccessListener(mActivity,new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(loadingDialog==null){
                    loadingDialog=new LoadingDialog();
                    loadingDialog.show(((Activity)context).getFragmentManager(),"loadingDialog2");
                }

                if(location!=null){
                    loadingDialog.dismiss();
                    loadingDialog=null;

                    mLocation=location;
                    Message message=new Message();
                    message.what=MYLOCATION;
                    message.obj=location;
                    if(context instanceof MainActivity){
                        ((MainActivity)context).handler.sendMessage(message);
                    }else if(context instanceof MapsActivity){
                        ((MapsActivity)context).handler.sendMessage(message);
                    }
                }else{
                    Log.d(TAG,"location=null");
                    getMyLocation(context);
                }
            }
        });
    }


    @SuppressLint("MissingPermission")
    public void requestLocationUpdates() {  //註冊隨時間更新
        if (mFusedLocationClient != null){
            Log.d(TAG,"註冊更新地點");
            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        }
    }

    public void removeLocationUpdates() {    //註銷
        if (mFusedLocationClient != null){
            Log.d(TAG,"註銷更新地點");
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }

    }

    public static String getDistance(double lat1, double lng1, double lat2, double lng2) {  //兩點經緯度計算距離 單位:公里km
        double radLat1 = lat1 * Math.PI / 180.0;
        double radLat2 = lat2 * Math.PI / 180.0;
        double a = radLat1 - radLat2;// 两点纬度差
        double b = (lng1 * Math.PI / 180.0) - (lng2 * Math.PI / 180.0);// 两点的经度差
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1)
                * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
        s = s * 6378.137;
        return String.format("%.2fkm",s);
    }


}
