package com.shang.gascheap.GoogleMap;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.shang.gascheap.Data.GasStation;
import com.shang.gascheap.R;

import java.util.ArrayList;

/**
 * Created by Shang on 2018/7/30.
 */

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context mContext;
    private Location mLocation;
    private ArrayList<GasStation> mGasStationList;


    public MyInfoWindowAdapter(Context context, Location location, ArrayList<GasStation> gasStationList) {
        this.mContext = context;
        this.mLocation = location;
        this.mGasStationList = gasStationList;

    }

    @Override
    public View getInfoWindow(Marker marker) {
        // getInfoContents回傳null才會執行這個
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        Log.d("TAG", marker.getTag() + "");

        View view = LayoutInflater.from(mContext).inflate(R.layout.infowindow_layout, null);

        TextView infoGasStation = (TextView) view.findViewById(R.id.infoGasStation);
        TextView infoSelf = (TextView) view.findViewById(R.id.infoSelf);
        TextView infoDistance = (TextView) view.findViewById(R.id.infoDistance);
        TextView infoAddress = (TextView) view.findViewById(R.id.infoAddress);

        GasStation gasStation = mGasStationList.get((int) marker.getTag());
        infoGasStation.append(gasStation.getName());            //加油站
        infoSelf.append(gasStation.getDIY() == 1 ? "YES" : "NO");     //自助
        infoDistance.append(                                    //距離
                MyLocationGps.getDistance(mLocation.getLatitude(), mLocation.getLongitude()
                        , marker.getPosition().latitude, marker.getPosition().longitude));
        infoAddress.append(gasStation.getLocal_address());            //地址

        return view;
    }


}
