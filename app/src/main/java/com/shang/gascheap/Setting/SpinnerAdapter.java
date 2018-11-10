package com.shang.gascheap.Setting;

import android.content.Context;
import android.support.annotation.ArrayRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.shang.gascheap.R;

/**
 * Created by Shang on 2018/8/9.
 */

public class SpinnerAdapter<T> extends ArrayAdapter<T>{


    public SpinnerAdapter(@NonNull Context context, int resource, @NonNull T[] objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return super.getDropDownView(position,convertView,parent);
    }

    public static SpinnerAdapter<CharSequence> createFromResource(@NonNull Context context, @ArrayRes int textArrayResId, @LayoutRes int textViewResId){

        final CharSequence[] sequences=context.getResources().getStringArray(R.array.GasArray);
        return new SpinnerAdapter<CharSequence>(context,textViewResId,sequences);
    }


}
