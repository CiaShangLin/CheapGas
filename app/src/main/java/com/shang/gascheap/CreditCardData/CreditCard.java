package com.shang.gascheap.CreditCardData;

import android.content.Context;

import com.shang.gascheap.Main.MainActivity;

import org.opencv.core.Mat;


public class CreditCard {

    private String id;              //卡號ID
    private String bank;            //銀行ID
    private String name;           //卡號名子

    //opencv輸出用
    private double ratio;
    private int rows;
    private int cols;
    private int type;
    private int elemSize;
    private float[] data;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public float[] getData() {
        return data;
    }

    public void setData(float[] data) {
        this.data = data;
    }

    public int getElemSize() {
        return elemSize;
    }

    public void setElemSize(int elemSize) {
        this.elemSize = elemSize;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getCols() {
        return cols;
    }

    public void setCols(int cols) {
        this.cols = cols;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRatio() {
        return ratio;
    }

    public void setRatio(double ratio) {
        this.ratio = ratio;
    }

    public Mat getMat(){
        Mat mat=new Mat(rows,cols,type);
        mat.put(0,0,data);
        return mat;
    }

}
