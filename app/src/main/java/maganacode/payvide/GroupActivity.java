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
import java.util.Objects;

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

    //RecyclerView Items
    private static String LIST_EXTRA = "Groups";

    //Handler
    private String currentUserID;

    //Declarations
    private String UID = null;
    private Group selectedGroup;
    private String groupName;
    private List<GroupMembers> mGroupMembers = new ArrayList<>(); //List of Group Members
    private List<UserList> selectedUsers = new ArrayList<>();
    private List<Group> groups = new ArrayList<>();
    private GroupActivityAdapter mAdapter;
    private DatabaseReference mRef;

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
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group);
        ButterKnife.bind(this);
        initializeBottomBar();

        //Grab the data if there is any!
        if (savedInstanceState != null) {
            Group loadGroups = (Group) savedInstanceState.getSerializable("Groups");
            groups.add(loadGroups);
            mAdapter.notifyDataSetChanged();
        }

        //Users from selected list -- NEW DATA
        selectedUsers = (List<UserList>) getIntent().getSerializableExtra("users");
        groupName = getIntent().getStringExtra("groupName");

        //Current User
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        
        mRef = FirebaseDatabase.getInstance().getReference("users");
        DatabaseReference newRef = mRef.child(currentUserID); //currentUserID
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                UserList user = dataSnapshot.getValue(UserList.class);

                selectedGroup = new Group(mGroupMembers, groupName, UID);
                groups.add(selectedGroup);

                //If the current user...
                if (Objects.equals(user.getUid(), currentUserID)) {
                    selectedUsers.add(user);
                    //Picked user.
                    for (UserList selected : selectedUsers) {
                        GroupMembers selectedMember = new GroupMembers();

                        selectedMember.setName(selected.getName());
                        selectedMember.setUsername(selected.getUsername());
                        selectedMember.setEmail(selected.getEmail());
                        selectedMember.setUid(selected.getUid());

                        mGroupMembers.add(selectedMember);
                    }
                } else {
                    for (UserList selected : selectedUsers) {
                        GroupMembers selectedMember = new GroupMembers();

                        selectedMember.setName(selected.getName());
                        selectedMember.setUsername(selected.getUsername());
                        selectedMember.setEmail(selected.getEmail());
                        selectedMember.setUid(selected.getUid());

                        mGroupMembers.add(selectedMember);
                    }
                }

                //RecyclerView
                mRecyclerview.setHasFixedSize(true);
                mRecyclerview.setLayoutManager(new LinearLayoutManager(GroupActivity.this));
                mAdapter = new GroupActivityAdapter(groups);
                mRecyclerview.setAdapter(mAdapter);

                //Todo : Write current user + admin to Firebase.
                writeNewGroup(mGroupMembers, groupName);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("Groups", selectedGroup);
    }

    @Override
    public void onBackPressed() {
        // Disable going back -- Goes to the home screen.
        moveTaskToBack(true);
    }

    private void writeNewGroup(List<GroupMembers> members, String name) {
        //Current User ID
        String currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();

        //Unique ID for the group.
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        String uniqueKey = ref.child(currentUser).child("groups").push().getKey();

        //Group Name & ID
        Map<String, Object> groupNameAndID = new HashMap<>();
        groupNameAndID.put("name", name);
        groupNameAndID.put("uid", uniqueKey);

        //Where in the DB should "Group" be created?
        //TODO: CurrentUser Reference
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = database.getReference("users").child(currentUser).child("groups").child(uniqueKey).child("members");

        //Picked Member.
        Map<String, Object> currentDict = new HashMap<>();
        Map<String, Object> pickedDict = new HashMap<>();

        for (GroupMembers selected : members) {
            String selectedID = selected.getUid();

            //Non Admin Members
            if (!Objects.equals(selectedID, currentUser)) {
                Map<String, Object> childUpdates = new HashMap<>();
                ref.child("users").child(selectedID).child("invites")
                        .child(uniqueKey)
                        .setValue(groupNameAndID);

                //Member's Reference
                DatabaseReference mDatabaseRefMemb = database.getReference("users")
                        .child(selectedID)
                        .child("invites")
                        .child(uniqueKey)
                        .child("members");

                //How 'members' will look.
                for (GroupMembers picked : members) {
                    pickedDict.put("name", picked.getName());
                    pickedDict.put("username", picked.getUsername());
                    pickedDict.put("email", picked.getEmail());
                    pickedDict.put("uid", picked.getUid());
                    pickedDict.put("joined", false);
                    pickedDict.put("admin", false);

                    if (Objects.equals(picked.getUid(), currentUserID)) {
                        pickedDict.put("joined", true);
                        pickedDict.put("admin", true);
                    }

                    //Sets the picked map.
                    childUpdates.put(picked.getUid(), pickedDict);
                }
                mDatabaseRefMemb.updateChildren(childUpdates);
            } else {

                //Current User
                ref.child("users").child(selectedID).child("groups").child(uniqueKey).setValue(groupNameAndID);
                for (GroupMembers current : members) {
                    currentDict.put("name", current.getName());
                    currentDict.put("username", current.getUsername());
                    currentDict.put("email", current.getEmail());
                    currentDict.put("uid", current.getUid());
                    currentDict.put("joined", false);
                    currentDict.put("admin", false);

                    if (Objects.equals(selected.getUid(), currentUserID)) {
                        currentDict.put("joined", true);
                        currentDict.put("admin", true);
                    }

                    Map<String, Object> childUpdates = new HashMap<>();
                    childUpdates.put(currentUser, currentDict);
                    databaseReference.updateChildren(childUpdates);
                }
            }
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
