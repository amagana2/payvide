package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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
import maganacode.payvide.adapter.RecyclerTouchListener;

public class GroupActivity extends AppCompatActivity {
    //Tag
    private static String TAG = "GroupActivity";

    //Handler
    private Handler mHandler;

    //Firebase
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase = database.getReference("users").child("groups");//Users -> Groups
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users");
    private String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference newRef = mRef.child(currentUserID);
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private String groupID = ref.child(currentUserID).child("groups").push().getKey();

    //Data Conversion
    private String email;
    private String name;
    private String username;
    private String ID;
    private String groupName;
    private List<GroupMembers> members = new ArrayList<>(); //List of Group Members
    private List<UserList> selectedUsers = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        initializeBottomBar();

        //Users from selected list -- NEW DATA
        selectedUsers = (List<UserList>) getIntent().getSerializableExtra("users");
        groupName = getIntent().getStringExtra("groupName");

        //Create Group from GroupMembers
        Group group = new Group(members, groupName, mDatabase.child("payvide").getKey());
        groups.add(group);

        //Convert UserList -> GroupMembers
        for (UserList users : selectedUsers) {
            //Grab their information
            name = users.getName();
            email = users.getEmail();
            username = users.getUsername();
            ID = users.getUid();

            //GroupMember Object.
            GroupMembers member = new GroupMembers(); //Individual Group Members

            //Write the member.
            member.setName(name);
            member.setEmail(email);
            member.setUsername(username);
            member.setUid(ID);

            //Add to the List of GroupMembers
            members.add(member); //List<GroupMembers> now has however members were selected.
        }

        //Todo : Write current user + admin to Firebase.
        writeNewGroup(members, groupName, groupID);

        //Current User
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Name: " + dataSnapshot.child("name").getValue());

                GroupMembers mCurrent = new GroupMembers();

                //Write the member.
                mCurrent.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                mCurrent.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                mCurrent.setUsername(String.valueOf(dataSnapshot.child("username").getValue()));
                mCurrent.setUid(currentUserID);

                members.add(mCurrent);

                //Unique ID
                String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //RecyclerView
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(GroupActivity.this));
        GroupActivityAdapter mAdapter = new GroupActivityAdapter(groups);
        mRecyclerview.setAdapter(mAdapter);


        //OnClick RecyclerView
        mRecyclerview.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(),
                mRecyclerview, new RecyclerTouchListener.OnItemClickListener() {
            @Override
            public void onClick(View view, int position) {
                Intent i = new Intent(view.getContext(), BillListActivity.class);
                startActivity(i);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        //Add new group intent
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(GroupActivity.this, PaymentsSearchActivity.class);
                startActivity(i);
            }
        });

        //Tab Button
        mAddGroupIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), GroupInviteActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        mGroupListIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(GroupActivity.this, "You are here.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Disable going back -- Goes to the home screen.
        moveTaskToBack(true);
    }

    private void writeNewGroup(List<GroupMembers> members, String name, String uid) {
        //Current User ID
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Selected User ID
        String selectedID = null;
        for (GroupMembers selected : members) {
            selectedID = selected.getUid();
        }

        //Group Name & ID
        Map<String, Object> groupNameAndID = new HashMap<>();
        groupNameAndID.put("name", name);
        groupNameAndID.put("uid", uid);

        //Unique ID for the group.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String uniqueKey = ref.child(currentUser).child("groups").push().getKey();

        //Where in the DB should "Group" be created?
        //TODO: CurrentUser Reference
        DatabaseReference mDatabaseReference = database.getReference("users").child(currentUser).child("groups");

        //Where in the DB should "Group" be created?
        //TODO: Members Reference
        DatabaseReference mDatabaseRefMemb = database.getReference("users").child(selectedID).child("invites").child(uniqueKey).child("members");

        //Picked Member.
        Map<String, Object> currentDict = new HashMap<>();

        //How 'members' will look.
        for (GroupMembers memb : members) {
            currentDict.put("name", memb.getName());
            currentDict.put("username", memb.getUsername());
            currentDict.put("email", memb.getEmail());
            currentDict.put("uid", memb.getUid());
            currentDict.put("joined", false);
            currentDict.put("admin", false);

            //Non Admin Members
            if (selectedID != currentUser) {
                currentDict.put("joined", false);
                currentDict.put("admin", false);

                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(selectedID, currentDict);
                ref.child("users").child(selectedID).child("invites").child(uniqueKey).setValue(groupNameAndID);
                mDatabaseRefMemb.updateChildren(childUpdates);

            } //Selected uID != currentID
            else {
                currentDict.put("joined", true);
                currentDict.put("admin", true);
                Map<String, Object> childUpdates = new HashMap<>();
                childUpdates.put(currentUser, currentDict);
                ref.child("users").child(selectedID).child("groups").child(uniqueKey).setValue(groupNameAndID);
                mDatabaseReference.updateChildren(childUpdates);
            }
            //Todo : Admin!
        }
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
