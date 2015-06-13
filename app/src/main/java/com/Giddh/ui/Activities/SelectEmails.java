package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.Giddh.R;
import com.Giddh.adapters.UserEmailsAdapter;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.UserEmail;
import com.splunk.mint.Mint;

import java.util.ArrayList;

public class SelectEmails extends AppCompatActivity implements AdapterView.OnItemClickListener {
    UserEmail emailDto;
    Context ctx;
    ListView lvEmail;
    UserEmailsAdapter tripEmailsAdapter;
    private ActionMode mActionMode;
    ArrayList<UserEmail> selections;
    Boolean hasEmail = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_email);
        ctx = SelectEmails.this;
        Mint.initAndStartSession(ctx, CommonUtility.BUGSENSEID);
        Mint.setUserIdentifier(Prefs.getEmailId(ctx));
        init();
        lvEmail.setFastScrollEnabled(true);
    }

    void init() {
        emailDto = (UserEmail) getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        lvEmail = (ListView) findViewById(R.id.list_selected);
//        Log.e("values", emailDto.getEmail() + emailDto.getName());
        tripEmailsAdapter = new UserEmailsAdapter(emailDto.getAllEmails(), ctx, SelectEmails.this);
        lvEmail.setAdapter(tripEmailsAdapter);
        lvEmail.setOnItemClickListener(this);
        selections = new ArrayList<>();
        lvEmail.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                onListItemSelect(position);
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.context_menu, menu);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mActionMode == null) {
            emailDto = (UserEmail) parent.getItemAtPosition(position);
            selections.add(emailDto);
            emailDto.setAllEmails(selections);
            hasEmail = true;
            SelectEmails.this.finish();
        } else
            // add or remove selection for current list item
            onListItemSelect(position);
    }

    private void onListItemSelect(int position) {
        tripEmailsAdapter.toggleSelection(position);
        boolean hasCheckedItems = tripEmailsAdapter.getSelectedCount() > 0;
        if (hasCheckedItems && mActionMode == null)
            // there are some selected items, start the actionMode
            mActionMode = startActionMode(new ActionModeCallback());
        else if (!hasCheckedItems && mActionMode != null)
            // there no selected items, finish the actionMode
            mActionMode.finish();
        if (mActionMode != null)
            mActionMode.setTitle(String.valueOf(tripEmailsAdapter
                    .getSelectedCount()) + " selected");
    }

    private class ActionModeCallback implements ActionMode.Callback {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // inflate contextual menu
            mode.getMenuInflater().inflate(R.menu.context_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.next_page:
                    // retrieve selected items and delete them out
                    SparseBooleanArray selected = tripEmailsAdapter
                            .getSelectedIds();
                    for (int i = (selected.size() - 1); i >= 0; i--) {
                        if (selected.valueAt(i)) {
                            emailDto = (UserEmail) tripEmailsAdapter.getItem(selected.keyAt(i));
                            selections.add(emailDto);
                        }
                    }
                    hasEmail = true;
                    emailDto.setAllEmails(selections);
                    SelectEmails.this.finish();
                    mode.finish(); // Action picked, so close the CAB
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            // remove selection
            tripEmailsAdapter.removeSelection();
            mActionMode = null;
        }
    }

    @Override
    public void finish() {
        Log.e("on finish  called ", "on finish ");
        if (hasEmail) {
            Intent data = new Intent();
            data.putExtra(VariableClass.Vari.SELECTEDDATA, emailDto);
            setResult(1001, data);
            super.finish();
        } else {
            super.finish();
        }
    }
}
