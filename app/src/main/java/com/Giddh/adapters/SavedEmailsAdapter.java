package com.Giddh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.FontTextView;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.dtos.TripInfo;
import com.Giddh.dtos.TripShare;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SavedEmailsAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<TripShare> list;
    DecimalFormat decimalFormat;

    public SavedEmailsAdapter(ArrayList<TripShare> paramArrayList, Context paramContext) {
        this.list = paramArrayList;
        this.ctx = paramContext;
        Prefs.setSizemail(ctx, list.size());
        decimalFormat = new DecimalFormat("#.00");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);
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
            viewholder.tvamount = ((FontTextView) paramView.findViewById(R.id.amount));
            viewholder.tvTripName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            viewholder.tvamount.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            paramView.setTag(viewholder);
        }
        final ViewHolder viewholder = (ViewHolder) paramView.getTag();
        TripShare ldto = list.get(paramInt);
        if (ldto.getOwner().equalsIgnoreCase("1")) {
            viewholder.tvTripName.setTextColor(Color.parseColor("#CD7051"));
            Log.e("Owner is = ", ldto.getEmail());
        }
        if (ldto.getCompanyName() != null && !ldto.getCompanyName().equals("null") && !ldto.getCompanyName().equals("")) {
            viewholder.tvTripName.setText(ldto.getCompanyName());

        } else {
            viewholder.tvTripName.setText(ldto.getEmail());
        }

        if (ldto.getAmount() != null && !ldto.getAmount().equals(""))
            viewholder.tvamount.setText((decimalFormat.format(Double.valueOf(ldto.getAmount()))) + " " + Prefs.getCurrency(ctx));

        return paramView;
    }

    public void updateReceiptsList(TripShare share1) {
        list.remove(share1);
        this.notifyDataSetChanged();
    }

    static class ViewHolder {
        public FontTextView tvTripName;
        public FontTextView tvamount;
    }
}