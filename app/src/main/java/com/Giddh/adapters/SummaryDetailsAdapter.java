package com.Giddh.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.SummaryAccount;
import com.Giddh.dtos.SummaryEntry;
import com.Giddh.util.UserService;

import java.util.ArrayList;
import java.util.HashMap;

public class SummaryDetailsAdapter extends BaseExpandableListAdapter {
    private Context _context;
    private ArrayList<SummaryEntry> _listDataHeader = new ArrayList<>(); // header titles
    ArrayList<SummaryAccount> childitem;

    UserService userService;
    Boolean TRIPSUMMARY;

    public SummaryDetailsAdapter(Context context, ArrayList<SummaryEntry> listall, Boolean Trip) {
        this._context = context;
        this._listDataHeader = listall;
        userService = UserService.getUserServiceInstance(_context);
        TRIPSUMMARY = Trip;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return _listDataHeader.get(groupPosition).getEntries().get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        childitem = _listDataHeader.get(groupPosition).getEntries();
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.saved_trips_row, null);
        }
        TextView txtListChildName = (TextView) convertView
                .findViewById(R.id.name);
        TextView txtamount = (TextView) convertView
                .findViewById(R.id.amount);
        if (TRIPSUMMARY) {
            txtListChildName.setText(childitem.get(childPosition).getAccountName());
        } else {
            Log.e("accountid", "" + childitem.get(childPosition).getAccountId());
            txtListChildName.setText(userService.getaccountnameorId(childitem.get(childPosition).getAccountId()).getAccountName());
        }
        txtamount.setText(String.valueOf(childitem.get(childPosition).getClosingBal()) + " " + Prefs.getCurrency(_context));
        if (childitem.get(childPosition).getTransactionType().equals("1")) {
            txtamount.setTextColor(Color.parseColor("#F44336"));
        } else if (childitem.get(childPosition).getTransactionType().equals("0")) {
            txtamount.setTextColor(Color.parseColor("#03A9F4"));
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return _listDataHeader.get(groupPosition).getEntries().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.summary_detail_headrow, null);
        }
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.searchrate_country_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(_listDataHeader.get(groupPosition).getDate());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}