package com.shang.gascheap;

import android.app.DialogFragment;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Created by Shang on 2018/8/8.
 */

public class DownloadDialog extends DialogFragment {

    private static DownloadDialog downloadDialog=null;
    private ProgressBar progressBar;

     public static DownloadDialog getInstance(){
        if(downloadDialog==null){
            downloadDialog=new DownloadDialog();
        }
        return downloadDialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_download, null);

        ImageView loadingView = (ImageView) view.findViewById(R.id.downloadView);
        AnimationDrawable animationDrawable = (AnimationDrawable) loadingView.getDrawable();
        animationDrawable.start();

        progressBar=(ProgressBar)view.findViewById(R.id.progressBar);
        progressBar.setMax(23);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setBackgroundDrawableResource(R.color.transparent);  //背景透明
    }

    protected void setProgressBar(int progress){
         progressBar.setProgress(progress);
    }
}
