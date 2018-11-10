package com.shang.gascheap.Data;

import android.app.Activity;
import android.content.Context;

import com.shang.gascheap.CreditCardData.CreditCard;
import com.shang.gascheap.Main.MainActivity;
import com.shang.gascheap.SharedPreConstant;
import com.shang.gascheap.SharedPreOperating;

import java.util.ArrayList;
import java.util.Set;

/**
 * Created by Shang on 2018/4/13.
 */

public class Setting {

    private static Setting setting = null;
    private String account;           //帳號
    private String password;         //密碼
    private String phone;
    private String email;
    private String gas;               //加油種類
    private Boolean mode_self;         //加油方法
    private Boolean mode_normal;      //加油方法
    private String distance;           //距離加油站距離
    private String much;             //預設加多少公升
    private static SharedPreOperating sp;
    private static Context mContext;


    public static Setting getInstance(Context context) {
        if (setting == null) {
            setting = new Setting();
            sp = SharedPreOperating.newInstance(context);
            mContext = context;
        }
        return setting;
    }

    public Setting() {
    }

    public void setAccount(String account) {
        sp.putString(SharedPreConstant.Save_Account, account);
    }

    public void setPassword(String password) {
        sp.putString(SharedPreConstant.Save_Password, password);
    }

    public void setPhone(String phone) {
        sp.putString(SharedPreConstant.Save_Phone, phone);
    }

    public void setEmail(String email) {
        sp.putString(SharedPreConstant.Save_Email, email);
    }

    public void setGas(String gas) {
        sp.putString(SharedPreConstant.Choice_Gas, gas);
    }

    public void setMode_self(Boolean mode_self) {
        sp.putBoolean(SharedPreConstant.Save_Self, mode_self);
    }

    public void setMode_normal(Boolean mode_normal) {
        sp.putBoolean(SharedPreConstant.Save_Normal, mode_normal);
    }

    public void setDistance(String distance) {
        sp.putString(SharedPreConstant.Save_Distance, distance);
    }

    public void setMuch(String much) {
        sp.putString(SharedPreConstant.Save_Much, much);
    }

    public String getAccount() {
        return sp.getString(SharedPreConstant.Save_Account, "");
    }

    public String getPassword() {
        return sp.getString(SharedPreConstant.Save_Password, "");
    }

    public String getPhone() {
        return sp.getString(SharedPreConstant.Save_Phone, "");
    }

    public String getEmail() {
        return sp.getString(SharedPreConstant.Save_Email, "");
    }

    public String getGas() {
        return sp.getString(SharedPreConstant.Choice_Gas, "");
    }

    public Boolean getMode_self() {
        return sp.getBoolean(SharedPreConstant.Save_Self, true);
    }

    public Boolean getMode_normal() {
        return sp.getBoolean(SharedPreConstant.Save_Normal, true);
    }

    public String getDistance() {
        return sp.getString(SharedPreConstant.Save_Distance, "0");
    }

    public String getMuch() {
        return sp.getString(SharedPreConstant.Save_Much, "0");
    }

    @Override
    public String toString() {
        return account + " " + gas + " " + mode_self + " " + mode_normal + " " + distance + " " + much;
    }

}
