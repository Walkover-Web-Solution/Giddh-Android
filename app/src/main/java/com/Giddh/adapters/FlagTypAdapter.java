package com.Giddh.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.dtos.Accounts;

import java.util.ArrayList;

/**
 * Created by walkover on 27/2/15.
 */

public class FlagTypAdapter extends BaseAdapter {
    Context ctx;
    ArrayList<Accounts> list;
    FlagTypAdapter adapter;
    Boolean isAtm;
    Activity mActivity;
    static Integer[] imageId = {
            R.drawable.electricity_bill,
            R.drawable.food_icon_large,
            R.drawable.loan_large,
            R.drawable.movies_large,
            R.drawable.other_large,
            R.drawable.petrol_icon_large,
            R.drawable.phone_bill_large,
            R.drawable.rent_large,
            R.drawable.shopping_large,
            R.drawable.travel_large,
            R.drawable.water_bill_large,
            R.drawable.salary_large,
            R.drawable.cash_large,
            R.drawable.creditcard_large,
            R.drawable.cash_withdraw_large,
            R.drawable.newspaper_large

    };


    public FlagTypAdapter(ArrayList<Accounts> paramArrayList, Context paramContext, Activity activity, Boolean Atm) {
        this.list = paramArrayList;
        this.ctx = paramContext;
        this.mActivity = activity;
        this.isAtm = Atm;
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
        ViewHolder viewholder;
        Accounts dto = list.get(paramInt);
        if (paramView == null) {
            paramView = LayoutInflater.from(ctx).inflate(R.layout.grid_cell, null);
            viewholder = new ViewHolder();
            viewholder.tvName = ((TextView) paramView.findViewById(R.id.grid_text));
            viewholder.imageTag = ((ImageView) paramView.findViewById(R.id.grid_image));
            viewholder.tick = ((ImageView) paramView.findViewById(R.id.tick_image));
            paramView.setTag(viewholder);
            Log.e("creating new row" + dto.getAccountName(), "creating row");
        } else {
            Log.e("not creating new row" + dto.getAccountName(), " not creating row");
            viewholder = (ViewHolder) paramView.getTag();
        }
        viewholder.tvName.setText(dto.getAccountName());
        if (dto.getAccountName() != null) {
            switch (dto.getAccountName().toLowerCase()) {
                case "food":
                    viewholder.imageTag.setImageResource(imageId[1]);
                    break;
                case "petrol":
                    viewholder.imageTag.setImageResource(imageId[5]);
                    break;
                case "phone bill":
                    viewholder.imageTag.setImageResource(imageId[6]);
                    break;
                case "electricity bill":
                    viewholder.imageTag.setImageResource(imageId[0]);
                    break;
                case "rent":
                    viewholder.imageTag.setImageResource(imageId[7]);
                    break;
                case "other":
                    viewholder.imageTag.setImageResource(imageId[4]);
                    break;
                case "shopping":
                    viewholder.imageTag.setImageResource(imageId[8]);
                    break;
                case "salary":
                    viewholder.imageTag.setImageResource(imageId[11]);
                    break;
                case "loan":
                    viewholder.imageTag.setImageResource(imageId[2]);
                    break;
                case "travelling":
                    viewholder.imageTag.setImageResource(imageId[9]);
                    break;
                case "cash":
                    viewholder.imageTag.setImageResource(imageId[12]);
                    break;
                case "credit card":
                    viewholder.imageTag.setImageResource(imageId[13]);
                    break;
                case "water bill":
                    viewholder.imageTag.setImageResource(imageId[10]);
                    break;
                case "movies":
                    viewholder.imageTag.setImageResource(imageId[3]);
                    break;
                case "atm withdraw":
                    viewholder.imageTag.setImageResource(imageId[14]);
                    break;
                case "books/news paper":
                    viewholder.imageTag.setImageResource(imageId[15]);
                    break;
                default:
                    viewholder.imageTag.setImageBitmap(CommonUtility.drawImageGrid(dto.getAccountName(), mActivity));
                    break;

            }
        }


        return paramView;
    }


    private static class ViewHolder {

        TextView tvName;
        ImageView imageTag;
        ImageView tick;
    }
}