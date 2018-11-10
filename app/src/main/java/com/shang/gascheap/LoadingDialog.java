package com.shang.gascheap;

import android.app.DialogFragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Shang on 2018/8/5.
 */

//目前CreditCard_Data_LoadTask和google map使用

public class LoadingDialog extends DialogFragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_loading, null);

        ImageView loadingView = (ImageView) view.findViewById(R.id.loadingView);
        AnimationDrawable animationDrawable = (AnimationDrawable) loadingView.getDrawable();
        animationDrawable.start();

        TextView loadingTv=(TextView)view.findViewById(R.id.loadingTv);



        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);  //背景透明

        /*int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
        int height = getResources().getDimensionPixelSize(R.dimen.popup_height);
        getDialog().getWindow().setLayout(width, height);*/

    }
}
