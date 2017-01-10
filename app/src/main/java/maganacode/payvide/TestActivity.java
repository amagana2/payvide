package maganacode.payvide;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import maganacode.payvide.Models.GroupMembers;
import maganacode.payvide.Models.UserList;

public class TestActivity extends AppCompatActivity {

    //Tag
    private static String TAG = "TestActivity";

    private List<UserList> selectedUsers;
    private TextView mPickedText;
    private TextView mCurrentText;
    private TextView mMembCountText;
    private List<GroupMembers> mGroupMembers;
    private String memberNames;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        //Selected users from previous activity.
        selectedUsers = (List<UserList>) getIntent().getSerializableExtra("users");

        //Text Views
        mPickedText = (TextView) findViewById(R.id.first_text);
        mCurrentText = (TextView) findViewById(R.id.second_text);
        mMembCountText = (TextView) findViewById(R.id.third_text);

        //GroupMembs
        mGroupMembers = new ArrayList<>();

        //Current user.
        final String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("users").child(currentUserID);

        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserList user = dataSnapshot.getValue(UserList.class);
                GroupMembers member;

                //If the current user...
                if (Objects.equals(user.getUid(), currentUserID)) {
                    mCurrentText.setText("Current: " + user.getName()+ "");
                    member = new GroupMembers(user.getName(), user.getUsername(), user.getEmail(), user.getUid());

                    Log.d(TAG, "Member object contains...Name: " + user.getName() + "\n" +
                            "Username: " + user.getUsername() + "\n" +
                            "Email: " + user.getEmail() +
                            "uid: " + user.getUid());

                    selectedUsers.add(user);

                    for (GroupMembers members : mGroupMembers) {
                        Log.d(TAG, "Member object contains...Name: " + members.getName() + "\n" +
                                "Username: " + members.getUsername() + "\n" +
                                "Email: " + members.getEmail() +
                                "uid: " + members.getUid());
                    }

                    //Picked user.
                    for (UserList selected : selectedUsers) {
                        GroupMembers selectedMember = new GroupMembers();

                        selectedMember.setName(selected.getName());
                        selectedMember.setUsername(selected.getUsername());
                        selectedMember.setEmail(selected.getEmail());
                        selectedMember.setUid(selected.getUid());

                        mGroupMembers.add(selectedMember);

                        mPickedText.setText("Selected: " + selected.getName() + "");
                    }

                    mMembCountText.setText("Group Members: " + mGroupMembers.size() + " ");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
