package com.shang.gascheap.VolleyUnits;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import java.util.Map;

/**
 * Created by Shang on 2018/10/4.
 */

public class VolleyRequestUnit {

    public static void RequestGet(Context context,String url,String tag,VolleyListenerInterface volleyListenerInterface){
        VolleyController.getInstance(context).cancelPendingRequest(tag);

        StringRequest stringRequest=new StringRequest(Request.Method.GET,url
                ,volleyListenerInterface.responseListener()
                ,volleyListenerInterface.errorListener());
        stringRequest.setTag(tag);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleyController.getInstance(context).addtoRequestQueue(stringRequest);

    }

    public static void RequestPost(Context context, String url, String tag, final Map<String, String> params, VolleyListenerInterface volleyListenerInterface){
        VolleyController.getInstance(context).cancelPendingRequest(tag);

        StringRequest stringRequest=new StringRequest(Request.Method.POST,url
                ,volleyListenerInterface.responseListener()
                ,volleyListenerInterface.errorListener()){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return params;
            }
        };
        stringRequest.setTag(tag);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleyController.getInstance(context).addtoRequestQueue(stringRequest);
    }
}
