package com.example.sale_calculator.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.example.sale_calculator.R;
import com.example.sale_calculator.lib.DialogUtils;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.util.Pair;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

import static androidx.preference.PreferenceManager.getDefaultSharedPreferences;
import static com.example.sale_calculator.activities.SaleCalc.getJSONFromGame;

public class MainActivity extends AppCompatActivity {

    private SaleCalc mSaleCalc;               // model
    private EditText mEditTextSale, mEditTextCost;
    private Snackbar mSnackBar;
    private int mCalculationsDone;
    private static final String sFORMAT_STRING = "%2.2f";
    private final String mKEY_GAME = "GAME";

    private boolean mUseAutoSave;
    private String mKEY_AUTO_SAVE;

    //added for checkboxes settings...
    private boolean mPrefUseAutoSave;
    private final String mKeyPrefsName = "PREFS";
    private String mKeyAutoSave, mKeyShowErrors;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupToolbar();
        setupFAB();
        setupFields();
//        restoreAppSettingsFromPrefs();
        /*
        restoreAppSettingsFromPrefs();
        doInitialStartGame(savedInstanceState);*/
    }

    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFAB() {
        ExtendedFloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleFABClick();

            }
        });
    }

    private void handleFABClick() {
        Pair<String, String> saleAndCost = new Pair<>(
                mEditTextSale.getText().toString(),
                mEditTextCost.getText().toString());
        if (isValidFormData(saleAndCost)) {                    // check view/user input
            mCalculationsDone++;
            setModelFieldsSaleAndCostTo(saleAndCost);
            String msg = generateFormattedStringOfSaleFromModel();
            showMessageWithLinkToResultsActivity(msg);
        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.error_msg_sale_or_cost_not_valid,
                    Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidFormData(Pair<String, String> saleAndCost) {
        String sale = saleAndCost.first;
        String cost = saleAndCost.second;

        return sale != null && cost != null &&
                sale.length() > 0 && cost.length() > 0
                && Double.parseDouble(sale) > 0
                && Double.parseDouble(cost) > 0;
    }

    private void setModelFieldsSaleAndCostTo(
            Pair<String, String> saleAndCost) {
        assert saleAndCost.first != null;
        assert saleAndCost.second != null;

        double sale = Double.parseDouble(saleAndCost.first);
        double cost = Double.parseDouble(saleAndCost.second);

        if (mSaleCalc == null)
            mSaleCalc = new SaleCalc(sale, cost);
        else
        {
            mSaleCalc.setSale(sale);
            mSaleCalc.setCost(cost);
        }
    }

    private String generateFormattedStringOfSaleFromModel() {
        final double salePrice;
        final double originalCost;
        final double discount;
        final String saleString, orgCostString, discountString;

        salePrice = mSaleCalc.getSalePrice();
        originalCost = mSaleCalc.getCost();
        discount = mSaleCalc.getDiscount();
        saleString = String.format(Locale.US, sFORMAT_STRING, salePrice);
        orgCostString = String.format(Locale.US, sFORMAT_STRING, originalCost);
        discountString = String.format(Locale.US, sFORMAT_STRING, discount);
//        bmiGroup = mSaleCalc.getBmiGroup();

        return String.format(Locale.getDefault(),
                "%s %s; %s %s; %s %s",
                getString(R.string.you_pay), saleString,
                getString(R.string.you_save), discountString,
                getString(R.string.original_price), orgCostString);
    }

    private void showMessageWithLinkToResultsActivity(String msg) {
        mSnackBar.setText(msg);
//        mSnackBar.setAction(getString(R.string.details), mResultsListener);
        mSnackBar.show();
    }

    //setupFields()
    private void setupFields() {
        mEditTextSale = findViewById(R.id.et_sale);
        mEditTextCost = findViewById(R.id.et_cost);
        mKEY_AUTO_SAVE = getString(R.string.auto_save_key);

        View layoutMain = findViewById(R.id.main_activity);
        mSnackBar = Snackbar.make(layoutMain, "", Snackbar.LENGTH_INDEFINITE);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(mKEY_GAME, getJSONFromGame(mSaleCalc));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.action_settings:
                showSettings();
                return true;
            case R.id.action_about:
                showAbout();
                return true;
            case R.id.action_sale_details:
                showSaleDetails();
                return true;
//            case R.id.action_toggle_auto_save:
//                toggleMenuItem(item);
//                mPrefUseAutoSave = item.isChecked();
//                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        DialogUtils.showInfoDialog(this, "About Sale Calculator",
                "This app will help you work out how much you really pay!");
    }

    private void showSettings() {
        dismissSnackBarIfShown();
        Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1) {
            restoreOrSetFromPreferences_AllAppAndGameSettings();
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void restoreOrSetFromPreferences_AllAppAndGameSettings() {
        SharedPreferences sp = getDefaultSharedPreferences(this);
        mUseAutoSave = sp.getBoolean(mKEY_AUTO_SAVE, true);
        //mSaleCalc.setWinnerIsLastPlayerToPick(sp.getBoolean(mKEY_WIN_ON_LAST_PICK, false));
    }

    private void showSaleDetails() {
        if (mSaleCalc == null){
            Toast.makeText(getApplicationContext(),
                    R.string.press_calculate,
                    Toast.LENGTH_SHORT).show();
        } else{
            dismissSnackBarIfShown();
            Intent intent = new Intent(getApplicationContext(), SaleDetails1.class);
            intent.putExtra("GAME", mSaleCalc.getJSONFromCurrentGame());
            startActivity(intent);
        }
    }

    private void dismissSnackBarIfShown() {
        if (mSnackBar.isShown()) {
            mSnackBar.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveOrDeleteGameInSharedPrefs();
    }

    private void saveOrDeleteGameInSharedPrefs() {
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = defaultSharedPreferences.edit();

        if(mUseAutoSave && mSaleCalc != null)
            editor.putString(mKEY_GAME, mSaleCalc.getJSONFromCurrentGame());
        else
            editor.remove(mKEY_GAME);

        editor.apply();
    }

    @Override
    protected void onStart() {
        super.onStart();

        restoreFromPreferences_SavedGameIfAutoSaveWasSetOn();
        restoreOrSetFromPreferences_AllAppAndGameSettings();
    }

    private void restoreFromPreferences_SavedGameIfAutoSaveWasSetOn() {
        SharedPreferences defaultSharedPreferences = getDefaultSharedPreferences(this);
        if (defaultSharedPreferences.getBoolean(mKEY_AUTO_SAVE,true)) {
            String gameString = defaultSharedPreferences.getString(mKEY_GAME, null);
            if (gameString!=null) {
                mSaleCalc = SaleCalc.getGameFromJSON(gameString);
                updateUI();
            }
        }
    }

//    private void setupFields() {
//        mKEY_AUTO_SAVE = getString(R.string.auto_save_key);
//        //mKEY_WIN_ON_LAST_PICK = getString(R.string.win_on_last_pick_key);
//    }

    private void updateUI(){
        dismissSnackBarIfShown();
    }

//    private void updateUI() {
//        dismissSnackBarIfShown();
//        mTvStatusBarCurrentPlayer.setText(
//                String.format(Locale.getDefault(), "%s: %d",
//                        getString(R.string.current_player),
//                        mGame.getCurrentPlayerNumber()));
//        mTvStatusBarStonesRemaining.setText
//                (String.format(Locale.getDefault(), "%s: %d",
//                        getString(R.string.stones_remaining_in_pile),
//                        mGame.getStonesRemaining()));
//        try {
//            mImageViewStones.setImageDrawable(
//                    ContextCompat.getDrawable(this, mImages[mGame.getStonesRemaining()]));
//        } catch (ArrayIndexOutOfBoundsException e) {
//            mSnackBar.setText(R.string.error_msg_could_not_update_image);
//            mSnackBar.show();
//        }
//    }

    //added for the settings check boxes...
//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        menu.findItem(R.id.action_toggle_auto_save).setChecked(mPrefUseAutoSave); //added menu option to menu_main
//        return super.onPrepareOptionsMenu(menu);
//    }
    //added menu option to above switch statement
    private void toggleMenuItem(MenuItem item) {
        item.setChecked(!item.isChecked());
    }

    //trying to make the auto save setting do something... right now it just check the box...
    private void restoreAppSettingsFromPrefs() {
        SharedPreferences preferences = getSharedPreferences(mKeyPrefsName, MODE_PRIVATE);

        mPrefUseAutoSave = preferences.getBoolean(mKeyAutoSave, true);
    }
}