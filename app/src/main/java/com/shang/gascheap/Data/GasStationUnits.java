package com.shang.gascheap.Data;

import android.content.Context;
import android.location.Location;
import android.os.Message;
import android.util.Log;

import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.shang.gascheap.GoogleMap.MapsActivity;
import com.shang.gascheap.Main.MainActivity;
import com.shang.gascheap.VolleyUnits.VolleyConstant;
import com.shang.gascheap.VolleyUnits.VolleyController;
import com.shang.gascheap.VolleyUnits.VolleyListenerInterface;
import com.shang.gascheap.VolleyUnits.VolleyRequestUnit;

import org.json.JSONArray;
import org.json.JSONException;
import org.opencv.features2d.Features2d;

import java.util.ArrayList;

/**
 * Created by Shang on 2018/9/22.
 */

public class GasStationUnits {

    private static final String TAG = "GasStationUnits";
    private static final String TAG_DISTANCE = "getGasStaionByDistance";
    private static final String TAG_PRICE = "getGasStaionByPrice";
    private static final String TAG_FAVORITE = "getGasStaionByFavorite";
    public static final int GASSTION = 2;
    private static int limit = 0;

    public static void getGasStaion(final Context context, final Location mLocation, final int arg) {
        String tag = "";
        String URL = "";

        int distance = Integer.parseInt(Setting.getInstance(context).getDistance());
        double Longitude = mLocation.getLongitude();
        double Latitude = mLocation.getLatitude();
        String account = Setting.getInstance(context).getAccount();
        //http://140.134.26.71:8887/gps/120.646740/24.178693/3/aaa
        // url+Longitude+Latitude+distance+user
        switch (arg) {
            case 1:
                //大的數字在前 先經度在緯度 最後距離
                URL = VolleyConstant.GOOGLEMAP_URL + Longitude + "/" + Latitude + "/" + distance + "/" + account;
                tag = TAG_DISTANCE;
                break;
            case 2:
                URL = VolleyConstant.CHEAP_URL + Longitude + "/" + Latitude + "/" + distance + "/" + account;
                tag = TAG_PRICE;
                break;
            case 3:
                URL = VolleyConstant.FAVORITEGAS_URL + "/" + Longitude + "/" + Latitude + "/" + distance + "/" + account;
                tag = TAG_FAVORITE;
                break;
        }
        Log.d(TAG, URL);


        final String finalTag = tag;
        VolleyRequestUnit.RequestGet(context, URL, tag,
                new VolleyListenerInterface(context, VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
                    @Override
                    public void onSucces(String response) {
                        //Log.d(TAG, finalTag +" "+response);
                        final ArrayList<GasStation> gasStationList = new ArrayList<GasStation>();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                gasStationList.add(jsonToGasStation(jsonArray.getJSONArray(i)));
                            }
                            handler(gasStationList, arg, context);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VolleyError error) {
                        Log.d(TAG, finalTag + " " + error.toString());
                        if (error instanceof NetworkError && limit < 4) {
                            getGasStaion(context, mLocation, arg);
                        } else {
                            //limit=0;
                        }
                    }
                });
    }


    private static GasStation jsonToGasStation(JSONArray jsonArray) {
        GasStation gasStation = null;
        try {
            gasStation = new GasStation((int) jsonArray.get(0), String.valueOf(jsonArray.get(1)), (double) jsonArray.get(2), (double) jsonArray.get(3)
                    , (double) jsonArray.get(4), (double) jsonArray.get(5), (double) jsonArray.get(6), (double) jsonArray.get(7), (double) jsonArray.get(8)
                    , (String) jsonArray.get(9), (double) jsonArray.get(10), (int) jsonArray.get(11), (String) jsonArray.get(12),
                    (String) jsonArray.get(13), (int) jsonArray.get(14), (double) jsonArray.get(15), (double) jsonArray.get(16), (double) jsonArray.get(17));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gasStation;
    }

    private static void handler(ArrayList<GasStation> gasStationList, int arg, Context context) {
        Message message = new Message();
        message.what = GASSTION;
        message.obj = gasStationList;
        message.arg1 = arg;
        if (context instanceof MainActivity) {
            ((MainActivity) context).handler.sendMessage(message);
        } else if (context instanceof MapsActivity) {
            ((MapsActivity) context).handler.sendMessage(message);
        }

    }
}
