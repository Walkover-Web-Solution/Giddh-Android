package com.Giddh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.dtos.Accounts;
import com.Giddh.dtos.SummaryAccount;
import com.Giddh.dtos.SummaryGroup;

import java.util.ArrayList;

public class GroupsSummaryAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<SummaryAccount> list = new ArrayList<>();
    ArrayList<SummaryAccount> list2 = new ArrayList<>();
    GroupsSummaryAdapter adapter;

    public GroupsSummaryAdapter(ArrayList<SummaryAccount> sgDto, Context paramContext, Boolean showliab) {
        list2 = sgDto;
        if (showliab) {
            for (int i = 0; i < list2.size(); i++) {
                if (list2.get(i).getGroupName().equals("Liability")) {
                    list.add(list2.get(i));
                }
            }
        }
        if (!showliab) {
            for (int i = 0; i < list2.size(); i++) {
                if (list2.get(i).getGroupName().equals("Assets")) {
                    list.add(list2.get(i));
                }
            }
        }
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.closing_bal, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvGrpNames = ((TextView) paramView.findViewById(R.id.group_name));
            viewholder.tvclosingbalance = ((TextView) paramView.findViewById(R.id.amount));
            paramView.setTag(viewholder);
        }
        final ViewHolder viewholder = (ViewHolder) paramView.getTag();
        SummaryAccount ldto;
        ldto = new SummaryAccount();
        ldto = list.get(paramInt);
        viewholder.tvGrpNames.setText(ldto.getAccountName());
        viewholder.tvclosingbalance.setText(ldto.getClosingBal() + " " + Prefs.getCurrency(ctx));
        return paramView;
    }

    static class ViewHolder {
        public TextView tvGrpNames;
        public TextView tvclosingbalance;
    }
}