package com.shang.gascheap.Main;

import android.app.Activity;
import android.content.Intent;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shang.gascheap.Data.GasPrice;
import com.shang.gascheap.GoogleMap.MapsActivity;
import com.shang.gascheap.Setting.SettingActivity;
import com.shang.gascheap.VolleyUnits.VolleyConstant;
import com.shang.gascheap.VolleyUnits.VolleyController;
import com.shang.gascheap.VolleyUnits.VolleyListenerInterface;
import com.shang.gascheap.VolleyUnits.VolleyRequestUnit;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;


//BottomNavigationView 裡面呼叫的方法 寫在MAIN實在是太佔空間了
public class BNV_Method {
    private static final String PRE_TAG = "PRE_TAG"; //中油
    private static final String CPC_TAG = "CPC_TAG"; //中油
    private static final String FPG_TAG = "FPG_TAG"; //台塑
    static final HashMap<String, GasPrice> map = new HashMap<>();
    private static String prediction = null;

    //油價表
    protected static void showGasPrice(final Activity activity, final BottomNavigationView mBottomNavigationView) {
        map.clear();
        prediction="";

        VolleyRequestUnit.RequestGet(activity, VolleyConstant.GASPRICE_1, CPC_TAG, new VolleyListenerInterface(activity,
                VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
            @Override
            public void onSucces(String response) {
                map.put(GasPriceDialog.CPC, responseToGasPrice(response));
                showGasPriceDialog(activity,mBottomNavigationView);
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(CPC_TAG, error.getMessage() + "");
            }
        });

        VolleyRequestUnit.RequestGet(activity, VolleyConstant.GASPRICE_2, FPG_TAG, new VolleyListenerInterface(activity
                , VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
            @Override
            public void onSucces(String response) {
                map.put(GasPriceDialog.FPG, responseToGasPrice(response));
                showGasPriceDialog(activity,mBottomNavigationView);
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(FPG_TAG, error.getMessage() + "");
            }
        });

        VolleyRequestUnit.RequestGet(activity, VolleyConstant.GASPREDICTION, PRE_TAG, new VolleyListenerInterface(activity,
                VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
            @Override
            public void onSucces(String response) {
                prediction = GasPrediction.UnicodeToString(response).replace("\\n", "");
                prediction=prediction.substring(1,prediction.length()-1);
                showGasPriceDialog(activity,mBottomNavigationView);
            }

            @Override
            public void onError(VolleyError error) {
                Log.d(PRE_TAG, error.getMessage() + "");
            }
        });
    }

    private static GasPrice responseToGasPrice(String response) {
        GasPrice gasPrice = new GasPrice();
        try {
            JSONArray jsonArray = new JSONArray(response);
            gasPrice.setId(jsonArray.getInt(0));
            gasPrice.setName(jsonArray.getString(1));
            gasPrice.setGas92(jsonArray.getDouble(2));
            gasPrice.setGas95(jsonArray.getDouble(3));
            gasPrice.setGas98(jsonArray.getDouble(4));
            gasPrice.setDiesel(jsonArray.getDouble(5));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return gasPrice;
    }

    private static void showGasPriceDialog(Activity activity, BottomNavigationView mBottomNavigationView) {
        if (map.size() == 2 && !prediction.equals("")) {
            GasPriceDialog gasPriceDialog = GasPriceDialog.newInstance(map,prediction);
            gasPriceDialog.show(activity.getFragmentManager(), GasPriceDialog.KEY);
            mBottomNavigationView.getMenu().getItem(0).setEnabled(true);
        }
    }


    //設定
    protected static void showSetting(Activity activity, BottomNavigationView mBottomNavigationView) {
        Intent intent = new Intent(activity.getBaseContext(), SettingActivity.class);
        activity.startActivityForResult(intent, MainActivity.ACTIVITY_RETURN);
        mBottomNavigationView.getMenu().getItem(1).setEnabled(true);
    }


    protected static void goGoogleMap(Activity activity) {
        //當初複製了這個專案,google map金鑰是用了opencv2410的 所以不能用從創了一個
        Intent intent1 = new Intent(activity.getBaseContext(), MapsActivity.class);
        activity.startActivityForResult(intent1, MainActivity.ACTIVITY_RETURN);
    }
}
