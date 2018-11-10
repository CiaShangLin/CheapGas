package com.shang.gascheap.Data;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class GasStation implements Serializable {
    private static final long serialVersionUID = 20180917;

    int company;
    String credit_card_id;
    double MON;
    double TUE;
    double WED;
    double TUR;
    double FRI;
    double SAT;
    double SUN;
    String description;
    double DIY_price;
    int idgas_station;
    String name;
    String local_address;
    int DIY;
    double longitude;  //long
    double latitude;  //lat
    double distance;

    public GasStation(int company, String credit_card_id, double MON, double TUE, double WED, double TUR, double FRI, double SAT, double SUN,
                      String description, double DIY_price, int idgas_station, String name, String local_address,
                      int DIY, double longitude, double latitude, double distance) {
        this.company = company;
        this.credit_card_id = credit_card_id;
        this.MON = MON;
        this.TUE = TUE;
        this.WED = WED;
        this.TUR = TUR;
        this.FRI = FRI;
        this.SAT = SAT;
        this.SUN = SUN;
        this.description = description;
        this.DIY_price = DIY_price;
        this.idgas_station = idgas_station;
        this.name = name;
        this.local_address = local_address;
        this.DIY = DIY;
        this.longitude = longitude;
        this.latitude = latitude;
        this.distance = distance;
    }

    public int getCompany() {
        return company;
    }

    public void setCompany(int company) {
        this.company = company;
    }

    public String getCredit_card_id() {
        return credit_card_id;
    }

    public void setCredit_card_id(String credit_card_id) {
        this.credit_card_id = credit_card_id;
    }

    public double getMON() {
        return MON;
    }

    public void setMON(double MON) {
        this.MON = MON;
    }

    public double getTUE() {
        return TUE;
    }

    public void setTUE(double TUE) {
        this.TUE = TUE;
    }

    public double getWED() {
        return WED;
    }

    public void setWED(double WED) {
        this.WED = WED;
    }

    public double getTUR() {
        return TUR;
    }

    public void setTUR(double TUR) {
        this.TUR = TUR;
    }

    public double getFRI() {
        return FRI;
    }

    public void setFRI(double FRI) {
        this.FRI = FRI;
    }

    public double getSAT() {
        return SAT;
    }

    public void setSAT(double SAT) {
        this.SAT = SAT;
    }

    public double getSUN() {
        return SUN;
    }

    public void setSUN(double SUN) {
        this.SUN = SUN;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getDIY_price() {
        return DIY_price;
    }

    public void setDIY_price(double DIY_price) {
        this.DIY_price = DIY_price;
    }

    public int getIdgas_station() {
        return idgas_station;
    }

    public void setIdgas_station(int idgas_station) {
        this.idgas_station = idgas_station;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocal_address() {
        return local_address;
    }

    public void setLocal_address(String local_address) {
        this.local_address = local_address;
    }

    public int getDIY() {
        return DIY;
    }

    public void setDIY(int DIY) {
        this.DIY = DIY;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getToday_Price(){

        //0 = Sunday, 1 = Monday, 2 = Tuesday, 3 = Wednesday, 4 = Thursday, 5 = Friday, 6 = Saturday
        Date date=new Date();
        double[] day=new double[]{SUN,MON,TUE,WED,TUR,FRI,SAT}; // 7 1~6
        int today=date.getDay();
        return day[today];

    }
}
