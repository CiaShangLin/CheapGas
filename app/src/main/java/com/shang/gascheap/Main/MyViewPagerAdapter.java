package com.shang.gascheap.Main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


import com.shang.gascheap.Data.GasStation;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Shang on 2018/9/10.
 */

public class MyViewPagerAdapter extends FragmentPagerAdapter {

    protected ArrayList<Fragment> fragmentList;
    private String[] title = {"距離", "價格", "我的最愛"};
    public static final String KEY = "KEY";
    public static final String TYPE="TYPE";


    //理論上會取得三個資料一次匯入 在setting回傳後 可能會改用啟動notify
    //https://www.jianshu.com/p/266861496508
    public MyViewPagerAdapter(FragmentManager fm, HashMap<Integer, ArrayList<GasStation>> map) {
        super(fm);

        fragmentList = new ArrayList<Fragment>();
        for (int i = 1; i <= map.size(); i++) {
            Bundle bundle = new Bundle();
            //bundle.putSerializable(KEY, map.get(i));
            // 這裡有BUG 檔案太大 在跳轉Actitvy的時候 會閃退 因為intent只能512K

            bundle.putInt(TYPE,i);

            GasStationFragment gasStationFragment = new GasStationFragment();
            gasStationFragment.setArguments(bundle);
            fragmentList.add(gasStationFragment);
        }
    }

    public void update(HashMap<Integer, ArrayList<GasStation>> map) {  //更新
        for (int i = 1; i <= map.size(); i++) {
            Bundle bundle = new Bundle();
            bundle.putInt(TYPE,i);
            //bundle.putSerializable(KEY, map.get(i));
            fragmentList.get(i - 1).setArguments(bundle);
        }
        notifyDataSetChanged();
    }


    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }


}
