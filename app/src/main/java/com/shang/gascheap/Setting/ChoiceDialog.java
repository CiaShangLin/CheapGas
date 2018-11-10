package com.shang.gascheap.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.shang.gascheap.CreditCardData.Bank;
import com.shang.gascheap.CreditCardData.CreditCard_id;
import com.shang.gascheap.Main.MainActivity;
import com.shang.gascheap.OpencvCamera.OpencvCameraActivity;
import com.shang.gascheap.R;

import java.util.ArrayList;

public class ChoiceDialog extends DialogFragment {


    public static final String TAG = "ChoiceDialog";
    private static String KEY = "ChoiceDialog";
    private static ChoiceDialog choiceDialog = null;

    public static ChoiceDialog newInstance(ArrayList<String> arrayList) {
        if (choiceDialog == null) {
            choiceDialog = new ChoiceDialog();
        }
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(KEY, arrayList);
        choiceDialog.setArguments(bundle);

        return choiceDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final ArrayList<String> cardList = changeToCardName(getArguments().getStringArrayList(KEY));
        final String[] array = new String[cardList.size()];
        for (int i = 0; i < cardList.size(); i++) {
            array[i] = cardList.get(i);
        }

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity())
                .setIcon(R.drawable.ic_creditcard)
                .setTitle("你的信用卡")
                .setItems(array, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //點了也沒用 只是拿來看的
                        //choiceDialog_listener.change_Textview(array[which]);
                    }
                })
                .setPositiveButton("掃描信用卡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(getActivity(), OpencvCameraActivity.class);
                        startActivity(intent);
                    }
                })
                .setNeutralButton("手選信用卡", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                        ManuallyDialog manuallyDialog = ManuallyDialog.newInstance();
                        manuallyDialog.show(getFragmentManager(), ManuallyDialog.TAG);
                    }
                });


        return alertDialog.create();
    }

    //把使用者以前的信用卡,從ID轉回Name,只能用enum的不能用cardList的,因為還沒讀入
    private ArrayList<String> changeToCardName(ArrayList<String> cardList_id) {

        ArrayList<String> bank = Bank.getBankArrayList();
        ArrayList<ArrayList<String>> bank_card = CreditCard_id.getBank_CardArrayList();
        ArrayList<String> cardList = new ArrayList<>();

        for (int i = 0; i < cardList_id.size(); i++) {
            String str = cardList_id.get(i);
            int bank_id = 0, creaditcard_id = 0;
            if (str.length() == 3) {  //EX: 1 01 目前沒有超過三位數,應該不用擔心
                bank_id = Integer.parseInt(str.substring(0, 1));
                creaditcard_id = Integer.parseInt(str.substring(1, str.length()));
            } else if (str.length() == 4) { //EX: 10 01
                bank_id = Integer.parseInt(str.substring(0, 2));
                creaditcard_id = Integer.parseInt(str.substring(2, str.length()));
            }
            Log.d(TAG, "changeToCardName:" + bank_id + " " + creaditcard_id);

            if (bank_id == 13 || bank_id == 14) {
                cardList.add(bank.get(bank_id - 2) + " " + bank_card.get(bank_id - 2).get(creaditcard_id - 1));  //因為是1~14 沒有12 所以後面要-2
            }else{
                cardList.add(bank.get(bank_id - 1) + " " + bank_card.get(bank_id - 1).get(creaditcard_id - 1));  //-1是因為回傳信id是從1開始
            }

        }
        return cardList;
    }


}
