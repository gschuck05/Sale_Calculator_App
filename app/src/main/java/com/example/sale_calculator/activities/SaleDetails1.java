package com.example.sale_calculator.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.sale_calculator.R;

import java.util.Locale;

public class SaleDetails1 extends AppCompatActivity {

    private TextView tvSale, tvCost, tvDiscount, tvSaleCost, tvCostAmount;
    private SaleCalc saleCalc;

    private static final String sFORMAT_STRING = "%2.2f";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale_details1);
        setupToolbar();
        setupFAB();
        setupViews();
        getIncomingData();
        populateViews();
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void getIncomingData() {
        Intent intent = getIntent();
        String gameJSON = intent.getStringExtra("GAME");
        saleCalc = SaleCalc.getGameFromJSON(gameJSON);
        Log.i("saleCalc object", "sale calc object when setting = " + saleCalc);
    }

    private void setupViews() {
        tvSale = findViewById(R.id.tvSaleAmount);
        tvCost = findViewById(R.id.tvCostAmount);
        tvDiscount = findViewById(R.id.tvDiscountAmount);
        tvSaleCost = findViewById(R.id.tvSalePriceAmount);
    }
    private void populateViews(){
        final String saleString, costString, discountString, saleCostString;

        double saleAmnt = saleCalc.getSale();
        double costAmnt = saleCalc.getCost();
        double discountAmnt = saleCalc.getDiscount();
        double saleCostAmnt = saleCalc.getSalePrice();

        saleString = String.format(Locale.US, sFORMAT_STRING, saleAmnt);
        costString = String.format(Locale.US, sFORMAT_STRING, costAmnt);
        discountString = String.format(Locale.US, sFORMAT_STRING, discountAmnt);
        saleCostString = String.format(Locale.US, sFORMAT_STRING, saleCostAmnt);

        tvSale.setText(String.valueOf(saleAmnt));
        tvCost.setText(costString);
        tvDiscount.setText(discountString);
        tvSaleCost.setText(saleCostString);
    }

    private void setupFAB() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        } else return super.onOptionsItemSelected(item);
    }
}