package com.Giddh.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.dtos.Company;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by walkover on 27/2/15.
 */
public class CompanyListAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Company> list;

    public CompanyListAdapter(ArrayList<Company> paramArrayList, Context paramContext) {
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.companynames_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvCompanyNames = ((TextView) paramView.findViewById(R.id.company_name));
            viewholder.financial_year = ((TextView) paramView.findViewById(R.id.fin_yr));
            paramView.setTag(viewholder);
        }
        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        Company ldto;
        ldto = list.get(paramInt);
        viewholder.tvCompanyNames.setText(ldto.getCompanyName());
        if (!ldto.getCompanyType().equals("1")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
            try {
                Date fin_yr = sdf.parse(ldto.getFinancialYear());
                Calendar cal = Calendar.getInstance();
                cal.setTime(fin_yr);
                cal.add(Calendar.YEAR, -1);
                viewholder.financial_year.setText(sdf.format(cal.getTime()) + " - " + sdf.format(fin_yr));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            viewholder.financial_year.setText("");
        }
        return paramView;
    }

    static class ViewHolder {
        public TextView tvCompanyNames, financial_year;
    }
}