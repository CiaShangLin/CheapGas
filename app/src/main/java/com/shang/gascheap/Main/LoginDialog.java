package com.shang.gascheap.Main;

import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shang on 2018/4/28.
 */

public class LoginDialog extends DialogFragment {

    public static final String TAG = "LoginDialog";

    private static LoginDialog loginDialog = null;
    private EditText account;
    private EditText password;
    private Button loginBt;
    private Button registerBt;

    public static LoginDialog newInstance() {
        if (loginDialog == null) {
            loginDialog = new LoginDialog();
        }
        return loginDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_login, container, false);
        account = view.findViewById(R.id.accountText);
        password = view.findViewById(R.id.passwordText);
        loginBt = view.findViewById(R.id.loginBt);
        registerBt = view.findViewById(R.id.registerBt);
        TextView loginTitle = view.findViewById(R.id.loginTitle);

        loginTitle.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/black.ttf"));

        account.setText(Setting.getInstance(getActivity()).getAccount());
        password.setText(Setting.getInstance(getActivity()).getPassword());

        loginBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (account.getText().toString().length() == 0 || password.getText().toString().length() == 0) {
                    Toast.makeText(getActivity(), "帳密不能為空", Toast.LENGTH_SHORT).show();
                } else {
                    Setting.getInstance(getActivity()).setAccount(account.getText().toString().trim());
                    Setting.getInstance(getActivity()).setPassword(password.getText().toString().trim());

                    Map<String, String> map = new HashMap<String, String>();
                    map.put(VolleyConstant.LOGIN_NAME, account.getText().toString());
                    map.put(VolleyConstant.LOGIN_SECRET, password.getText().toString());

                    VolleyRequestUnit.RequestPost(getActivity(), VolleyConstant.LOGIN_URL, TAG, map, mVolleyListenerInterface);

                    loginBt.setFocusable(false);
                    loginBt.setEnabled(false);
                }
            }
        });

        registerBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisteredDialog registeredDialog = RegisteredDialog.newInstance();
                registeredDialog.show(getFragmentManager(), "Registered");
                dismiss();
            }
        });
        return view;
    }

    private VolleyListenerInterface mVolleyListenerInterface = new VolleyListenerInterface(getActivity(),
            VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
        @Override
        public void onSucces(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString(VolleyConstant.LOGIN_MSG);
                switch (status) {
                    case VolleyConstant.LOGIN_MSG_NO_USER:
                        Toast.makeText(getActivity(), "沒有這個帳號", Toast.LENGTH_SHORT).show();
                        break;
                    case VolleyConstant.LOGIN_MSG_ERROR_SECRET:
                        Toast.makeText(getActivity(), "密碼錯誤", Toast.LENGTH_SHORT).show();
                        break;
                    case VolleyConstant.LOGIN_MSG_POST:
                        Toast.makeText(getActivity(), "登入成功", Toast.LENGTH_SHORT).show();
                        dismiss();
                        Message message = new Message();
                        message.what = MainActivity.LOGIN_DIALOG;
                        ((MainActivity) getActivity()).handler.sendMessage(message);//用context的話 沒辦法轉成Main
                        break;
                }
                Log.d(VolleyConstant.TAG, status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.d(VolleyConstant.TAG, error.toString());
            Toast.makeText(getActivity(), "發生錯誤:" + error.toString(), Toast.LENGTH_SHORT).show();
            loginBt.setFocusable(true);
            loginBt.setEnabled(true);
        }
    };


    @Override
    public int getTheme() {
        //Dialog全螢幕
        return R.style.FullDialog;
    }

}
