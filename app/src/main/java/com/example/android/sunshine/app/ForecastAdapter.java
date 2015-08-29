package com.example.android.sunshine.app;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.android.sunshine.app.data.WeatherContract;

/**
 * Created by FM on 6/27/2015.
 */

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 *
 * from a {@link Cursor} to a {@link android.widget.ListView}.
 * from a {@link android.database.Cursor} to a {@link android.support.v7.widget.RecyclerView}.
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {
    //here a two type of view
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_FUTURE_DAY = 1;
    private static final int VIEW_TYPE_COUNT = 2;

    // Flag to determine if we want to use a separate view for "today".
    private boolean mUseTodayLayout = true;
    private Cursor mCursor;
    final private Context mContext;
    final private ForecastAdapterOnClickHandler mClickHandler;
    final private View mEmptyView;
    final private ItemChoiceManager mICM;
    public void setUseTodayLayout(boolean useTodayLayout) {
        mUseTodayLayout = useTodayLayout;
    }

    public static interface ForecastAdapterOnClickHandler {
        void onClick(Long date, ForecastAdapterViewHolder vh);} ///????
    public ForecastAdapter(Context context, ForecastAdapterOnClickHandler dh, View emptyView, int choiceMode) {
        mContext = context;
        mClickHandler = dh;
        mEmptyView = emptyView;
        mICM = new ItemChoiceManager(this);
        mICM.setChoiceMode(choiceMode);
    }

    @Override
    public int getItemViewType(int position) {
        //apply VIEW_TYPE_TODAY for the position 1
        //return position == 0 ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
        return (position == 0 && mUseTodayLayout) ? VIEW_TYPE_TODAY : VIEW_TYPE_FUTURE_DAY;
    }
    /**
     * Cache of the children views for a forecast list item.
     */
    public class ForecastAdapterViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mIconView;
        public final TextView mDateView;
        public final TextView mDescriptionView;
        public final TextView mHighTempView;
        public final TextView mLowTempView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            mIconView = (ImageView) view.findViewById(R.id.list_item_icon);
            mDateView = (TextView) view.findViewById(R.id.list_item_date_textview);
            mDescriptionView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
            mHighTempView = (TextView) view.findViewById(R.id.list_item_high_textview);
            mLowTempView = (TextView) view.findViewById(R.id.list_item_low_textview);
            view.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            //get the index of this column
            int dateColumnIndex = mCursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
            //Important This going to send the date into mClickHandler then it will be deliver to constructor
            mClickHandler.onClick(mCursor.getLong(dateColumnIndex), this);
            mICM.onClick(this);
        }
    }

    @Override
    public int getItemCount() {
        if ( null == mCursor ) return 0;
        return mCursor.getCount();
    }
    public void swapCursor(Cursor newCursor) {
               mCursor = newCursor;
                notifyDataSetChanged();
                mEmptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }

                public Cursor getCursor() {
                return mCursor;
    }

     /*
        This takes advantage of the fact that the viewGroup passed to onCreateViewHolder is the
        RecyclerView that will be used to contain the view, so that it can get the current
        ItemSelectionManager from the view.
        One could implement this pattern without modifying RecyclerView by taking advantage
        of the view tag to store the ItemChoiceManager.

     */

    /**
     * create a new RecyclerView.ViewHolder and initializes some private fields to be used by RecyclerView.
     * @param viewGroup parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     *
     * The new ViewHolder will be used to display items of the adapter using onBindViewHolder(ViewHolder, int, List).
     * Since it will be re-used to display different items in the data set,
     * it is a good idea to cache references to sub views of the View to avoid unnecessary findViewById(int) calls.
     */
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if ( viewGroup instanceof RecyclerView ) {
            int layoutId = -1;
            switch (viewType) {
                case VIEW_TYPE_TODAY: {
                    layoutId = R.layout.list_item_forecast_today;
                    break;
                }
                case VIEW_TYPE_FUTURE_DAY: {
                    layoutId = R.layout.list_item_forecast;
                    break;
                }
            }
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
            view.setFocusable(true);
            return new ForecastAdapterViewHolder(view);
        } else {
            throw new RuntimeException("Not bound to RecyclerView");
        }
    }
    /**
     * Prepare the weather high/lows for presentation.
     */
    /*
    private String formatHighLows(double high, double low) {
        boolean isMetric = Utility.isMetric(mContext);
        String highLowStr = Utility.formatTemperature(high, isMetric) + "/" + Utility.formatTemperature(low, isMetric);
        return highLowStr;
    }

    /*
        This is ported from FetchWeatherTask --- but now we go straight from the cursor to the
        string.
     */
    /*
    private String convertCursorRowToUXFormat(Cursor cursor) {


        // get row indices for our cursor
        /* int idx_max_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MAX_TEMP);
        int idx_min_temp = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_MIN_TEMP);
        int idx_date = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_DATE);
        int idx_short_desc = cursor.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC);*/

/*
        String highAndLow = formatHighLows(
                cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP),
                cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP));
        //cursor.getDouble(idx_max_temp),
        //cursor.getDouble(idx_min_temp));
        return Utility.formatDate(cursor.getLong(ForecastFragment.COL_WEATHER_DATE)) +
                " - " + cursor.getString(ForecastFragment.COL_WEATHER_DESC) +
                " - " + highAndLow;

    }




    /*
        This is where we fill-in the views with the contents of the cursor.
     */
    //old bindView has been replaced after use the Viewholder
    /*
    @Override
    public void bindView(View view, Context context, Cursor cursor) {


        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(ForecastFragment.COL_WEATHER_ID);
        // Use placeholder image for now
        ImageView iconView = (ImageView) view.findViewById(R.id.list_item_icon);
        iconView.setImageResource(R.drawable.ic_launcher);

        // Read date from cursor
        long dateInMillis = cursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        TextView dateView = (TextView) view.findViewById(R.id.list_item_date_textview);
        dateView.setText(Utility.getFriendlyDayString(context, dateInMillis));

        // Read weather forecast from cursor
        String description = cursor.getString(ForecastFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it
        TextView descriptionView = (TextView) view.findViewById(R.id.list_item_forecast);
        descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        double high = cursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        TextView highView = (TextView) view.findViewById(R.id.list_item_high_textview);
        highView.setText(Utility.formatTemperature(high, isMetric));

        // Read low temperature from cursor
        double low = cursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        TextView lowView = (TextView) view.findViewById(R.id.list_item_low_textview);
        lowView.setText(Utility.formatTemperature(low, isMetric));
    }
    */

    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        /*
        * Returns this view's tag.
        *Returns
        *the Object stored in this view as a tag, or null if not set
        */



        // Use placeholder image for now
        //viewHolder.iconView.setImageResource(R.drawable.ic_launcher);
        mCursor.moveToPosition(position);


        int defaultImage;
        int weatherId = mCursor.getInt(ForecastFragment.COL_WEATHER_CONDITION_ID);

        switch (getItemViewType(position)) { //Return the view type of the item at position for the purposes of view recycling.
            case VIEW_TYPE_TODAY:
                defaultImage = Utility.getArtResourceForWeatherCondition(weatherId);
                break;
            default:
                defaultImage = Utility.getIconResourceForWeatherCondition(weatherId);
        }
        if (Utility.usingLocalGraphics(mContext)) {
            forecastAdapterViewHolder.mIconView.setImageResource(defaultImage);
        } else {
            Glide.with(mContext)
                    .load(Utility.getArtUrlForWeatherCondition(mContext, weatherId))
                    .error(defaultImage)
                    .crossFade()
                    .into(forecastAdapterViewHolder.mIconView);
        }

        // Read date from cursor
        long dateInMillis = mCursor.getLong(ForecastFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it
        forecastAdapterViewHolder.mDateView.setText(Utility.getFriendlyDayString(mContext, dateInMillis));

        // Read weather forecast from cursor
        String description = Utility.getStringForWeatherCondition(mContext, weatherId);
        // Find TextView and set weather forecast on it
        forecastAdapterViewHolder.mDescriptionView.setText(description);
        // For accessibility, add a content description to the icon field
        forecastAdapterViewHolder.mIconView.setContentDescription(mContext.getString(R.string.a11y_forecast, description));

        // Read high temperature from cursor
        double high = mCursor.getDouble(ForecastFragment.COL_WEATHER_MAX_TEMP);
        String highString = Utility.formatTemperature(mContext, high);
        forecastAdapterViewHolder.mHighTempView.setText(highString);
        forecastAdapterViewHolder.mHighTempView.setContentDescription(mContext.getString(R.string.a11y_high_temp, highString));

        // Read low temperature from cursor
        double low = mCursor.getDouble(ForecastFragment.COL_WEATHER_MIN_TEMP);
        String lowString = Utility.formatTemperature(mContext, low);
        forecastAdapterViewHolder.mLowTempView.setText(lowString);
        forecastAdapterViewHolder.mLowTempView.setContentDescription(mContext.getString(R.string.a11y_low_temp, lowString));
        mICM.onBindViewHolder(forecastAdapterViewHolder, position);
    }
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        mICM.onRestoreInstanceState(savedInstanceState);
    }
    public void onSaveInstanceState(Bundle outState) {
        mICM.onSaveInstanceState(outState);
    }
    public int getSelectedItemPosition() {
        return mICM.getSelectedItemPosition();
    }
    public void selectView(RecyclerView.ViewHolder viewHolder) {
        if ( viewHolder instanceof ForecastAdapterViewHolder ) {
            ForecastAdapterViewHolder vfh = (ForecastAdapterViewHolder)viewHolder;
            vfh.onClick(vfh.itemView);
        }
    }

}
