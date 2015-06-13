package com.Giddh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.dtos.AccountDetails;

import java.util.ArrayList;

/**
 * Created by walkover on 27/2/15.
 */

public class AccountDetailsAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<AccountDetails> list;

    public AccountDetailsAdapter(ArrayList<AccountDetails> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.account_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvCompanyNames = ((TextView) paramView.findViewById(R.id.group_name));
            viewholder.tvclosingbalance = ((TextView) paramView.findViewById(R.id.closing_balance));
            paramView.setTag(viewholder);
        }

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        AccountDetails adto;
        adto = list.get(paramInt);

        viewholder.tvCompanyNames.setText(paramInt+1+". "+adto.getAccountName());
        viewholder.tvclosingbalance.setText(adto.getBalance()+" "+ Prefs.getCurrency(ctx));

        return paramView;
    }

    static class ViewHolder {
        public TextView tvCompanyNames;
        public TextView tvclosingbalance;
    }
}