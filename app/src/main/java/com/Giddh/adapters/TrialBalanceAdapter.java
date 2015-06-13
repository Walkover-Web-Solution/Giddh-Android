package com.Giddh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.dtos.GroupDetails;

import java.util.ArrayList;

/**
 * Created by walkover on 27/2/15.
 */
public class TrialBalanceAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<GroupDetails> list;
    Boolean isSubgrp;
    TrialBalanceAdapter adapter;

    public TrialBalanceAdapter(ArrayList<GroupDetails> paramArrayList, Context paramContext, Boolean subgrp) {
        this.list = paramArrayList;
        this.ctx = paramContext;
        this.isSubgrp = subgrp;
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.trial_balance_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvCompanyNames = ((TextView) paramView.findViewById(R.id.group_name));
            viewholder.tvclosingbalance = ((TextView) paramView.findViewById(R.id.closing_balance));
            paramView.setTag(viewholder);
        }
        final ViewHolder viewholder = (ViewHolder) paramView.getTag();
        GroupDetails ldto;
        ldto = new GroupDetails();
        ldto = list.get(paramInt);
        if (!ldto.getGroupName().equals("") || !(ldto.getGroupName() == null)) {
            if (isSubgrp)
                viewholder.tvCompanyNames.setText(paramInt + 1 + ". " + ldto.getGroupName());
            else
                viewholder.tvCompanyNames.setText(ldto.getGroupName());
            viewholder.tvclosingbalance.setText(ldto.getClosingBalance() + " " + Prefs.getCurrency(ctx));
        } else {
            viewholder.tvCompanyNames.setText("No Data Available");
            viewholder.tvclosingbalance.setText("");
        }
        return paramView;
    }

    static class ViewHolder {
        public TextView tvCompanyNames;
        public TextView tvclosingbalance;
    }
}