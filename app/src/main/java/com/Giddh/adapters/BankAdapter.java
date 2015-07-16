package com.Giddh.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.Giddh.R;
import com.Giddh.commonUtilities.FontTextView;
import com.Giddh.commonUtilities.RecyclerViewClickListener;

import java.util.ArrayList;
import java.util.List;


public class BankAdapter extends RecyclerView.Adapter<BankAdapter.CustomViewHolder> implements Filterable {
    private List<String> feedItemList;
    private Context mContext;
    private List<String> orig;
    private static RecyclerViewClickListener itemListener;

    public BankAdapter(Context context, List<String> feedItemList, RecyclerViewClickListener itemListener) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.itemListener = itemListener;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.bank_row, null);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int i) {

        customViewHolder.textView.setText(feedItemList.get(i));
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                final FilterResults oReturn = new FilterResults();
                final List<String> results = new ArrayList<>();
                if (orig == null)
                    orig = feedItemList;
                if (constraint != null) {
                    if (orig != null & orig.size() > 0) {
                        for (final String g : orig) {
                            if (g.toLowerCase().contains(constraint.toString())) results.add(g);
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                feedItemList = (ArrayList<String>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public Object getItem(int paramInt) {
        return this.feedItemList.get(paramInt);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected FontTextView textView;

        public CustomViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            this.textView = (FontTextView) view.findViewById(R.id.title);
        }

        @Override
        public void onClick(View v) {
            itemListener.recyclerViewListClicked(v, this.getLayoutPosition());
        }
    }


}