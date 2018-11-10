package com.shang.gascheap.OpencvCamera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by SERS on 2018/2/14.
 */
public class MyView extends View {

    //获取屏幕的宽和高。根据屏幕的宽和高来算取景框的位置
    private int screenWidth, screenHeight, myViewPaddingLeft, myViewPaddingTop;


    public MyView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setMyParams(int screenWidth, int screenHeight, int myViewPaddingLeft, int myViewPaddingTop) {
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.myViewPaddingLeft = myViewPaddingLeft;
        this.myViewPaddingTop = myViewPaddingTop;
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAlpha(250);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        canvas.drawPaint(paint);
    }

    public double getMyViewWidth(){
        ViewGroup.LayoutParams myViewParams=this.getLayoutParams();
        return myViewParams.width;
    }

    public double getMyViewHeight(){
        ViewGroup.LayoutParams myViewParams=this.getLayoutParams();
        return myViewParams.height;
    }
}
