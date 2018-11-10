package com.shang.gascheap.CreditCardData;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.toolbox.StringRequest;
import com.shang.gascheap.Main.MainActivity;


public class CreditCardConstant{


    //public static final String SD_CARD_PATH= Environment.getExternalStorageDirectory().toString();
    //Environment.getExternalStorageDirectory().toString(); 這個是外部儲存 APP刪掉他不會被刪掉  要改成另外一個

    public static final int CreditCard_Amount =23;
    public static final String CreditCardDir = "CreditCardDir";
    public static final String CreditCard = "creditcard";

    //public static final String CC = SD_CARD_PATH+"/"+CreditCardDir+"/"+CreditCard;


    //這個是內部路徑　ＡＰＰ刪除資料也會一起刪除
    public static String getInnerPath(Context context){
        return context.getExternalFilesDir(null).getPath();
    }

    public static String getCC(Context context){
        String CC=getInnerPath(context)+"/"+CreditCardDir+"/"+CreditCard;
        return CC;
    }


}
