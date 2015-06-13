package com.Giddh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.dtos.Accounts;

import java.util.ArrayList;

/**
 * Created by walkover on 14/4/15.
 */

public class AddBankAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Accounts> list;

    public AddBankAdapter(ArrayList<Accounts> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.row_add_bank, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvCompanyNames = ((TextView) paramView.findViewById(R.id.name));
            paramView.setTag(viewholder);
        }

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        Accounts adto;
        adto = list.get(paramInt);

        viewholder.tvCompanyNames.setText(adto.getAccountName());


        return paramView;
    }

    static class ViewHolder {
        public TextView tvCompanyNames;

    }
}