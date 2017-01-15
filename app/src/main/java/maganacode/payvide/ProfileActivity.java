package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.Bind;
import butterknife.ButterKnife;
import maganacode.payvide.Models.UserList;

public class ProfileActivity extends AppCompatActivity {

    @Bind(R.id.bottom_naviation)
    BottomNavigationView mBottomNaviation;
    @Bind(R.id.app_bar)
    AppBarLayout mAppBar;
    @Bind(R.id.email_text)
    TextView mEmailText;
    @Bind(R.id.Username_text)
    TextView mUsernameText;
    @Bind(R.id.name_text)
    TextView mNameText;

    //Declarations
    String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        initializeBottomBar();

        //Current User Information
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("users").child(currentUserID);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserList user = dataSnapshot.getValue(UserList.class);

                mNameText.setText(user.getName());
                mEmailText.setText(user.getEmail());
                mUsernameText.setText(user.getUsername());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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