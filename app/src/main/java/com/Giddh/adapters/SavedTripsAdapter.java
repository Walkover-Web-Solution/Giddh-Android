package com.Giddh.adapters;

import android.content.Context;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.FontTextView;
import com.Giddh.dtos.TripInfo;

import java.util.ArrayList;

public class SavedTripsAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<TripInfo> list;


    public SavedTripsAdapter(ArrayList<TripInfo> paramArrayList, Context paramContext) {
        this.list = paramArrayList;
        this.ctx = paramContext;
    }

    public int getCount() {

        return this.list.size();
    }

    public Object getItem(int paramInt) {
        return this.list.get(paramInt);
    }

    public long getItemId(int paramInt) {
        return paramInt;
    }

    public View getView(int paramInt, View paramView, ViewGroup paramViewGroup) {

        if (paramView == null) {
            paramView = LayoutInflater.from(ctx).inflate(R.layout.saved_trips_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvTripName = ((FontTextView) paramView.findViewById(R.id.name));
            paramView.setTag(viewholder);
        }

        final ViewHolder viewholder = (ViewHolder) paramView.getTag();
        TripInfo ldto;
        ldto = new TripInfo();
        ldto = list.get(paramInt);
        viewholder.tvTripName.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
        viewholder.tvTripName.setText(ldto.getTripName());

        return paramView;
    }


    static class ViewHolder {
        public FontTextView tvTripName;
    }
}