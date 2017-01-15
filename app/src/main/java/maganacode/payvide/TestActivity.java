package maganacode.payvide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import maganacode.payvide.Models.Group;
import maganacode.payvide.Models.GroupMembers;
import maganacode.payvide.Models.UserList;
import maganacode.payvide.adapter.GroupActivityAdapter;

public class TestActivity extends AppCompatActivity {

    //Tag
    private static String TAG = "TestActivity";

    private List<UserList> selectedUsers;
    private List<GroupMembers> mGroupMembers;
    private RecyclerView mRecyclerView;
    private GroupActivityAdapter mAdapter;
    private List<Group> groups = new ArrayList<>();
    private Group selectedGroup;
    private String groupName;
    private String UID;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //Selected users from previous activity.
        selectedUsers = (List<UserList>) getIntent().getSerializableExtra("users");
        groupName = "TestGroup";
        UID = "123";

        //RecyclerView
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerTest);

        //GroupMembers
        mGroupMembers = new ArrayList<>();

        writeNewGroup(mGroupMembers, groupName);

        childListeners();

    }


    public void childListeners() {
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);
        mRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                UserList user = dataSnapshot.getValue(UserList.class);

                selectedGroup = new Group(mGroupMembers, groupName, UID);
                groups.add(selectedGroup);

                GroupMembers mCurrent = new GroupMembers();

                mCurrent.setName(String.valueOf(dataSnapshot.child("name").getValue()));
                mCurrent.setEmail(String.valueOf(dataSnapshot.child("email").getValue()));
                mCurrent.setUsername(String.valueOf(dataSnapshot.child("username").getValue()));
                mCurrent.setUid(currentUserID);

                mGroupMembers.add(mCurrent);

                //If the current user...
                if (Objects.equals(user.getUid(), currentUserID)) {
                    //Picked user.
                    for (UserList selected : selectedUsers) {
                        GroupMembers selectedMember = new GroupMembers();
                        selectedMember.setName(selected.getName());
                        selectedMember.setUsername(selected.getUsername());
                        selectedMember.setEmail(selected.getEmail());
                        selectedMember.setUid(selected.getUid());
                        mGroupMembers.add(selectedMember);
                    }
                }

                //Recycler + Adapter
                mRecyclerView.setHasFixedSize(true);
                mRecyclerView.setLayoutManager(new LinearLayoutManager(TestActivity.this));
                mAdapter = new GroupActivityAdapter(groups);
                mRecyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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
}
