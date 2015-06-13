package com.Giddh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.dtos.UserEmail;

import java.util.ArrayList;

public class TripEmailsAdapter extends BaseAdapter implements Filterable {
    Context ctx;
    ArrayList<UserEmail> list, suggestions, emailAllClone;

    private SparseBooleanArray mSelectedItemsIds;

    public TripEmailsAdapter(ArrayList<UserEmail> paramArrayList, Context paramContext) {
        mSelectedItemsIds = new SparseBooleanArray();
        this.list = paramArrayList;
        this.ctx = paramContext;
        suggestions = new ArrayList<>();
        emailAllClone = paramArrayList;
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
            paramView = LayoutInflater.from(ctx).inflate(R.layout.email_row, paramViewGroup, false);
            ViewHolder viewholder = new ViewHolder();
            viewholder.tvNames = ((TextView) paramView.findViewById(R.id.name_user));
            viewholder.tvEmail = ((TextView) paramView.findViewById(R.id.email_id));
            viewholder.photo = ((ImageView) paramView.findViewById(R.id.contact_image));
            paramView.setTag(viewholder);
        }

        ViewHolder viewholder = (ViewHolder) paramView.getTag();
        if (list.size() >= paramInt) {
            UserEmail email_dto = list.get(paramInt);

            viewholder.tvNames.setText(email_dto.getName());
            viewholder.tvEmail.setText(email_dto.getEmail());
            paramView.setBackgroundColor(mSelectedItemsIds.get(paramInt) ? 0x9934B5E4
                    : Color.TRANSPARENT);
        }

        return paramView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
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

    Filter nameFilter = new Filter() {

//        public String convertResultToString(Object resultValue) {
//            return ((UserEmail) (resultValue)).getEmail();
//        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && !constraint.equals("")) {
                suggestions.clear();
                for (UserEmail email : emailAllClone) {
                    if (email.getEmail().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(email);
                    }
                }
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
                notifyDataSetChanged();
                return filterResults;
            } else {
                return filterResults;
            }
        }


        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {


            if (results != null && results.count > 0) {
//                clear();
//                for (Contacts c : filteredList) {
//                    add(c);
//                }
                list = (ArrayList<UserEmail>) results.values;
                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        }
    };
}