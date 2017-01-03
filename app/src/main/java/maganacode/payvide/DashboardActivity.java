package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardActivity extends AppCompatActivity {


    @Bind(R.id.dashButton)
    Button mDashButton;
    @Bind(R.id.activity_dashboard)
    RelativeLayout mActivityDashboard;
    @Bind(R.id.bottom_naviation)
    BottomNavigationView mBottomNavigationView;
    @Bind(R.id.color_test_button)
    Button mColorTest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        //Bottom Bar
        mBottomNavigationView.getMenu().getItem(0).setChecked(false);
        mBottomNavigationView.getMenu().getItem(1).setChecked(true);
        initializeBottomBar();


        mDashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, PaymentsDateActivity.class);
                startActivity(i);
            }
        });

        mColorTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(DashboardActivity.this, PaymentOptionsActivity.class);
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
                        i = new Intent(DashboardActivity.this, PaymentsSearchActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_dashboard:
                        i = new Intent(DashboardActivity.this, DashboardActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_profile:
                        i = new Intent(DashboardActivity.this, ProfileActivity.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }
}
