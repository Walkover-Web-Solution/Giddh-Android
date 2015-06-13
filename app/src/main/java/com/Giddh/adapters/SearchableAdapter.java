package com.Giddh.adapters;

/**
 * Created by walkover on 18/4/15.
 */

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.dtos.UserEmail;

// The standard text view adapter only seems to search from the beginning of whole words
// so we've had to write this whole class to make it possible to search
// for parts of the arbitrary string we want
public class SearchableAdapter extends BaseAdapter implements Filterable {

    private List<UserEmail> originalData = null;
    private List<UserEmail> filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public SearchableAdapter(Context context, List<UserEmail> data) {
        this.filteredData = data;
        this.originalData = data;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return filteredData.size();
    }

    public UserEmail getItem(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.email_row, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name_user);
            holder.email = (TextView) convertView.findViewById(R.id.email_id);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.name.setText(filteredData.get(position).getName());
        holder.email.setText(filteredData.get(position).getEmail());

        return convertView;
    }

    static class ViewHolder {
        TextView name, email;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null) {
                ArrayList<UserEmail> nlist = new ArrayList<>();
                for (int i = 0; i < originalData.size(); i++) {
                    if (originalData.get(i).getEmail().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        nlist.add(originalData.get(i));
                    }
                }
                results.values = nlist;
                results.count = nlist.size();
            }
            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null)
                if (results.values != null) {
                    filteredData = (ArrayList<UserEmail>) results.values;
                    notifyDataSetChanged();
                }
        }

    }
}