package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;



import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by jamespink on 13/05/2017.
 */

public class StockWidgetIntentService extends IntentService {

    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;

    private static final String PREFS_NAME
            = "com.udacity.stockhawk.widget.StockWidgetProvider";
    private static final String PREF_PREFIX_KEY = "prefix_";
    private static final String PREF_NOT_FOUND = "No name defined";

    public static final String[] STOCK_HISTORY_PROJECTION = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
    };
    /**
     * We store indicies with the position of each string/COLUMN in the array
     */
    public static final int INDEX_STOCK_SYMBOL = 0;
    public static final int INDEX_STOCK_PRICE = 1;
    public static final int INDEX_STOCK_ABSOLUTE_CHANGE =2;

    public StockWidgetIntentService() {
        super(StockWidgetIntentService.class.getSimpleName());
        }

    @Override
    protected void onHandleIntent(Intent intent) {

        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix(getString(R.string.dollar_plus_symbol));


        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockWidgetProvider.class));

            //loop for each widget linking the views
            for (int appWidgetId : appWidgetIds) {

                //get stock from prefs
                SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);
                String prefKey = PREF_PREFIX_KEY + appWidgetId;
                //Log.d(prefKey, "PREFKEY IN FOR");
                String symbol = prefs.getString(prefKey, PREF_NOT_FOUND);
                //Log.d(symbol, "SYMBOL IN FOR");

                //get the data

                Uri mUri = Contract.Quote.makeUriForStock(symbol);
                Cursor data = getContentResolver().query(mUri, STOCK_HISTORY_PROJECTION, null, null, null);

                if (data == null) return;
                if (!data.moveToFirst()) {
                    data.close();
                    return;
                }

                //extract the data from the cursor
                String stockSymbol = data.getString(INDEX_STOCK_SYMBOL);
                //Log.d(stockSymbol, "symbol:");
                float stockPrice = data.getFloat(INDEX_STOCK_PRICE);
                float stockChange = data.getFloat(INDEX_STOCK_ABSOLUTE_CHANGE);
                data.close();

                //Log.d(String.valueOf(appWidgetId), "appWidgetId within for loop");
                int layoutId = R.layout.widget_stock_summary;
                RemoteViews views = new RemoteViews(getPackageName(), layoutId);
                views.setTextViewText(R.id.widget_symbol, String.valueOf(stockSymbol));
                views.setContentDescription(R.id.widget_symbol, getString(R.string.stock_history_detail_btns)+stockSymbol);
                //views.setContentDescription(R.id.widget_price, getString(R.string.stock_history_detail_btns)+stockSymbol);
                //views.setContentDescription(R.id.widget_change, getString(R.string.stock_history_detail_btns)+stockSymbol);
                if (stockChange > 0) {
                    views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_green);
                } else {
                    views.setInt(R.id.widget_change, "setBackgroundResource", R.drawable.percent_change_pill_red);
                }

                //format our doubles nicely
                String change = dollarFormatWithPlus.format(stockChange);
                String dollarPrice = dollarFormat.format(stockPrice);
                views.setTextViewText(R.id.widget_price, dollarPrice);
                views.setTextViewText(R.id.widget_change, change);

                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.widget, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);


            }

    }
}
