package com.udacity.stockhawk.widget;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.udacity.stockhawk.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.udacity.stockhawk.data.Contract.*;

/**
 * Created by jamespink on 23/05/2017.
 */

public class CollectionWidgetDataProvider implements RemoteViewsService.RemoteViewsFactory {

    List<String> mCollection = new ArrayList<>();
    Cursor data = null;
    Context mContext;
    Intent mIntent;

    private DecimalFormat dollarFormatWithPlus;
    private DecimalFormat dollarFormat;

    public static final String[] STOCK_HISTORY_PROJECTION = {
            Quote.COLUMN_SYMBOL,
            Quote.COLUMN_PRICE,
            Quote.COLUMN_ABSOLUTE_CHANGE,
    };
    /**
     * We store indicies with the position of each string/COLUMN in the array
     */
    public static final int INDEX_STOCK_SYMBOL = 0;
    public static final int INDEX_STOCK_PRICE = 1;
    public static final int INDEX_STOCK_ABSOLUTE_CHANGE =2;


    private void initData() {
        data = null;
        data = mContext.getContentResolver().query(Quote.URI, STOCK_HISTORY_PROJECTION, null, null, null);
        dollarFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus = (DecimalFormat) NumberFormat.getCurrencyInstance(Locale.US);
        dollarFormatWithPlus.setPositivePrefix(mContext.getString(R.string.dollar_plus_symbol));
    }

    public CollectionWidgetDataProvider(Context context, Intent intent) {
        this.mContext = context;
        this.mIntent = intent;
    }

    @Override
    public void onCreate() {
        initData();
    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        data.moveToPosition(position);
        String symbol = data.getString(INDEX_STOCK_SYMBOL);
        float stockPrice = data.getFloat(INDEX_STOCK_PRICE);
        String price = dollarFormat.format(stockPrice);
        float stockChange = data.getFloat(INDEX_STOCK_ABSOLUTE_CHANGE);
        String change = dollarFormatWithPlus.format(stockChange);



        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_quote);
        remoteViews.setTextViewText(R.id.symbol, symbol);
        remoteViews.setTextViewText(R.id.price, price);
        remoteViews.setTextViewText(R.id.change, change);
        remoteViews.setTextColor(R.id.symbol, Color.BLACK);
        remoteViews.setTextColor(R.id.price, Color.BLACK);
        if (stockChange > 0) {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_green);
        } else {
            remoteViews.setInt(R.id.change, "setBackgroundResource", R.drawable.percent_change_pill_red);
        }
        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }


}
