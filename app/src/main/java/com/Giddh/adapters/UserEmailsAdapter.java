package com.Giddh.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.dtos.UserEmail;

import java.util.ArrayList;
import java.util.Collections;


public class UserEmailsAdapter extends BaseAdapter implements SectionIndexer {
    Context ctx;
    ArrayList<UserEmail> list;
    UserEmail email_dto;
    private SparseBooleanArray mSelectedItemsIds;
    Activity activity;
    private static String sections = "abcdefghilmnopqrstuvz";

    public UserEmailsAdapter(ArrayList<UserEmail> paramArrayList, Context paramContext,Activity activity1) {

        mSelectedItemsIds = new SparseBooleanArray();
        this.list = paramArrayList;
        this.ctx = paramContext;
        this.activity=activity1;
        Collections.sort(list);
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.email_row, null);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvNames = ((TextView) paramView.findViewById(R.id.name_user));
            viewholder.tvEmail = ((TextView) paramView.findViewById(R.id.email_id));
            viewholder.photo = ((ImageView) paramView.findViewById(R.id.contact_image));
            paramView.setTag(viewholder);
        }

        final ViewHolder viewholder = (ViewHolder) paramView.getTag();

        email_dto = list.get(paramInt);

        viewholder.tvNames.setText(email_dto.getName());
        viewholder.tvEmail.setText(email_dto.getEmail());
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message message) {
                viewholder.photo.setImageBitmap(CommonUtility.drawImageGrid(email_dto.getEmail(),activity));
            }
        };

        /*paramView
                .setBackgroundColor(mSelectedItemsIds.get(paramInt) ? 0x9934B5E4
                        : Color.TRANSPARENT);*/

        paramView
                .setBackgroundColor(mSelectedItemsIds.get(paramInt) ? ctx.getResources().getColor(R.color.blue_mat)
                        : Color.TRANSPARENT);

        return paramView;
    }


    static class ViewHolder {
        public TextView tvNames, tvEmail;
        public ImageView photo;


    }

    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);

        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }


    @Override
    public Object[] getSections() {
        String[] sectionsArr = new String[sections.length()];
        for (int i = 0; i < sections.length(); i++)
            sectionsArr[i] = "" + sections.charAt(i);

        return sectionsArr;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < this.getCount(); i++) {

            String item = list.get(i).getEmail().toLowerCase();
            if (item != null && !item.equals("")){
                if (item.charAt(0) == sections.charAt(sectionIndex))
                    return i;
            }
        }

        return 0;
    }

    @Override
    public int getSectionForPosition(int position) {
        return 0;
    }

}