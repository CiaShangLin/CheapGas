package com.shang.gascheap.Main;

import android.app.DialogFragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.shang.gascheap.Data.GasPrice;
import com.shang.gascheap.R;

import java.util.HashMap;

/**
 * Created by Shang on 2018/6/27.
 */

public class GasPriceDialog extends DialogFragment{

    public static final String KEY="GasPriceDialog";
    protected static final String CPC="CPC";
    protected static final String FPG="FPG";
    protected static final String PREDICTION="PREDICTION";
    private static GasPriceDialog gasPriceDialog = null;

    public static GasPriceDialog newInstance(HashMap<String,GasPrice> map,String prediction) {
        if (gasPriceDialog == null) {
            gasPriceDialog = new GasPriceDialog();
        }
        Bundle bundle=new Bundle();
        bundle.putSerializable(KEY,map);
        bundle.putString(PREDICTION,prediction);
        gasPriceDialog.setArguments(bundle);
        return gasPriceDialog;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        String prediction=getArguments().getString(PREDICTION);
        HashMap<String,GasPrice> map=(HashMap<String, GasPrice>) getArguments().getSerializable(KEY);

        View view=LayoutInflater.from(getActivity()).inflate(R.layout.dialog_gasprice,container,false);
        TextView cpcTv92,cpcTv95,cpcTv98,cpcTvDe;
        TextView fpgTv92,fpgTv95,fpgTv98,fpgTvDe;
        cpcTv92=(TextView)view.findViewById(R.id.cpcTv92);
        cpcTv95=(TextView)view.findViewById(R.id.cpcTv95);
        cpcTv98=(TextView)view.findViewById(R.id.cpcTv98);
        cpcTvDe=(TextView)view.findViewById(R.id.cpcTvDe);

        fpgTv92=(TextView)view.findViewById(R.id.fpgTv92);
        fpgTv95=(TextView)view.findViewById(R.id.fpgTv95);
        fpgTv98=(TextView)view.findViewById(R.id.fpgTv98);
        fpgTvDe=(TextView)view.findViewById(R.id.fpgTvDe);

        cpcTv92.setText(String.valueOf(map.get(CPC).getGas92()));
        cpcTv95.setText(String.valueOf(map.get(CPC).getGas95()));
        cpcTv98.setText(String.valueOf(map.get(CPC).getGas98()));
        cpcTvDe.setText(String.valueOf(map.get(CPC).getDiesel()));

        fpgTv92.setText(String.valueOf(map.get(FPG).getGas92()));
        fpgTv95.setText(String.valueOf(map.get(FPG).getGas95()));
        fpgTv98.setText(String.valueOf(map.get(FPG).getGas98()));
        fpgTvDe.setText(String.valueOf(map.get(FPG).getDiesel()));

        Button gaspriceBt=(Button)view.findViewById(R.id.gaspriceBt);
        gaspriceBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView gasPriceTv=(TextView)view.findViewById(R.id.gasPriceTv);
        gasPriceTv.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"fonts/steel.ttf")); //字型放好玩的

        TextView predictionTv=(TextView)view.findViewById(R.id.predictionTv);
        predictionTv.setText(prediction);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
