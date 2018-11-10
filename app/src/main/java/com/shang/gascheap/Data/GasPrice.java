package com.shang.gascheap.Data;

import java.io.Serializable;

/**
 * Created by Shang on 2018/6/27.
 */

//油價表
public class GasPrice implements Serializable{
    private static final long serialVersionUID = 2018627;
    int id;
    String name;
    double gas92;
    double gas95;
    double gas98;
    double Diesel;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getGas92() {
        return gas92;
    }

    public void setGas92(double gas92) {
        this.gas92 = gas92;
    }

    public double getGas95() {
        return gas95;
    }

    public void setGas95(double gas95) {
        this.gas95 = gas95;
    }

    public double getGas98() {
        return gas98;
    }

    public void setGas98(double gas98) {
        this.gas98 = gas98;
    }

    public double getDiesel() {
        return Diesel;
    }

    public void setDiesel(double diesel) {
        Diesel = diesel;
    }
}
