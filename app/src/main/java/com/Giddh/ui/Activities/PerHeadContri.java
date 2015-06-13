package com.Giddh.ui.Activities;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.Giddh.R;
import com.Giddh.commonUtilities.Prefs;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.TripInfo;
import com.Giddh.dtos.TripShare;
import com.Giddh.util.UserService;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PerHeadContri extends Fragment {
    View v;
    Context ctx;
    UserService userService;
    static TripInfo tripInfo;
    TextView total_expense, today_expense, expense_per_head;
    Double perhead, today_total, total;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.per_head_chart, container, false);
        init();
        return v;
    }

    void init() {
        ctx = getActivity();
        userService = UserService.getUserServiceInstance(ctx);
        tripInfo = (TripInfo) getActivity().getIntent().getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
        total_expense = (TextView) v.findViewById(R.id.total_expense);
        today_expense = (TextView) v.findViewById(R.id.today_expense);
        expense_per_head = (TextView) v.findViewById(R.id.expense_per_head);

    }

    public static PerHeadContri newInstance(int page, String title) {
       /* tripInfo = tripInfo1;*/
        PerHeadContri f = new PerHeadContri();
        Bundle b = new Bundle();
        b.putString("someTitle", title);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onResume() {
        super.onResume();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        total = (userService.getperheadcontri(tripInfo.getTripId(), null, true, true)) - (userService.getperheadcontri(tripInfo.getTripId(), null, true, false));
        if (total < 0)
            total = total * -1;
        today_total = (userService.getperheadcontri(tripInfo.getTripId(), sdf.format(date), false, false));
        final Handler handler = new Handler();
        Timer timer = new Timer(false);
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        int size = userService.getallshareTrips(tripInfo.getTripId(), true, null, true).size();

                        Log.e("members_size", "" + size);
                        if (total > 0 && size > 0) {
                            perhead = total / size;
                            if (perhead != null)
                                expense_per_head.setText(new DecimalFormat("##.##").format(perhead) + " " + Prefs.getCurrency(ctx));
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 750); // 1000 = 1 second.
        Log.e("values_total", "" + total + " today=" + today_total);
        if (total != null)
            total_expense.setText(new DecimalFormat("##.##").format(total) + " " + Prefs.getCurrency(ctx));
        if (today_total != null)
            today_expense.setText(new DecimalFormat("##.##").format(today_total) + " " + Prefs.getCurrency(ctx));

    }
}
