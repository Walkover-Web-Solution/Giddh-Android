package com.Giddh.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.GroupDetails;
import com.Giddh.dtos.TripInfo;

import java.util.ArrayList;

/**
 * Created by walkover on 27/2/15.
 */

public class TripGridAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<TripInfo> list;
    FlagTypAdapter adapter;
    Activity mActivity;


    public TripGridAdapter(ArrayList<TripInfo> paramArrayList, Context paramContext, Activity activity) {
        this.list = paramArrayList;
        this.ctx = paramContext;
        this.mActivity = activity;
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.grid_cell, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvName = ((TextView) paramView.findViewById(R.id.grid_text));
            viewholder.imageTag = ((ImageView) paramView.findViewById(R.id.grid_image));
            paramView.setTag(viewholder);
        }

        final ViewHolder viewholder = (ViewHolder) paramView.getTag();
        TripInfo dto;
        dto = new TripInfo();
        dto = list.get(paramInt);
        viewholder.tvName.setText(dto.getTripName());
        viewholder.imageTag.setImageBitmap(CommonUtility.drawImageGrid(dto.getTripName(), mActivity));


        return paramView;
    }

    static class ViewHolder {
        public TextView tvName;
        public ImageView imageTag;
    }
}