package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

import com.Giddh.R;
import com.Giddh.commonUtilities.VariableClass;
import com.Giddh.dtos.Accounts;
import com.Giddh.util.UserService;
import com.rengwuxian.materialedittext.MaterialEditText;


public class AddBankAccount extends AppCompatActivity implements View.OnTouchListener {
    MaterialEditText selectbank, add_account_no, add_ifsc, add_opening_bal;
    Context context;
    ImageButton save;
    UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bank_account_info);
        context = AddBankAccount.this;
        userService = UserService.getUserServiceInstance(context);
        selectbank = (MaterialEditText) findViewById(R.id.select_bank);
        selectbank.setInputType(InputType.TYPE_NULL);
        save = (ImageButton) findViewById(R.id.save);
        add_account_no = (MaterialEditText) findViewById(R.id.add_account_no);
        add_ifsc = (MaterialEditText) findViewById(R.id.add_ifsc);
        add_opening_bal = (MaterialEditText) findViewById(R.id.add_opening_bal);
        add_account_no.requestFocus();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!selectbank.getText().toString().equals("") && !add_account_no.getText().equals("") && !add_ifsc.getText().toString().equals("") &&
                        !add_opening_bal.getText().equals("")) {
                    Accounts accounts1 = new Accounts();
                    int id = userService.getmaxaccId() + 1;
                    accounts1.setAcc_webId(String.valueOf(id));
                    accounts1.setAccountName(selectbank.getText().toString());
                    accounts1.setGroupId("3");
                    accounts1.setOpeningBalance(Double.parseDouble(add_opening_bal.getText().toString()));
                    accounts1.setBank_ifsc(add_ifsc.getText().toString());
                    accounts1.setBank_account_number(add_account_no.getText().toString());
                    userService.addaccountsdata(accounts1);
                }
            }
        });
        selectbank.setOnTouchListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0:
                    String bank = (String) data.getExtras().getSerializable(VariableClass.Vari.SELECTEDDATA);
                    if (bank != null) {
                        selectbank.setText(bank);
                        selectbank.requestFocus();
                    }
                    break;
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            switch (v.getId()) {
                case R.id.select_bank:
                    Intent intent = new Intent(context, BankList.class);
                    startActivityForResult(intent, 0);
                    break;
            }
        }
        return false;
    }
}
