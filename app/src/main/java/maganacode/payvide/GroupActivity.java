package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import maganacode.payvide.Models.Group;
import maganacode.payvide.Models.GroupMembers;
import maganacode.payvide.Models.UserList;
import maganacode.payvide.adapter.GroupActivityAdapter;

public class GroupActivity extends AppCompatActivity {
    //Tag
    private static String TAG = "GroupActivity";

    //Firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference();

    //Butterknife Injections
    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.group_text_view)
    TextView mGroupTextView;
    @Bind(R.id.group_list_icon)
    ImageButton mGroupListIcon;
    @Bind(R.id.add_group_icon)
    ImageButton mAddGroupIcon;
    @Bind(R.id.linear_group_items)
    LinearLayout mLinearGroupItems;
    @Bind(R.id.recyclerview)
    RecyclerView mRecyclerview;
    @Bind(R.id.bottom_naviation)
    BottomNavigationView mBottomNavigationView;
    @Bind(R.id.activity_groups)
    RelativeLayout mActivityGroups;
    @Bind(R.id.add_group)
    TextView mAddButton;

    //Data Conversion
    private String email;
    private String name;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        initializeBottomBar();

        //Users from selected list -- NEW DATA
        List<UserList> groupMembers = (List<UserList>) getIntent().getSerializableExtra("users");
        String groupName = getIntent().getStringExtra("groupName");

        //Convert UserList -> GroupMembers
        List<GroupMembers> members = new ArrayList<>(); //List of Group Members
        GroupMembers member = new GroupMembers(); //Individual Group Members
        for (UserList users : groupMembers) {
            name = users.getName();
            email = users.getEmail();
            username = users.getUsername();

            member.setName(name);
            member.setEmail(email);
            member.setUsername(username);
            members.add(member); //List<GroupMembers> now has however members were selected.
        }

        //Create Group!
        Group group = new Group(members, groupName, mDatabase.child("payvide").getKey());
        List<Group> groups = new ArrayList<>();
        groups.add(group);

        //RecyclerView
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        GroupActivityAdapter mAdapter = new GroupActivityAdapter(groups);
        mRecyclerview.setAdapter(mAdapter);
        onGroupCreate(members, groupName);

        //Add new group intent
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupActivity.this, PaymentsSearchActivity.class);
                startActivity(i);
            }
        });
    }

    private void onGroupCreate(List<GroupMembers> groupMembers, String groupName) {
        List<GroupMembers> allMembers = new ArrayList<>();
        GroupMembers groupMemb = new GroupMembers();
        for (GroupMembers member : groupMembers) {
            name = member.getName();
            email = member.getEmail();
            username = member.getUsername();

            //Add all members
            allMembers.add(groupMemb);
        }

        //Unique ID
        String id = mDatabase.child("payvide").getKey();
        //Creates new group with name!
        writeNewGroup(allMembers, groupName, id);
    }

    private void writeNewGroup(List<GroupMembers> members, String name, String uid) {
        String key = mDatabase.child("payvide").getKey();
        //TODO: Create Groups
        Group newGroup = new Group(members, name, uid);

        Map<String, Object> groupValues = newGroup.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(key, groupValues);
        mDatabase.updateChildren(childUpdates);
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
                        i = new Intent(GroupActivity.this, PaymentsSearchActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_dashboard:
                        i = new Intent(GroupActivity.this, DashboardActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_profile:
                        item.setEnabled(true);
                        i = new Intent(GroupActivity.this, ProfileActivity.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }
}
