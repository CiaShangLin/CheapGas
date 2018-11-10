package com.shang.gascheap.VolleyUnits;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

/**
 * Created by Shang on 2018/10/4.
 */

//把Listener抽象化,實作兩個抽象Method就不需要handler回傳
public abstract class VolleyListenerInterface {

    Context mContext;
    public static Response.Listener<String> mLinstener;
    public static Response.ErrorListener mErrorLinstener;

    public VolleyListenerInterface(Context context,Response.Listener<String> listener,Response.ErrorListener errorListener){
        mContext=context;
        mLinstener=listener;
        mErrorLinstener=errorListener;
    }

    public abstract void onSucces(String response);
    public abstract void onError(VolleyError error);

    public Response.Listener<String> responseListener(){
        mLinstener=new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                onSucces(response);
            }
        };
        return mLinstener;
    }

    public Response.ErrorListener errorListener(){
        mErrorLinstener=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onError(error);
            }
        };
        return mErrorLinstener;
    }

}
