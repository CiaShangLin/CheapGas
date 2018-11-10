package com.shang.gascheap.OpencvCamera;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shang.gascheap.Data.Setting;
import com.shang.gascheap.R;
import com.shang.gascheap.SharedPreConstant;
import com.shang.gascheap.SharedPreOperating;
import com.shang.gascheap.VolleyUnits.VolleyConstant;
import com.shang.gascheap.VolleyUnits.VolleyController;
import com.shang.gascheap.VolleyUnits.VolleyListenerInterface;
import com.shang.gascheap.VolleyUnits.VolleyRequestUnit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class CardDialog extends DialogFragment {

    private static final String TAG = "CardDialog";
    private static String KEY = "CardDialog";
    private static CardDialog cardDialog = null;
    private static OperatingCamera mOperatingCamera;


    public static CardDialog newInstance(ArrayList<Integer> cardList, OperatingCamera operatingCamera) {
        if (cardDialog == null) {
            cardDialog = new CardDialog();
        }
        mOperatingCamera = operatingCamera;
        Bundle bundle = new Bundle();
        bundle.putIntegerArrayList(KEY, cardList);
        cardDialog.setArguments(bundle);
        return cardDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        final ArrayList<Integer> list = bundle.getIntegerArrayList(KEY);   //取得相似大於70%的信用卡ID

        //CardDialog的資料
        final String[] cardList = new String[list.size() + 1];
        for (int i = 0; i < list.size(); i++) {
            int index = list.get(i);
            cardList[i] = OpencvCameraActivity.cardList.get(index).getName();  //用index去對應在cardList的資料
        }
        cardList[list.size()] = "沒有你的信用卡";

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_creditcard)
                .setTitle("請選擇信用卡")
                .setItems(cardList, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, final int which) {
                        if (which == cardList.length - 1) {  //都沒有
                            mOperatingCamera.startCamera();
                        } else {                        //有 要記錄下來
                            Map<String, String> map = new HashMap<String, String>();
                            //使用者帳戶

                            String account = Setting.getInstance(getActivity()).getAccount();
                            //這裡有點複雜,連我自己都忘了,這時序列化信用卡時,已經塞好Bank跟id的,所以直接用,不用enum那邊的
                            String id = OpencvCameraActivity.cardList.get(list.get(which)).getBank()
                                    + OpencvCameraActivity.cardList.get(list.get(which)).getId();
                            Log.d(TAG, "account:" + account + " id:" + id + " name:" + OpencvCameraActivity.cardList.get(list.get(which)).getName());
                            map.put(VolleyConstant.CREDIT_NAME, account);
                            map.put(VolleyConstant.CREDIT_ID, id);

                            VolleyRequestUnit.RequestPost(getActivity(), VolleyConstant.CREDIT_URL, TAG, map, mVolleyListenerInterface);

                            mOperatingCamera.stopCamera();  //停止相機
                        }
                    }
                });
        return alertDialog.create();
    }


    private VolleyListenerInterface mVolleyListenerInterface = new VolleyListenerInterface(getActivity()
            , VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
        @Override
        public void onSucces(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString(VolleyConstant.CREDIT_MSG);

                switch (status) {
                    case VolleyConstant.CREDIT_MSG_ADD:
                        mOperatingCamera.closeCamera();
                        break;
                    case VolleyConstant.CREDIT_MSG_NO_USER:
                        break;
                    default:
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(VolleyError error) {
            //後臺資料庫 有設定不能重複信用卡卡號
            Log.d(TAG, "Error:" + error.toString());
            mOperatingCamera.closeCamera();
        }

    };

}
