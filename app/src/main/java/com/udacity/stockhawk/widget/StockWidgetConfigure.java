package com.udacity.stockhawk.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.PrefUtils;
import com.udacity.stockhawk.sync.QuoteSyncJob;
import com.udacity.stockhawk.ui.MainActivity;

import java.util.ArrayList;

public class StockWidgetConfigure extends AppCompatActivity {
    static final String TAG = "StockWidgetConfigure";
    private static final String PREFS_NAME
            = "com.udacity.stockhawk.widget.StockWidgetProvider";
    private static final String PREF_PREFIX_KEY = "prefix_";
    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    EditText mAppWidgetPrefix;

    public StockWidgetConfigure() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setResult(RESULT_CANCELED);

        setContentView(R.layout.activity_stock_widget_configure);

        mAppWidgetPrefix = (EditText)findViewById(R.id.et_widget_stock_setting);

        findViewById(R.id.btn_save_stock).setOnClickListener(mOnClickListener);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }

        //mAppWidgetPrefix.setText(loadTitlePref(StockWidgetConfigure.this, mAppWidgetId));
    }

    //TODO - WE COULD STORE THE ENTERED VARIABLE IN A PUBLIC STRING THAT COULD BE USED BY THE INTENT SERVICE
    //TODO - ADD SOME DATA VALIDATION TO THE CONFIG ACTIVITY TO PREVENT INCORRECT SYMBOLS BEING ADDED AND MAYBE ALSO CHECK STOCK ALREADY EXISTS IN DB
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            final Context context = StockWidgetConfigure.this;
            // When the button is clicked, save the string in our prefs and return that they
            // clicked OK.

            String titlePrefix = mAppWidgetPrefix.getText().toString();
            saveTitlePref(context, mAppWidgetId, titlePrefix);

            if (titlePrefix.length() != 4 || titlePrefix.matches(".*\\d+.") || titlePrefix == null || titlePrefix.isEmpty()) {
                showError();
            }

            // Push widget update to surface with newly set prefix
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            //StockWidgetProvider.on(context, appWidgetManager,
                    //mAppWidgetId, titlePrefix);
            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            resultValue.putExtra("titlePrefix", titlePrefix);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };
    // Write the prefix to the SharedPreferences object for this widget
    static void saveTitlePref(Context context, int appWidgetId, String text) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        String key = PREF_PREFIX_KEY + appWidgetId;
        prefs.putString(key, text);
        //Log.d(key, "- PREFIX KEY");
        prefs.commit();
    }
    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String loadTitlePref(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String prefix = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null);
        if (prefix != null) {
            return prefix;
        } else {
            return context.getString(R.string.action_change_units);
        }
    }
    static void deleteTitlePref(Context context, int appWidgetId) {
    }
    static void loadAllTitlePrefs(Context context, ArrayList<Integer> appWidgetIds,
                                  ArrayList<String> texts) {
    }


        public void showError() {
            String message = getString(R.string.toast_no_input);
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        }

}
