package com.morristaedt.mirror;

import android.content.Intent;
import android.graphics.ColorFilter;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.morristaedt.mirror.modules.BirthdayModule;
import com.morristaedt.mirror.modules.ChoresModule;
import com.morristaedt.mirror.modules.DayModule;
import com.morristaedt.mirror.modules.CalendarModule;
import com.morristaedt.mirror.modules.ForecastModule;
import com.morristaedt.mirror.modules.XKCDModule;
import com.morristaedt.mirror.modules.YahooFinanceModule;
import com.morristaedt.mirror.requests.YahooStockResponse;
import com.morristaedt.mirror.utils.WeekUtil;
import com.squareup.picasso.Picasso;

public class MirrorActivity extends ActionBarActivity
{

    private static final boolean DEMO_MODE = false;

    private TextView mBirthdayText;
    private TextView mDayText;
    private TextView mWeatherSummary;
    private TextView mHelloText;
    private TextView mBikeTodayText;
    private TextView mStockText;
    private View mWaterPlants;
    private View mLibraryDay;
    private View mGroceryList;
    private ImageView mXKCDImage;
    private TextView mCalendarTitleText;
    private TextView mCalendarDetailsText;

    private XKCDModule.XKCDListener mXKCDListener = new XKCDModule.XKCDListener()
    {
        @Override
        public void onNewXKCDToday(String url)
        {
            if (TextUtils.isEmpty(url))
            {
                mXKCDImage.setVisibility(View.GONE);
            }
            else
            {
                Picasso.with(MirrorActivity.this).load(url).into(mXKCDImage);
                mXKCDImage.setVisibility(View.VISIBLE);
            }
        }
    };

    private CalendarModule.CalendarListener mCalendarListener = new CalendarModule.CalendarListener() {
               @Override
               public void onCalendarUpdate(String title, String details) {
                       mCalendarTitleText.setVisibility(title != null ? View.VISIBLE : View.GONE);
                       mCalendarTitleText.setText(title);
                       mCalendarDetailsText.setVisibility(details != null ? View.VISIBLE : View.GONE);
                       mCalendarDetailsText.setText(details);

                               //Make marquee effect work for long text
                                       mCalendarTitleText.setSelected(true);
                       mCalendarDetailsText.setSelected(true);
                   }
           };

    private YahooFinanceModule.StockListener mStockListener = new YahooFinanceModule.StockListener()
    {
        @Override
        public void onNewStockPrice(YahooStockResponse.YahooQuoteResponse quoteResponse)
        {
            if (quoteResponse == null)
            {
                mStockText.setVisibility(View.GONE);
            }
            else
            {
                mStockText.setVisibility(View.VISIBLE);
                mStockText.setText("$" + quoteResponse.symbol + " $" + quoteResponse.LastTradePriceOnly);
            }
        }
    };

    private ForecastModule.ForecastListener mForecastListener = new ForecastModule.ForecastListener()
    {
        @Override
        public void onWeatherToday(String weatherToday)
        {
            if (!TextUtils.isEmpty(weatherToday))
            {
                mWeatherSummary.setVisibility(View.VISIBLE);
                mWeatherSummary.setText(weatherToday);
            }
        }

        @Override
        public void onShouldBike(boolean showToday, boolean shouldBike)
        {
            mBikeTodayText.setVisibility(showToday ? View.VISIBLE : View.GONE);
            mBikeTodayText.setText(shouldBike ? R.string.bike_today : R.string.no_bike_today);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mBirthdayText = (TextView) findViewById(R.id.birthday_text);
        mDayText = (TextView) findViewById(R.id.day_text);
        mWeatherSummary = (TextView) findViewById(R.id.weather_summary);
        mHelloText = (TextView) findViewById(R.id.hello_text);
        mWaterPlants = findViewById(R.id.water_plants);
        mGroceryList = findViewById(R.id.grocery_list);
        mLibraryDay = findViewById(R.id.library_day);
        mBikeTodayText = (TextView) findViewById(R.id.can_bike);
        mStockText = (TextView) findViewById(R.id.stock_text);
        mXKCDImage = (ImageView) findViewById(R.id.xkcd_image);
        mCalendarTitleText = (TextView) findViewById(R.id.calendar_title);
        mCalendarDetailsText = (TextView) findViewById(R.id.calendar_details);

        //Negative of XKCD image
        float[] colorMatrixNegative = {
                -1.0f, 0, 0, 0, 255, //red
                0, -1.0f, 0, 0, 255, //green
                0, 0, -1.0f, 0, 255, //blue
                0, 0, 0, 1.0f, 0 //alpha
        };
        ColorFilter colorFilterNegative = new ColorMatrixColorFilter(colorMatrixNegative);
//        mXKCDImage.setColorFilter(colorFilterNegative); // not inverting for now

        setViewState();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);
        setViewState();
    }

    private void setViewState()
    {
        String birthday = BirthdayModule.getBirthday();
        if (TextUtils.isEmpty(birthday))
        {
            mBirthdayText.setVisibility(View.GONE);
        }
        else
        {
            mBirthdayText.setVisibility(View.VISIBLE);
            mBirthdayText.setText(getString(R.string.happy_birthday, birthday));
        }

        mDayText.setText(DayModule.getDay());
//        mHelloText.setText(TimeModule.getTimeOfDayWelcome(getResources())); // not in current design

//        mWaterPlants.setVisibility(ChoresModule.waterPlantsToday() ? View.VISIBLE : View.GONE);
        mWaterPlants.setVisibility(View.GONE);
        mLibraryDay.setVisibility((ChoresModule.libraryToday()? View.VISIBLE : View.GONE));
        mGroceryList.setVisibility(ChoresModule.marketToday() ? View.VISIBLE : View.GONE);

        // TODO Move lat/long to query OS
        ForecastModule.getHourlyForecast(getResources(), 32.858805, -117.198556, mForecastListener);
        XKCDModule.getXKCDForToday(mXKCDListener);
        CalendarModule.getCalendarEvents(this, mCalendarListener);


        if (WeekUtil.isWeekday() && WeekUtil.afterFive())
        {
            YahooFinanceModule.getStockForToday(getString(R.string.StockSymbol), mStockListener);
        }
        else
        {
            mStockText.setVisibility(View.GONE);
        }
    }

    private void showDemoMode()
    {
        if (DEMO_MODE)
        {
            mBikeTodayText.setVisibility(View.VISIBLE);
            mStockText.setVisibility(View.VISIBLE);
            mWaterPlants.setVisibility(View.VISIBLE);
            mGroceryList.setVisibility(View.VISIBLE);
        }
    }
}
