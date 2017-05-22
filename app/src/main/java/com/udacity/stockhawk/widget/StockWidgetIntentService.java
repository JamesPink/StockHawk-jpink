package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

import org.stringtemplate.v4.ST;

/**
 * Created by jamespink on 13/05/2017.
 */

public class StockWidgetIntentService extends IntentService {

    private static final String PREFS_NAME
            = "com.udacity.stockhawk.widget.StockWidgetProvider";

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
        //Retreive widget id's
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this,
                StockWidgetProvider.class));

        //get the data
        Context context = getApplicationContext();
        ComponentName name = new ComponentName(context,StockWidgetProvider.class);
        int [] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(name);
        Log.d(Integer.toString(ids[0]), "- ids");

        //Bundle testBundle = Intent
        String widgetId = AppWidgetManager.EXTRA_APPWIDGET_ID;
        Log.d(widgetId, "widgetId");



        String symbol = "GOOG";
        int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        Log.d(String.valueOf(mAppWidgetId), "- AppWidgetID" );
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, 0);

        //TODO implement a way to get it to work with prefix key
        symbol = prefs.getString("stockSymbol", "No name defined");





        Log.d(symbol, "- Symbol");
        Uri mUri = Contract.Quote.makeUriForStock(symbol);
        Log.d(mUri.toString(), "- URI");
        Cursor data;
        data = getContentResolver().query(mUri, STOCK_HISTORY_PROJECTION, null, null, null);
        Log.d(data.toString(), "- DATA");

        if (data == null) {
            Log.d(data.toString(), "data is null");
            return;
        }
        if (!data.moveToFirst()) {
            Log.d(data.toString(), "data is null");
            data.close();
            return;
        }

            //extract the data from the cursor
            String stockSymbol = data.getString(INDEX_STOCK_SYMBOL);
            Log.d(stockSymbol, "symbol:");
            float stockPrice = data.getFloat(INDEX_STOCK_PRICE);
            float stockChange = data.getFloat(INDEX_STOCK_ABSOLUTE_CHANGE);
            data.close();

            //TODO 1. add setting option for the stock
            //TODO 2. fix the +/- symbol in front of the stock.
            //TODO 3. fix the layout - 1 pane would probably be good

            //loop for each widget linking the views
            for (int appWidgetId : appWidgetIds) {
                int layoutId = R.layout.widget_stock_summary;
                RemoteViews views = new RemoteViews(getPackageName(), layoutId);
                views.setTextViewText(R.id.widget_symbol, String.valueOf(stockSymbol));
                views.setTextViewText(R.id.widget_price, "$" + String.valueOf(stockPrice));
                views.setTextViewText(R.id.widget_change, "$" + String.valueOf(stockChange));

                Intent launchIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
                views.setOnClickPendingIntent(R.id.widget, pendingIntent);

                appWidgetManager.updateAppWidget(appWidgetId, views);

            }

    }
}
