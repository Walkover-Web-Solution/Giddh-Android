package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.adapters.SavedEmailsAdapter;
import com.Giddh.commonUtilities.Apis;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.TripInfo;
import com.Giddh.dtos.TripShare;
import com.Giddh.util.UserService;
import com.afollestad.materialdialogs.AlertDialogWrapper;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class SavedEmails extends Fragment {
    ListView lvSavedTrips;
    ImageButton addNewTrip;
    UserService userService;
    Context ctx;
    TripInfo tripInfo;
    TripShare tripShare;
    ArrayList<TripShare> savedEmails;
    ArrayList<TripShare> tripsInDb;
    SavedEmailsAdapter savedTripsAdapter;
    RelativeLayout footer;
    TextView tvHead;
    MaterialDialog mt;
    View v;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.saved_trips, container, false);
        init();
        return v;
    }

    void init() {
        ctx = getActivity();
        userService = UserService.getUserServiceInstance(ctx);
        tripInfo = (TripInfo) getActivity().getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        savedEmails = new ArrayList<>();
        tripsInDb = new ArrayList<>();
        lvSavedTrips = (ListView) v.findViewById(R.id.saved_trips);
        addNewTrip = (ImageButton) v.findViewById(R.id.add_new_trip);
        footer = (RelativeLayout) v.findViewById(R.id.foot);
        tvHead = (TextView) v.findViewById(R.id.total_per_user);
        addNewTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(ctx, TripHome.class);
                edit.putExtra("EditMode", true);
                edit.putExtra(VariableClass.Vari.EDITTRIP, tripInfo);
                startActivity(edit);
            }
        });
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit = new Intent(ctx, TripHome.class);
                edit.putExtra("EditMode", true);
                edit.putExtra(VariableClass.Vari.EDITTRIP, tripInfo);
                startActivity(edit);
            }
        });
        tvHead.setText("Shared with");
        lvSavedTrips.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TripShare tripsh = (TripShare) parent.getItemAtPosition(position);
                Intent detail = new Intent(ctx, SummaryTrip.class);
                detail.putExtra(VariableClass.Vari.SELECTEDDATA, tripsh);
                ctx.startActivity(detail);
            }
        });
        lvSavedTrips.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final TripShare share = (TripShare) parent.getItemAtPosition(position);
                if (share.getEmail() != null && share.getOwner() != null)
                    if (!share.getEmail().equals(Prefs.getEmailId(ctx)) && !share.getOwner().equals("1") && tripInfo.getOwner().equals("1")) {
                        new MaterialDialog.Builder(ctx)
                                .items(R.array.remove_mail)
                                .itemsCallback(new MaterialDialog.ListCallback() {
                                    @Override
                                    public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                        new AlertDialogWrapper.Builder(ctx)
                                                .setTitle("Remove " + share.getEmail() + " from trip?")
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        dialog.dismiss();
                                                    }
                                                }).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                new removemailfromtrip().execute(share.getEmail());
                                                userService.deleteEmail(share.getTripId(), share.getEmail());
                                                tripsInDb.remove(position);
                                                savedTripsAdapter.updateReceiptsList(share);
                                                savedTripsAdapter.notifyDataSetChanged();
                                            }
                                        }).show();
                                    }
                                })
                                .show();
                    }
                return true;
            }
        });
        new tripList().execute();
        tripsInDb = userService.getallshareTrips(tripInfo.getTripId(), true, null,false);
        for (int i = 0; i < tripsInDb.size(); i++) {
            Double sum = userService.getTripUserEntryAmount
                    (tripsInDb.get(i).getTripId(), tripsInDb.get(i).getCompanyId(), true) - userService.getTripUserEntryAmount
                    (tripsInDb.get(i).getTripId(), tripsInDb.get(i).getCompanyId(), false);
            tripsInDb.get(i).setAmount(String.valueOf(sum));
        }
        savedTripsAdapter = new SavedEmailsAdapter(tripsInDb, ctx);
        lvSavedTrips.setAdapter(savedTripsAdapter);
        /*Double total = (userService.getperheadcontri(tripInfo.getTripId(), null, true, true)) - (userService.getperheadcontri(tripInfo.getTripId(), null, true, false));
        Double perhead = total / tripsInDb.size();
        tvHead.setText("TOTAL : " + new DecimalFormat("##.##").format(total) + " Per head:" + new DecimalFormat("##.##").format(perhead));*/
    }

    class removemailfromtrip extends AsyncTask<String, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            // CommonUtility.dialog.dismiss();
            if (iserr) {
            } else {
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // showErrorMessage(false, "");
            // CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(String... params) {
            response = Apis.getApisInstance(ctx).RemoveEmail(tripInfo.getTripId(), params[0]);
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        // japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

    class tripList extends AsyncTask<Void, Void, Void> {
        String response = null;
        Boolean iserr = false;

        @Override
        protected void onPostExecute(Void result) {
            mt.dismiss();
            if (iserr) {
                //showErrorMessage(true, response);
            } else {
                tripsInDb = userService.getallshareTrips(tripInfo.getTripId(), true, null,false);
                for (int i = 0; i < tripsInDb.size(); i++) {
                    Double sum = userService.getTripUserEntryAmount
                            (tripsInDb.get(i).getTripId(), tripsInDb.get(i).getCompanyId(), true) - userService.getTripUserEntryAmount
                            (tripsInDb.get(i).getTripId(), tripsInDb.get(i).getCompanyId(), false);
                    tripsInDb.get(i).setAmount(String.valueOf(sum));
                }
                savedTripsAdapter = new SavedEmailsAdapter(tripsInDb, ctx);
                lvSavedTrips.setAdapter(savedTripsAdapter);
            }
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            // showErrorMessage(false, "");
            mt = new MaterialDialog.Builder(ctx)
                    .title("Loading")
                    .content(R.string.please_wait)
                    .progress(true, 0)
                    .show();
            //CommonUtility.show_PDialog(ctx, getResources().getString(R.string.please_wait));
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            response = Apis.getApisInstance(ctx).getTripInfomails(tripInfo.getTripId());
            if (!response.equals("")) {
                JSONObject joparent = null;
                JSONObject jochild = null;
                JSONArray japarent = null;
                try {
                    joparent = new JSONObject(response);
                    if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.ErrorResponse)) {
                        iserr = true;
                        jochild = joparent.getJSONObject(VariableClass.ResponseVariables.RESPONSEMESSAGE);
                        response = jochild.getString(VariableClass.ResponseVariables.ERRORMESSAGE);
                    }
                    //success response
                    else if (joparent.getString(VariableClass.ResponseVariables.RESPONSE).equals(Apis.SuccessResponse)) {
                        japarent = joparent.getJSONArray(VariableClass.ResponseVariables.DATA);
                        if (japarent.length() > 0)
                            for (int i = 0; i < japarent.length(); i++) {
                                tripShare = new TripShare();
                                jochild = japarent.getJSONObject(i);
                                tripShare.setTripId(tripInfo.getTripId());
                                tripShare.setEmail(jochild.getString(VariableClass.ResponseVariables.EMAIL));
                                tripShare.setOwner(jochild.getString(VariableClass.ResponseVariables.OWNER));
                                tripShare.setCompanyName(jochild.getString(VariableClass.ResponseVariables.COMPANY_NAME));
                                tripShare.setCompanyId(jochild.getString(VariableClass.ResponseVariables.COMPANY_ID));
                                tripShare.setCompanyType(jochild.getString(VariableClass.ResponseVariables.COMPANY_TYPE));
                                savedEmails.add(tripShare);
                                tripsInDb = userService.getallshareTrips(tripInfo.getTripId(), false, tripShare.getCompanyId(),false);
                                if (tripsInDb.size() > 0) {
                                    Log.e("will Update", "update");
                                    userService.updateTripInfoshare(tripShare, tripShare.getTripId(), tripShare.getCompanyId());
                                } else {
                                    userService.addtripshareInfodto(tripShare);
                                }
                            }
                    }
                } catch (JSONException e) {
                    iserr = true;
                    response = getResources().getString(R.string.parse_error);
                    e.printStackTrace();
                }
            } else {
                iserr = true;
                response = getResources().getString(R.string.server_error);
            }
            return null;
        }
    }

    public static SavedEmails newInstance(int page, String title) {
        SavedEmails f = new SavedEmails();
        Bundle b = new Bundle();
        b.putString("someTitle", title);
        f.setArguments(b);
        return f;
    }
}
