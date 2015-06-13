package com.Giddh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


import com.Giddh.R;

import java.util.ArrayList;

public class EmailListAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<String> list;

    public EmailListAdapter(ArrayList<String> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(this.ctx).inflate(R.layout.search_row_view, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.name = ((TextView) paramView.findViewById(R.id.search_text));
            paramView.setTag(viewholder);
        }
        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        viewholder.name.setText(list.get(paramInt));
        return paramView;
    }

    static class ViewHolder {
        public TextView name;
    }
}