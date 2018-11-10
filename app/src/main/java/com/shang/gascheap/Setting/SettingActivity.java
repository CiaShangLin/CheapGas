package com.shang.gascheap.Setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.android.volley.VolleyError;
import com.shang.gascheap.Data.Setting;
import com.shang.gascheap.Main.MainActivity;
import com.shang.gascheap.R;
import com.shang.gascheap.SharedPreConstant;
import com.shang.gascheap.SharedPreOperating;
import com.shang.gascheap.VolleyUnits.VolleyConstant;
import com.shang.gascheap.VolleyUnits.VolleyListenerInterface;
import com.shang.gascheap.VolleyUnits.VolleyRequestUnit;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Set;

public class SettingActivity extends AppCompatActivity {

    public static final String TAG = "SettingActivity";

    private Toolbar toolbar;
    private CheckBox checkBox_Self, checkBox_Normal;
    private FloatingActionButton floatingActionButton;
    private TextView tvChoiceCard;
    private EditText distanceEt, muchEt;
    private Spinner gasSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult();
                finish();
            }
        });


        final String[] array = getResources().getStringArray(R.array.GasArray);
        gasSpinner = (Spinner) findViewById(R.id.gasSpinner);
        ArrayAdapter<CharSequence> arrayAdapter = SpinnerAdapter.createFromResource(this, R.array.GasArray, R.layout.setting_spinner);
        gasSpinner.setAdapter(arrayAdapter);
        gasSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Setting.getInstance(SettingActivity.this).setGas(array[position]);
                //Log.d("TAG",parent.getSelectedItem()+""); //取得字串的方法
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //設定spinner的初始畫面
        String ch = Setting.getInstance(this).getGas();
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(ch)) {
                gasSpinner.setSelection(i);
            }
        }

        checkBox_Self = (CheckBox) findViewById(R.id.checkBox_Self);
        checkBox_Normal = (CheckBox) findViewById(R.id.checkBox_Normal);
        checkBox_Self.setOnCheckedChangeListener(mOnCheckedBoxListener);
        checkBox_Normal.setOnCheckedChangeListener(mOnCheckedBoxListener);
        checkBox_Self.setChecked(Setting.getInstance(this).getMode_self());
        checkBox_Normal.setChecked(Setting.getInstance(this).getMode_normal());


        tvChoiceCard = (TextView) findViewById(R.id.tvChoiceCard);
        tvChoiceCard.setText("<-信用卡列表");

        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floatingActionButton.setEnabled(false);
                String URL = VolleyConstant.CREDIT_URL + "/" + Setting.getInstance(SettingActivity.this).getAccount();
                VolleyRequestUnit.RequestGet(SettingActivity.this, URL, TAG, mVolleyListenerInterface);
            }
        });

        distanceEt = (EditText) findViewById(R.id.distanceEt);
        distanceEt.setText(Setting.getInstance(this).getDistance());

        muchEt = (EditText) findViewById(R.id.muchEt);
        muchEt.setText(Setting.getInstance(this).getMuch());
    }

    private VolleyListenerInterface mVolleyListenerInterface = new VolleyListenerInterface(this
            , VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
        @Override
        public void onSucces(String response) {

            ArrayList<String> arrayList = new ArrayList();
            try {
                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    arrayList.add(jsonArray.getJSONObject(i).getJSONArray("user").getString(1));
                }
                ChoiceDialog choiceDialog = ChoiceDialog.newInstance(arrayList);
                choiceDialog.show(getFragmentManager(), ChoiceDialog.TAG);
                floatingActionButton.setEnabled(true);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.d(TAG, error.toString());
            ChoiceDialog choiceDialog = ChoiceDialog.newInstance(new ArrayList<String>());
            choiceDialog.show(getFragmentManager(), ChoiceDialog.TAG);
            floatingActionButton.setEnabled(true);
        }
    };

    private CompoundButton.OnCheckedChangeListener mOnCheckedBoxListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            switch (buttonView.getId()) {
                case R.id.checkBox_Self:
                    Setting.getInstance(SettingActivity.this).setMode_self(isChecked);
                    break;
                case R.id.checkBox_Normal:
                    Setting.getInstance(SettingActivity.this).setMode_normal(isChecked);
                    break;
            }
        }
    };

    private void setResult(){
        Intent intent=new Intent(this,MainActivity.class);
        intent.putExtra(MainActivity.RETURN,true);
        setResult(MainActivity.ACTIVITY_RETURN,intent);
        //startActivity(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //不能寫在onStop上 因為生命流程的原因
        Log.d(TAG,"onPause");
        Setting.getInstance(SettingActivity.this).setDistance(distanceEt.getText().toString().trim());
        Setting.getInstance(SettingActivity.this).setMuch(muchEt.getText().toString().trim());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            setResult();
        }
        return super.onKeyDown(keyCode, event);
    }
}
