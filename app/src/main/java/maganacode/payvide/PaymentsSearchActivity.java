package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.Bind;
import butterknife.ButterKnife;
import maganacode.payvide.Models.UserList;
import maganacode.payvide.adapter.UserAdapter;

public class PaymentsSearchActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    //TAGs
    private static final String TAG = "PaymentsSearchActivity";

    //Firebase Database
    private DatabaseReference mReference;

    //Data
    private List<UserList> mUsers = new ArrayList<>();
    private List<UserList> selectedUsers = new ArrayList<>();

    //Adapter
    private UserAdapter mAdapter;

    //Injections
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.bottom_naviation)
    BottomNavigationView mBottomNavigationView;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerView;
    @Bind(R.id.createButton)
    TextView mCreate;
    @Bind(R.id.group_name_text)
    EditText mGroupName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payments_search);
        ButterKnife.bind(this);
        initializeToolbar();
        initializeBottomBar();

        //RecyclerView + Adapter
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new UserAdapter(mUsers);
        mRecyclerView.setAdapter(mAdapter);

        //Child Listener
        childListeners();

        //NewActivity
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (UserList user : mUsers) {
                    if (user.isSelected()) {
                        selectedUsers.add(user);
                    }

                    //This is for GroupActivity Testing
                    Intent i = new Intent(PaymentsSearchActivity.this, GroupActivity.class);

                    //This is for PaymentOptionsTesting -- Completed
                    //Intent i = new Intent(PaymentsSearchActivity.this, PaymentOptionsActivity.class);

                    //Testing
                    //Intent i = new Intent(PaymentsSearchActivity.this,TestActivity.class);

                    i.putExtra("users", (Serializable) selectedUsers);
                    i.putExtra("groupName", mGroupName.getText().toString());
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_items, menu);
        final MenuItem menuItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setOnQueryTextListener(this);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    //Toolbar
    public void initializeToolbar() {
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
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
                        i = new Intent(PaymentsSearchActivity.this, PaymentsSearchActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_dashboard:
                        i = new Intent(PaymentsSearchActivity.this, DashboardActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_profile:
                        item.setEnabled(true);
                        i = new Intent(PaymentsSearchActivity.this, ProfileActivity.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }

    //ChildListeners
    public void childListeners() {
        mReference = FirebaseDatabase.getInstance().getReference("users");
        mReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                //Clear data before adding new ones.
                UserList user = dataSnapshot.getValue(UserList.class);

                //Removes the current user from the search!
                if (!Objects.equals(user.getUid(), FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                    mUsers.add(user);
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.d(TAG, "onChildChanged: " + dataSnapshot.getKey());

                UserList newUser = dataSnapshot.getValue(UserList.class);
                String userKey = dataSnapshot.getKey();
                int userIndex = mUsers.indexOf(userKey);

                if (userIndex > -1) {
                    mUsers.set(userIndex, newUser);
                } else {
                    Log.w(TAG, "onChildChanged:unknown_child " + userKey);
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    //Auto completion of search
    @Override
    public boolean onQueryTextChange(String query) {
        final List<UserList> filteredModeList = filter(mUsers, query);
        Log.v("App", query + ", " + mUsers.size() + ", " + filteredModeList.size());
        mAdapter.animateTo(filteredModeList);
        mRecyclerView.scrollToPosition(0);
        return true;
    }

    private List<UserList> filter(List<UserList> users, String query) {
        //The text to be entered
        query = query.toLowerCase();

        //New List to append objects..this s always empty!
        ArrayList<UserList> filteredUserList = new ArrayList<>();

        //Go through users in database, find their name, email, and username.
        for (UserList user : users) {
            final String name = user.getName().toLowerCase();
            final String username = user.getUsername().toLowerCase();
            final String email = user.getEmail().toLowerCase();

            //If users in database contains any of these
            //then add them to "filter"
            if (name.contains(query) || username.contains(query) | email.contains(query)) {
                filteredUserList.add(user);
            }
        }
        //Show the filtered list as typed.
        return filteredUserList;
    }
}