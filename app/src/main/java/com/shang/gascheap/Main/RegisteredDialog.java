package com.shang.gascheap.Main;

import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
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
import com.shang.gascheap.Setting.SettingActivity;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegisteredDialog extends DialogFragment {

    private static final String TAG = "RegisteredDialog";
    private static RegisteredDialog registeredDialog = null;

    public static RegisteredDialog newInstance() {//姓名 電話 email,密碼
        if (registeredDialog == null) {
            registeredDialog = new RegisteredDialog();
        }
        return registeredDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_registered, container, false);
        final EditText account = (EditText) view.findViewById(R.id.account);
        final EditText password = (EditText) view.findViewById(R.id.password);
        final EditText phone = (EditText) view.findViewById(R.id.phone);
        final EditText email = (EditText) view.findViewById(R.id.email);
        final Button register = (Button) view.findViewById(R.id.register);
        final Button backtrack = (Button) view.findViewById(R.id.backtrack);
        TextView registerTv = (TextView) view.findViewById(R.id.registerTv);

        registerTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(), "fonts/black.ttf"));

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mAccount = account.getText().toString().trim();
                String mPassword = password.getText().toString().trim();
                String mPhone = phone.getText().toString().trim();
                String mEmail = email.getText().toString().trim();

                if (checkPattern(mAccount, "[A-Za-z0-9]+") || checkPattern(mPassword, "[A-Za-z0-9]+")) {
                    Toast.makeText(getActivity(), "帳密,信箱不能為空", Toast.LENGTH_SHORT).show();
                } else if (checkPattern(mPhone, "[0-9]{10}")) {
                    Toast.makeText(getActivity(), "手機要為10碼", Toast.LENGTH_SHORT).show();
                } else {
                    Setting.getInstance(getActivity()).setAccount(mAccount);
                    Setting.getInstance(getActivity()).setPassword(mPassword);
                    Setting.getInstance(getActivity()).setPhone(mPhone);
                    Setting.getInstance(getActivity()).setEmail(mEmail);

                    Map<String, String> map = new HashMap<String, String>();
                    map.put(VolleyConstant.SIGNUP_NAME, mAccount);
                    map.put(VolleyConstant.SIGNUP_SECRET, mPassword);
                    map.put(VolleyConstant.SIGNUP_EMAIL, mEmail);
                    map.put(VolleyConstant.SIGNUP_PHONE, mPhone);

                    VolleyRequestUnit.RequestPost(getActivity(), VolleyConstant.SIGNUP_URL, TAG, map, mVolleyListenerInterface);


                }
            }
        });

        backtrack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginDialog loginDialog = LoginDialog.newInstance();
                loginDialog.show(getFragmentManager(), LoginDialog.TAG);
                dismiss();
            }
        });

        return view;

    }

    private VolleyListenerInterface mVolleyListenerInterface = new VolleyListenerInterface(getActivity()
            , VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
        @Override
        public void onSucces(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString(VolleyConstant.SIGNUP_MSG);
                switch (status) {
                    case VolleyConstant.SIGNUP_MSG_SIGNUPPOST:
                        Toast.makeText(getActivity(), "註冊成功", Toast.LENGTH_SHORT).show();
                        dismiss();
                        Intent intent = new Intent(getActivity(), SettingActivity.class);
                        getActivity().startActivityForResult(intent, MainActivity.ACTIVITY_RETURN);

                        break;
                    case VolleyConstant.SIGNUP_MSG_USER_EXIST:
                        Toast.makeText(getActivity(), "已有這個使用者", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
                Log.d(VolleyConstant.TAG, status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.d(TAG, error.toString());
        }
    };


    @Override
    public int getTheme() {
        //Dialog全螢幕
        return R.style.FullDialog;
    }

    private Boolean checkPattern(String text, String pattern) {   //正規表達式驗證法
        Pattern mPattern = Pattern.compile(pattern);
        Matcher matcher = mPattern.matcher(text);
        Boolean find = matcher.find();

        Log.d(TAG, text + " " + pattern + " " + find);

        return !find;
    }

}
