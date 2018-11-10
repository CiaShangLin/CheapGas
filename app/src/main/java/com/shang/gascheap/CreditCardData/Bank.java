package com.shang.gascheap.CreditCardData;

import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Shang on 2018/6/29.
 */

public enum Bank {
    花旗銀行(1),
    玉山銀行(2),
    聯邦銀行(3),
    凱基銀行(4),
    中國信託(5),
    元大銀行(6),
    滙豐銀行(7),
    國泰世華(8),
    華南銀行(9),
    第一銀行(10),
    台新銀行(11),
    //AAAA(12),
    兆豐銀行(13),
    新光銀行(14);

    private int value;


    private Bank(int value) {
        this.value = value;
    }

    public int getValue() {
        return this.value;
    }



    static public ArrayList<String> getBankArrayList(){
        ArrayList<String> bank=new ArrayList<>();
        for (Bank b : Bank.values()) {     //先加銀行
            bank.add(b.toString());      //0~14
        }
        return bank;
    }

    //用值轉回名稱
    static public String getName(int value){

        for (Bank bank : Bank.values()) {
            if(value==bank.getValue()){
                return bank.name();
            }
        }
        return "";
    }
    /* 用ID轉換成信用卡名稱
    Log.d(TAG, Bank.getName(1) + " " + CreditCard_id.getName("01"));
                Log.d(TAG, Bank.getName(2) + " " + CreditCard_id.getName("01"));
                Log.d(TAG, Bank.getName(2) + " " + CreditCard_id.getName("02"));*/

}
