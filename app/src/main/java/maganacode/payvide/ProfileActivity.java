package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.bottom_naviation)
    BottomNavigationView mBottomNaviation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        //Bottom Bar
        mBottomNaviation.getMenu().getItem(0).setChecked(false);
        mBottomNaviation.getMenu().getItem(1).setChecked(false);
        mBottomNaviation.getMenu().getItem(2).setChecked(true);
        initializeBottomBar();

    }

    //TODO : Link the Bottom Navigation Bar
    public void initializeBottomBar() {
        mBottomNaviation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i;
                switch (item.getItemId()) {
                    case R.id.action_groups:
                        i = new Intent(ProfileActivity.this, PaymentsSearchActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_dashboard:
                        i = new Intent(ProfileActivity.this, DashboardActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_profile:
                        //Refresh
                        item.setEnabled(true);
                        i = new Intent(ProfileActivity.this, ProfileActivity.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }
}