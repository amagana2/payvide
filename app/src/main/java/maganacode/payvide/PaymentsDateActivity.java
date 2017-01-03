package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.codetroopers.betterpickers.calendardatepicker.CalendarDatePickerDialogFragment;
import com.codetroopers.betterpickers.calendardatepicker.MonthAdapter;

import org.joda.time.DateTime;

import butterknife.Bind;
import butterknife.ButterKnife;


public class PaymentsDateActivity extends AppCompatActivity implements CalendarDatePickerDialogFragment.OnDateSetListener {
    //TAG
    private static String TAG = "Calendar";
    private static String date_key = "ChosenDate";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.result_view)
    TextView mResultView;
    @Bind(R.id.next_view)
    TextView mNextView;
    @Bind(R.id.fab)
    FloatingActionButton mFab;
    @Bind(R.id.date_pick_text)
    TextView mDatePickText;
    @Bind(R.id.bottom_naviation)
    BottomNavigationView mBottomNavigationView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_date);
        ButterKnife.bind(this);
        initializeToolbar();
        initializeBottomBar();

        mResultView = (TextView) findViewById(R.id.result_view);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateTime now = new DateTime();
                MonthAdapter.CalendarDay minDate = new MonthAdapter.CalendarDay(now.getYear() - 1, now.getMonthOfYear(), now.getDayOfMonth());
                CalendarDatePickerDialogFragment cdp = new CalendarDatePickerDialogFragment()
                        .setDateRange(minDate, null)
                        .setOnDateSetListener(PaymentsDateActivity.this);
                cdp.show(getSupportFragmentManager(), TAG);
            }
        });

        mNextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(PaymentsDateActivity.this, PaymentsSearchActivity.class);
                startActivity(i);
            }
        });

    }

    /**
     * Preconditions: User has intentions of choosing a specific date, unless cancelled.
     * Postconditions: ImageButton changes to "Update Image",
     * The parameters become the new textView.
     *
     * @param dialog      - Fragment
     * @param yearPicked  - Year
     * @param monthOfYear - Month
     * @param dayOfMonth  - Day
     */
    @Override
    public void onDateSet(CalendarDatePickerDialogFragment dialog, int yearPicked, int monthOfYear, int dayOfMonth) {
        //The picked month, year, and day.
        String month = monthDate(monthOfYear);
        String year = Integer.toString(yearPicked);
        String day = Integer.toString(dayOfMonth);

        //TODO: Write Date Object to Firebase Database

        //Display the chosen date.
        mResultView.setText(month + " " + day + ", " + year);
        mFab.setImageResource(R.drawable.ic_update);
    }

    private void initializeToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setTitle(""); //Takes brand name off.

        //Support for older versions of Android.
        setSupportActionBar(toolbar);

        //Menu Navigation Button
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: Add drawer view to add listview.
                Intent i = (new Intent(PaymentsDateActivity.this, DashboardActivity.class));
                startActivity(i);
            }
        });

    }

    //TODO : Link the Bottom Navigation Bar
    public void initializeBottomBar() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i;
                switch (item.getItemId()) {
                    case R.id.action_groups:
                        //Refresh
                        i = new Intent(PaymentsDateActivity.this, PaymentsSearchActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_dashboard:
                        i = new Intent(PaymentsDateActivity.this, DashboardActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_profile:
                        item.setEnabled(true);
                        i = new Intent(PaymentsDateActivity.this, ProfileActivity.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }

    public String monthDate(int month) {
        String monthConverted = "";
        switch (month) {
            case 0:
                monthConverted = "January";
                break;
            case 1:
                monthConverted = "February";
                break;
            case 2:
                monthConverted = "March";
                break;
            case 3:
                monthConverted = "April";
                break;
            case 4:
                monthConverted = "May";
                break;
            case 5:
                monthConverted = "June";
                break;
            case 6:
                monthConverted = "July";
                break;
            case 7:
                monthConverted = "August";
                break;
            case 8:
                monthConverted = "September";
                break;
            case 9:
                monthConverted = "October";
                break;
            case 10:
                monthConverted = "November";
                break;
            case 11:
                monthConverted = "December";
                break;
        }

        return monthConverted;
    }
}