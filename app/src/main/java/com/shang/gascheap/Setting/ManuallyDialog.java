package com.shang.gascheap.Setting;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.shang.gascheap.CreditCardData.Bank;
import com.shang.gascheap.CreditCardData.CreditCard_id;
import com.shang.gascheap.Data.Setting;
import com.shang.gascheap.R;
import com.shang.gascheap.SharedPreConstant;
import com.shang.gascheap.SharedPreOperating;
import com.shang.gascheap.VolleyUnits.VolleyConstant;
import com.shang.gascheap.VolleyUnits.VolleyController;
import com.shang.gascheap.VolleyUnits.VolleyListenerInterface;
import com.shang.gascheap.VolleyUnits.VolleyRequestUnit;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Shang on 2018/5/21.
 */

public class ManuallyDialog extends DialogFragment {

    public static final String TAG = "ManuallyDialog";
    private static ManuallyDialog manuallyDialog;


    //UI有問題要改
    public static ManuallyDialog newInstance() {
        if (manuallyDialog == null) {
            manuallyDialog = new ManuallyDialog();
        }
        return manuallyDialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final ArrayList<String> bank = Bank.getBankArrayList();
        final ArrayList<ArrayList<String>> bank_card = CreditCard_id.getBank_CardArrayList();

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.manually_layout, null);
        ExpandableListView expandableListView = (ExpandableListView) view.findViewById(R.id.expand_ListView);
        expandableListView.setAdapter(new MyAdapter(bank, bank_card));
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, final int groupPosition, final int childPosition, long id) {

                Map<String, String> map = new HashMap<String, String>();
                String bank_id = String.valueOf(Bank.valueOf(bank.get(groupPosition)).getValue());
                String creditcard_id = String.valueOf(CreditCard_id.valueOf(bank_card.get(groupPosition).get(childPosition)).getValue());

                map.put(VolleyConstant.CREDIT_NAME, Setting.getInstance(getActivity()).getAccount());
                map.put(VolleyConstant.CREDIT_ID, bank_id + creditcard_id);
                Log.d(TAG, bank.get(groupPosition) + " " + bank_card.get(groupPosition).get(childPosition));
                Log.d(TAG, bank_id + " " + creditcard_id);

                VolleyRequestUnit.RequestPost(getActivity(), VolleyConstant.CREDIT_URL, TAG, map, mVolleyListenerInterface);

                return true;
            }
        });

        //最後創建Dialog
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setTitle("手動選擇信用卡")
                .setIcon(R.drawable.ic_creditcard)
                .setNegativeButton("關閉", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setView(view)  //上面的VIEW
                .create();
        return alertDialog;

    }

    private VolleyListenerInterface mVolleyListenerInterface = new VolleyListenerInterface(getActivity()
            , VolleyListenerInterface.mLinstener, VolleyListenerInterface.mErrorLinstener) {
        @Override
        public void onSucces(String response) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                String status = jsonObject.getString(VolleyConstant.CREDIT_MSG);
                switch (status) {
                    case VolleyConstant.CREDIT_MSG_ADD:
                        Toast.makeText(getActivity(), "信用卡新增完成", Toast.LENGTH_SHORT).show();
                        dismiss();
                        break;
                    case VolleyConstant.CREDIT_MSG_NO_USER:
                        Toast.makeText(getActivity(), "沒有這個使用者", Toast.LENGTH_SHORT).show();
                        break;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onError(VolleyError error) {
            Log.d(TAG, "已經有這張卡片了:" + error.toString());
            Toast.makeText(getActivity(), "已經有這張卡片了", Toast.LENGTH_SHORT).show();
            dismiss();
        }
    };


}

class MyAdapter extends BaseExpandableListAdapter {

    private ArrayList<String> bank;
    private ArrayList<ArrayList<String>> bank_card;

    public MyAdapter(ArrayList<String> bank, ArrayList<ArrayList<String>> bank_card) {
        this.bank = bank;
        this.bank_card = bank_card;
    }

    @Override
    public int getGroupCount() {
        return bank.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return bank_card.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return bank.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return bank_card.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        GroupViewHolder groupViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_group_normal, parent, false);
            groupViewHolder = new GroupViewHolder();
            groupViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_group_normal);
            convertView.setTag(groupViewHolder);
        } else {
            groupViewHolder = (GroupViewHolder) convertView.getTag();
        }
        groupViewHolder.tvTitle.setText(bank.get(groupPosition));
        return convertView;

    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        ChildViewHolder childViewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_expand_child, parent, false);
            childViewHolder = new ChildViewHolder();
            childViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.label_expand_child);
            convertView.setTag(childViewHolder);
        } else {
            childViewHolder = (ChildViewHolder) convertView.getTag();
        }
        childViewHolder.tvTitle.setText(bank_card.get(groupPosition).get(childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class GroupViewHolder {
        TextView tvTitle;
    }

    static class ChildViewHolder {
        TextView tvTitle;
    }

}
