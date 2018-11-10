package com.shang.gascheap.Main;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.shang.gascheap.CreditCardData.Bank;
import com.shang.gascheap.CreditCardData.CreditCardName;
import com.shang.gascheap.CreditCardData.CreditCard_id;
import com.shang.gascheap.Data.GasStation;
import com.shang.gascheap.Data.Setting;
import com.shang.gascheap.GoogleMap.MyLocationGps;
import com.shang.gascheap.R;

import java.util.ArrayList;

/**
 * Created by Shang on 2018/6/3.
 */
//RecycleviewAdapter和DistanceFragment和FavoriteFragment一組
public class RecycleviewAdapter extends RecyclerView.Adapter<RecycleviewAdapter.ViewHolder> implements View.OnClickListener {

    private Context mContext;
    private OnItemClickListener mOnItemClickListener = null;
    private int type;
    private ArrayList<GasStation> gasStations;

    public RecycleviewAdapter(Context context, ArrayList<GasStation> gasStations,int type) {
        mContext = context;
        this.gasStations = gasStations;
        this.type=type;
    }

    public static interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_fragment, parent, false);
        view.setOnClickListener(this);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemView.setTag(position);

        GasStation gasStation = gasStations.get(position);
        if (gasStation != null) {
            if (gasStation.getCompany() == 1) {   //中油
                holder.companyIg.setImageResource(R.drawable.ic_cpc);
                holder.companyTv.setText("中油");
            } else if (gasStation.getCompany() == 2) { //台塑
                holder.companyIg.setImageResource(R.drawable.ic_fpg);
                holder.companyTv.setText("台塑");
            }

            holder.gasstationTv.setText(gasStation.getName());

            String name = CreditCardName.getName(gasStation.getCredit_card_id());
            holder.creditcardTv.setText(name);

            holder.distanceTv.setText(String.format("%.2fkm", gasStation.getDistance()));

            String much=Setting.getInstance(mContext).getMuch();
            int liter = Integer.parseInt(String.valueOf(much==""?0:much));
            String price = String.format("省%.0f元",liter * gasStation.getToday_Price());
            holder.normalTv.setText(price);

            if (gasStation.getDIY_price() == 100) {  //自助沒有優惠
                holder.selfTv.setText("無優惠");
            } else {
                holder.selfTv.setText(String.format("省%.0f元", liter * gasStation.getDIY_price()));
            }

            if (gasStation.getDIY() == 0) {
                holder.selfTv.setText("無提供");
            }

            switch (type){
                case 1:
                    holder.distanceTv.setTextColor(Color.RED);
                    break;
                case 2:
                    holder.normalTv.setTextColor(Color.RED);
                    holder.selfTv.setTextColor(Color.RED);
                    break;
                case 3:
                    holder.gasstationTv.setTextColor(Color.RED);
                    break;
            }
        }


        //holder.itemView.setOnClickListener()  //這個也是可以觸發點擊 只是要跟MAIN溝通的話比較麻煩
    }

    @Override
    public int getItemCount() {
        return gasStations.size();
    }


    @Override
    public void onClick(View v) {
        mOnItemClickListener.onItemClick(v, (int) v.getTag());
    }

    public void setmOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView companyTv;
        TextView creditcardTv;
        TextView gasstationTv;
        TextView normalTv;
        TextView distanceTv;
        TextView selfTv;
        ImageView companyIg;


        public ViewHolder(View itemView) {
            super(itemView);

            gasstationTv = itemView.findViewById(R.id.gasstationTv);
            companyTv = itemView.findViewById(R.id.companyTv);
            creditcardTv = itemView.findViewById(R.id.creditcardTv);
            normalTv = itemView.findViewById(R.id.normalTv);
            distanceTv = itemView.findViewById(R.id.distanceTv);
            selfTv = itemView.findViewById(R.id.selfTv);
            companyIg = itemView.findViewById(R.id.companyIg);

        }
    }
}
