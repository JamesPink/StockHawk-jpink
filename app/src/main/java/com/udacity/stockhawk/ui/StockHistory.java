package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;
import com.udacity.stockhawk.databinding.ActivityStockHistoryBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StockHistory extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /*
collumns of data we are interesting in displaying in StockHistory,
i have delcared all columns during the design stage whiile we decide
what to display and what not to. Upon completion we can remove any that
are unused.
 */
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

    //ID for our loader
    private static final int ID_STOCK_HISTORY_LOADER = 353;

    //uri used to access the stock history
    private Uri mUri;

    //activity for data binding so we avoid the need for findviewbyid
    private ActivityStockHistoryBinding mDetailBinding;

    /**
     *in onCreate we set the data binder, get the data from the intent
     * and call the loader
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_stock_history);

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        /* This connects our Activity into the loader lifecycle. */
        getSupportLoaderManager().initLoader(ID_STOCK_HISTORY_LOADER, null, this);
    }

    /**
     * creates and returns the CursorLoader
     * @param id
     * @param args
     * @return a new loader instance for loading the stock data
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        switch (id) {

            case ID_STOCK_HISTORY_LOADER:
                return new CursorLoader(this,
                        mUri,
                        STOCK_HISTORY_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException(("Loader not  implemented: " + id));
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //checks if the cursor is valid before proceeding to bind the data
        boolean cursoHasValidData = false;

        if (data != null && data.moveToFirst())
            cursoHasValidData = true;

        if(!cursoHasValidData) {
            return;
        }

        //extract data from the cursor
        String symbol = data.getString(INDEX_STOCK_SYMBOL);
        double price = data.getDouble(INDEX_STOCK_PRICE);
        double absoluteChange = data.getDouble(INDEX_STOCK_ABSOLUTE_CHANGE);
        double percentChange = data.getDouble(INDEX_STOCK_PRECENTAGE_CHANGE);
        String history = data.getString(INDEX_STOCK_HISTORY);

        //convert history into something that is useable in the linechart
        List<String> stockHistoryArrayList  = Arrays.asList(history.split("\\r?\\n"));
        Collections.reverse(stockHistoryArrayList);

        //set up the line chart
        LineChart stockGraph = (LineChart) mDetailBinding.lcStockChart;
        ArrayList<Entry> entries = new ArrayList<Entry>();
        ArrayList<String> dateLabels = new ArrayList<String>();

        //iterate through stockHistoryArrayList and parse data required for use in the linechart
        float index = 0;
        for(String stockPoint : stockHistoryArrayList) {
            String[] thisStock = stockPoint.split(",");

            entries.add(new Entry(index, Float.valueOf(thisStock[1])));

            dateLabels.add(new String(thisStock[0]));

            index++;
        }

        //set the data variables for the linechart
        LineDataSet dataSet = new LineDataSet(entries, symbol);
        LineData lineData = new LineData(dataSet);
        stockGraph.setData(lineData);







    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
