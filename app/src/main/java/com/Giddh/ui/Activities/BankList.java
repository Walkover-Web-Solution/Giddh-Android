package com.Giddh.ui.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.SearchView;

import com.Giddh.R;
import com.Giddh.adapters.BankAdapter;
import com.Giddh.commonUtilities.CommonUtility;
import com.Giddh.commonUtilities.DividerItemDecoration;
import com.Giddh.commonUtilities.RecyclerViewClickListener;
import com.Giddh.commonUtilities.VariableClass;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class BankList extends AppCompatActivity implements SearchView.OnQueryTextListener, RecyclerViewClickListener {
    RecyclerView mBankList;
    SearchView searchView;
    BankAdapter bankAdapter;
    Context context;
    String bankname;
    String banks[] = {"Abhyudaya Co-op Bank Ltd", "Abu Dhabi Commercial Bank Ltd", "AB Bank Ltd.",
            "Ahmedabad Mercantile Co-op Bank", "Allahabad Bank", "Andhra Bank", "Antwerp Diamond Bank Mumbai",
            "Australia and New Zealand Banking Group Limited", "Axis Bank", "Bank Of America", "Bank Of Bahrain And Kuwait",
            "Bank of Baroda", "Bank Of Ceylon", "Bank of India", "Bank Of Maharashtra", "Bank Of Nova Scotia", "Bank Of Tokyo-Mitsubishi Ufj Ltd",
            "Bank Internasional Indonesia", "Barclays Bank Plc", "Bassein Catholic Co-op Bank Ltd", "Bharat Co-op Bank (Mumbai) Ltd", "BNP Paribas", "Canara Bank",
            "Catholic Syrian Bank Ltd", "Central Bank of India", "Chinatrust Commercial Bank", "Citibank India", "Citizencredit Co-op Bank Ltd", "City Union Bank Ltd",
            "Corporation Bank", "Cosmos Co-op Bank Ltd", "Credit Agricole Corp and Investment Bank", "Commonwealth Bank of Australia", "DBS Bank", "Dena Bank", "Deutsche Bank Ag",
            "Development Credit Bank", "Dhanlaxmi Bank Ltd", "Dicgc", "Doha Bank", "Dombivli Nagari Sahakari Bank Ltd", "Export-Import Bank of India", "Federal Bank Ltd", "Firstrand Bank Ltd",
            "Greater Bombay Co-op Bank Ltd", "HDFC Bank", "HSBC", "HSBC Bank Oman S.A.O.G.", "ICICI Bank", "IDBI Bank", "Indian Bank", "Indian Overseas Bank",
            "IndusInd Bank Ltd.", "Industrial and Commercial Bank of China Ltd.", "ING Vysya Bank", "Jammu & Kashmir Bank Ltd.", "Janakalyan Sahakari Bank Ltd",
            "Janata Sahkari Bank Ltd Pune", "Jpmorgan Chase Bank", "Kalupur Commercial Co-op Bank Ltd", "Kalyan Janata Sahakari Bank Ltd",
            "Kapole Co-op Bank", "Karnataka Bank Ltd.", "Karnataka State Co-op Apex Bank", "Karur Vysya Bank", "Kotak Mahindra Bank", "Krung Thai Bank PCL Mumbai",
            "Lakshmi Vilas Bank Ltd", "Mahanagar Co-op Bank Ltd", "Maharashtra State Co-operative Bank Limited", "Mashreq Bank Psc", "Mehsana Urban Co-op Bank Ltd",
            "Mizuho Corporate Bank Ltd", "Nainital Bank Ltd", "National Australia Bank", "New India Co-op Bank Ltd", "Nkgsb Co-op Bank Ltd", "Nutan Nagarik Sahakari Bank Ltd",
            "Oman International Bank Saog", "Oriental Bank of Commerce", "Parsik Janata Sahakari Bank Ltd", "Punjab and Maharashtra Bank Ltd.", "Punjab National Bank",
            "Punjab & Sind Bank", "Rajkot Nagarik Sahakari Bank Ltd", "Ratnakar Bank Ltd", "Reserve Bank Of India", "Royal bank of scotland", "Rabobank International", "Sberbank",
            "Saraswat Co-operative Bank Ltd.", "Shamrao Vithal Co-op Bank Ltd", "Shinhan Bank", "Societe Generale", "Sonali Bank Ltd.", "South Indian Bank", "Standard Chartered Bank",
            "State Bank Of Bikaner And Jaipur", "State Bank of Hyderabad", "State Bank of India", "SBI Global Factors Ltd(SBIGFL)factoring Unit", "India Factoring and Finance Solutions Pvt. Ltd",
            "IFCI Factors Limited", "Bombay Mercantile Co-operative bank Limited", "State Bank Of Mauritius Ltd", "State Bank of Mysore", "State Bank Of Patiala", "State Bank Of Travancore",
            "State Bank of Saurashtra", "Sumitomo Mitsui Banking Corporation", "Surat Peoples Co-op Bank Ltd", "Syndicate Bank", "Tamilnad Mercantile Bank Ltd", "Thane Janta Sahakari Bank, Ltd.",
            "The Karad Urban Co-op Bank Ltd", "The Nasik Merchants Co-op Bank Ltd", "UBS AG", "UCO Bank", "Union Bank of India", "United Bank of India",
            "United Overseas Bank", "Vijaya Bank", "West Bengal State Co-op Bank Ltd", "Westpac Banking Corporation", "Woori Bank", "Yes Bank"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.bank_list);
        context = BankList.this;
        mBankList = (RecyclerView) findViewById(R.id.bank_recycler);
        mBankList.setLayoutManager(new LinearLayoutManager(this));
        mBankList.setItemAnimator(new DefaultItemAnimator());
        mBankList.addItemDecoration(new DividerItemDecoration(getResources().getDrawable(R.drawable.abc_list_divider_mtrl_alpha)));
        searchView = (SearchView) findViewById(R.id.search_bank);
        searchView.setIconifiedByDefault(false);
        searchView.setOnQueryTextListener(this);
        searchView.setSubmitButtonEnabled(true);
        searchView.setBackgroundColor(R.color.orange_footer_head);
        searchView.setQueryHint("Search Here");
        List<String> banklist = new ArrayList<>(Arrays.asList(banks));
        bankAdapter = new BankAdapter(context, banklist, this);
        mBankList.setAdapter(bankAdapter);
        mBankList.requestFocus();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            bankAdapter.getFilter().filter("");
        } else {
            bankAdapter.getFilter().filter(newText.toString());
        }
        return true;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)  {
        if (keyCode == KeyEvent.KEYCODE_BACK) {

            BankList.this.finish();
        }
        return true;
    }
    @Override
    public void onBackPressed() {
        BankList.this.finish();
        Log.e("BankList", "BankListcalled");
    }

    @Override
    public void recyclerViewListClicked(View v, int position) {
        bankname = (String) bankAdapter.getItem(position);
        BankList.this.finish();
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra(VariableClass.Vari.SELECTEDDATA, bankname);
        setResult(RESULT_OK, data);
        super.finish();
    }
}
