package com.Giddh.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.dtos.TripInfo;
import com.Giddh.dtos.TripShare;

import java.util.ArrayList;

public class SavedEmailsAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<TripShare> list;

    public SavedEmailsAdapter(ArrayList<TripShare> paramArrayList, Context paramContext) {
        this.list = paramArrayList;
        this.ctx = paramContext;
        Prefs.setSizemail(ctx, list.size());
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
            viewholder.tvTripName = ((TextView) paramView.findViewById(R.id.name));
            viewholder.tvamount = ((TextView) paramView.findViewById(R.id.amount));
            paramView.setTag(viewholder);
        }
        final ViewHolder viewholder = (ViewHolder) paramView.getTag();
        TripShare ldto = list.get(paramInt);
        if (ldto.getCompanyName() != null && !ldto.getCompanyName().equals("null") && !ldto.getCompanyName().equals("")) {
            viewholder.tvTripName.setText(ldto.getCompanyName());
        } else {
            viewholder.tvTripName.setText(ldto.getEmail());
        }
        if (ldto.getAmount() != null && !ldto.getAmount().equals(""))
            viewholder.tvamount.setText(ldto.getAmount() + " " + Prefs.getCurrency(ctx));
        return paramView;
    }

    public void updateReceiptsList(TripShare share1) {
        list.remove(share1);
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        public TextView tvTripName;
        public TextView tvamount;
    }
}