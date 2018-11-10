package com.shang.gascheap.GoogleMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.DrawableRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.shang.gascheap.Data.GasStationUnits;
import com.shang.gascheap.Data.Setting;
import com.shang.gascheap.LoadingDialog;
import com.shang.gascheap.Data.GasStation;

import com.shang.gascheap.Main.GasStationFragment;
import com.shang.gascheap.Main.MainActivity;
import com.shang.gascheap.Main.MyViewPagerAdapter;
import com.shang.gascheap.R;
import com.shang.gascheap.VolleyUnits.VolleyConstant;
import com.shang.gascheap.VolleyUnits.VolleyController;
import com.shang.gascheap.VolleyUnits.VolleyListenerInterface;
import com.shang.gascheap.VolleyUnits.VolleyRequestUnit;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    //等到要釋出release版的時候 要取得release版的sha1-key 然後註冊到google api consolo裡面
    //cmd cd到keystore的目錄 然後輸入 例如:keytool -list -v -keystore MediaPlayerByKotlin  這個得大概會是:keytool -list -v -keystore gascheap
    //這樣就可以看到SHA1了
    //https://blog.csdn.net/qq_27818541/article/details/51206596
    //https://console.developers.google.com/apis/credentials/key/59?project=thermal-rain-209112&hl=zh-tw

    private final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private MyInfoWindowAdapter myInfoWindowAdapter;
    private MyLocationGps myLocationGps = null;
    private FloatingActionButton nevigationBt;
    private FloatingActionButton favoriteGasBt;
    private LoadingDialog mLoadingDialog;
    protected ArrayList<GasStation> gasStationList = new ArrayList<>();
    private ArrayList<Marker> markerArrayList = new ArrayList<>();
    private Boolean status = false;
    private Marker nowMarker=null;

    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MyLocationGps.MYLOCATION:
                    Location mLocation = (Location) msg.obj;
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLocation.getLatitude()
                            , mLocation.getLongitude()), 12));   //移動畫面
                    loadMap(mLocation);
                    break;
                case GasStationUnits.GASSTION:  //直接點地圖過來

                    gasStationList = (ArrayList<GasStation>) msg.obj;
                    addMarkToMap(gasStationList, MyLocationGps.mLocation);
                    mLoadingDialog.dismiss();

                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Log.i(TAG, "onCreate");

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);

        //Loading畫面
        mLoadingDialog = new LoadingDialog();
        mLoadingDialog.show(getFragmentManager(), "GoogleMapLoading");

        nevigationBt = (FloatingActionButton) findViewById(R.id.nevigationBt);
        favoriteGasBt = (FloatingActionButton) findViewById(R.id.favoriteGasBt);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {  //點擊maker反應
            @Override
            public boolean onMarkerClick(final Marker marker) {
                if ((int) marker.getTag() != -1) {
                    marker.showInfoWindow();
                    nowMarker=marker;
                    nevigationBt.setVisibility(View.VISIBLE);
                    favoriteGasBt.setVisibility(View.VISIBLE);
                } else {
                    Toast.makeText(MapsActivity.this, "我的位置", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });


        mMap.setOnInfoWindowCloseListener(new GoogleMap.OnInfoWindowCloseListener() {    //點地圖info視窗關閉
            @Override
            public void onInfoWindowClose(Marker marker) {
                nevigationBt.setVisibility(View.INVISIBLE);
                favoriteGasBt.setVisibility(View.INVISIBLE);
            }
        });

        nevigationBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigationGastion(myLocationGps.mLocation, nowMarker.getPosition());
            }
        });

        favoriteGasBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String account = Setting.getInstance(MapsActivity.this).getAccount();
                final String gasid = String.valueOf(gasStationList.get((int) nowMarker.getTag()).getIdgas_station());
                Log.d(TAG, "user:" + account + " gasid:" + gasid);

                Map<String, String> map = new HashMap<String, String>();
                map.put(VolleyConstant.FAVORITEGAS_USER, account);
                map.put(VolleyConstant.FAVORITEGAS_GASID, gasid);

                VolleyRequestUnit.RequestPost(MapsActivity.this, VolleyConstant.FAVORITEGAS_URL,
                        "favoriteGasBt", map, favoriteGasBtListener);
            }
        });



        mMap.setMyLocationEnabled(true);   //右上角我的位置按鈕

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.973875, 120.982025), 7));

        //如果從MAIN點籍加油站過來 那他會傳那一群的加油站資料過來顯示
        //如果直接點地圖 那就是選是距離的加油站
        initGasStation();

        Log.i(TAG, "onMapReady");
    }

    VolleyListenerInterface favoriteGasBtListener = new VolleyListenerInterface(this,
            VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
        @Override
        public void onSucces(String response) {
            status = true;
            Toast.makeText(MapsActivity.this, "新增成功", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "response" + response);
        }

        @Override
        public void onError(VolleyError error) {
            Log.d(TAG, error.toString());
            if (error.getMessage() == null) {
                Toast.makeText(MapsActivity.this, "已有這個加油站", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MapsActivity.this, error.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    //匯入加油站
    private void initGasStation() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            gasStationList = (ArrayList<GasStation>) bundle.getSerializable(MyViewPagerAdapter.KEY);
            addMarkToMap(gasStationList, MyLocationGps.mLocation);
            int position = bundle.getInt(GasStationFragment.POSITION);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(gasStationList.get(position).getLatitude(), gasStationList.get(position).getLongitude()), 12));

            nowMarker=markerArrayList.get(position);
            markerArrayList.get(position).showInfoWindow();
            favoriteGasBt.setVisibility(View.VISIBLE);
            nevigationBt.setVisibility(View.VISIBLE);
            mLoadingDialog.dismiss();
        } else {
            Log.d(TAG, "NOT DATA");
            myLocationGps = MyLocationGps.getInstance(MapsActivity.this);
            if (myLocationGps != null) {
                myLocationGps.getMyLocation(this);
            }
        }
    }

    //開啟導航 打開googlemap導航
    private void navigationGastion(Location mLocation, LatLng latLng) {
        String myLoaction = "saddr=" + mLocation.getLatitude() + "," + mLocation.getLongitude();
        String myDestination = "daddr=" + latLng.latitude + "," + latLng.longitude;
        String uriString = "http://maps.google.com/maps?" + myLoaction + "&" + myDestination;
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uriString));
        startActivity(intent);
    }

    //取到位置的經緯度後 匯入加油站
    public void loadMap(Location location) {
        Log.i(TAG, "LoadMap");
        if (location == null) {
            Toast.makeText(this, "所在位置無法接收GPS", Toast.LENGTH_LONG).show();
        } else {
            gasStationList.clear();
            GasStationUnits.getGasStaion(MapsActivity.this, location, 1);
        }
    }

    //匯入加油站
    private void addMarkToMap(ArrayList<GasStation> gasStationList, Location mLocation) {
        for (int i = 0; i < gasStationList.size(); i++) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(new LatLng(gasStationList.get(i).getLatitude(), gasStationList.get(i).getLongitude()));  //緯經
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(getBitmap(MapsActivity.this, R.drawable.ic_gas_station)));
            //這個icon是換掉原本那個紅色的 不是訊息框裡的ICON
            Marker marker = mMap.addMarker(markerOptions);
            marker.setTag(i);
            markerArrayList.add(marker);
        }

        mMap.addMarker(new MarkerOptions().
                position(new LatLng(mLocation.getLatitude(), mLocation.getLongitude()))
                .title("我的位置")
        ).setTag(-1);


        //這個時候gasStationList才有資料
        myInfoWindowAdapter = new MyInfoWindowAdapter(MapsActivity.this, MyLocationGps.mLocation, gasStationList);
        mMap.setInfoWindowAdapter(myInfoWindowAdapter);    //點擊marker後 彈出的客製化視窗
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {   //接收是否有開啟GPS的結果
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case MyLocationGps.GPS_OPEN:
                if (resultCode == 0) {
                    myLocationGps = MyLocationGps.getInstance(MapsActivity.this);
                    if (myLocationGps != null) {
                        myLocationGps.getMyLocation(this);
                    }
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (myLocationGps != null) {
            myLocationGps.requestLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (myLocationGps != null) {
            myLocationGps.removeLocationUpdates();
        }
    }

    public static Bitmap getBitmap(Context context, @DrawableRes int drawableId) {  //支援svg用的,如果是png就不需要了

        Drawable drawable = ContextCompat.getDrawable(context, drawableId);

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent=new Intent(MapsActivity.this,MainActivity.class);
            intent.putExtra(MainActivity.RETURN,status);
            setResult(MainActivity.ACTIVITY_RETURN,intent);
        }
        return super.onKeyDown(keyCode, event);
    }

}
