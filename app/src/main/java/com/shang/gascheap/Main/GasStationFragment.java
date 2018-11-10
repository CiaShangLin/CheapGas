package com.shang.gascheap.Main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shang.gascheap.Data.GasStation;
import com.shang.gascheap.GoogleMap.MapsActivity;
import com.shang.gascheap.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

/**
 * Created by Shang on 2018/9/22.
 */

public class GasStationFragment extends Fragment {

    public static final String POSITION = "POSITION";
    private RecyclerView recyclerView;
    private RecycleviewAdapter adapter;
    private ArrayList<GasStation> dataList;
    private TextView pagerTv;
    private int type = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //原先想用event bus來處理 可是在new fragment的時候他會先create view 我猜可能先new view出來在做post填入資料後 再用event bus
        //然後有可能翻轉時他會去判斷Bundle有沒有資料 這裡在做一個判斷來考慮是否加載資料

        type = (int) getArguments().get(MyViewPagerAdapter.TYPE);
        //dataList=(ArrayList<GasStation>)getArguments().getSerializable(MyViewPagerAdapter.KEY);
        dataList = MainActivity.gasStationMap.get(type);

        View view = inflater.inflate(R.layout.fragment_view_pager, container, false);
        recyclerView = view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pagerTv = view.findViewById(R.id.pagerTv);
        if (dataList.size() != 0) {
            pagerTv.setVisibility(View.INVISIBLE);
        }

        adapter = new RecycleviewAdapter(getContext(), dataList, type);
        recyclerView.setAdapter(adapter);
        adapter.setmOnItemClickListener(new RecycleviewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Intent intent = new Intent(getActivity().getBaseContext(), MapsActivity.class);
                Bundle bundle = new Bundle();
                ArrayList<GasStation> temp=new ArrayList<>();
                temp.add(dataList.get(position));
                bundle.putSerializable(MyViewPagerAdapter.KEY, temp);
                bundle.putInt(POSITION, position);  //取得點擊位置
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });

        return view;
    }

}
