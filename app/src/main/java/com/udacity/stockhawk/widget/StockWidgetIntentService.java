package com.udacity.stockhawk.widget;

import android.app.IntentService;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.widget.RemoteViews;

import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.ui.MainActivity;

import org.stringtemplate.v4.ST;

/**
 * Created by jamespink on 13/05/2017.
 */

public class StockWidgetIntentService extends IntentService {

    public static final String[] STOCK_HISTORY_PROJECTION = {
            Contract.Quote.COLUMN_SYMBOL,
            Contract.Quote.COLUMN_PRICE,
            Contract.Quote.COLUMN_ABSOLUTE_CHANGE,
            Contract.Quote.COLUMN_PERCENTAGE_CHANGE,
            Contract.Quote.COLUMN_HISTORY,
    };
    /**
     * We store indicies with the position of each string/COLUMN in the array
     */
    public static final int INDEX_STOCK_SYMBOL = 0;
    public static final int INDEX_STOCK_PRICE = 1;
    public static final int INDEX_STOCK_ABSOLUTE_CHANGE =2;
    public static final int INDEX_STOCK_PRECENTAGE_CHANGE = 3;
    public static final int INDEX_STOCK_HISTORY = 4;

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
        String symbol = "GOOG";
        Uri mUri = Contract.Quote.makeUriForStock(symbol);
        Cursor data = getContentResolver().query(mUri, STOCK_HISTORY_PROJECTION, null, null, null);

        if (data == null) return;
        if (!data.moveToFirst()) {
            data.close();
            return;
        }

        //extract the data from the cursor
        String stockSymbol = data.getString(INDEX_STOCK_SYMBOL);
        float stockPrice = data.getFloat(INDEX_STOCK_PRICE);
        float stockChange = data.getFloat(INDEX_STOCK_ABSOLUTE_CHANGE);
        data.close();


        //loop for each widget linking the views
        for (int appWidgetId : appWidgetIds) {
            int layoutId = R.layout.widget_stock_summary;
            RemoteViews views = new RemoteViews(getPackageName(), layoutId);
            views.setTextViewText(R.id.widget_symbol, "goog");
            views.setTextViewText(R.id.widget_price, String.valueOf(stockPrice));
            views.setTextViewText(R.id.widget_change, String.valueOf(stockChange));

            Intent launchIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, launchIntent, 0);
            views.setOnClickPendingIntent(R.id.widget, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);

        }
    }
}
