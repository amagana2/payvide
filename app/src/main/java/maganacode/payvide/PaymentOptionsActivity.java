package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.xdty.preference.colorpicker.ColorPickerDialog;
import org.xdty.preference.colorpicker.ColorPickerSwatch;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import maganacode.payvide.Models.GroupMembers;
import maganacode.payvide.Models.UserList;
import maganacode.payvide.adapter.MembersAdapter;

public class PaymentOptionsActivity extends AppCompatActivity {
    //Tag
    private static String TAG = "PaymentOptionsActivity";

    //Reference -- we are at User's
    DatabaseReference mRef;

    //GroupMembers
    List<UserList> groupMembers = new ArrayList<>();

    @Bind(R.id.name_label)
    TextView mNameLabel;
    @Bind(R.id.cost_label)
    TextView mCostLabel;
    @Bind(R.id.color_label)
    TextView mColorLabel;
    @Bind(R.id.recurrence_label)
    TextView mRecurrenceLabel;
    @Bind(R.id.name_field)
    EditText mNameField;
    @Bind(R.id.cost_field)
    EditText mCostField;
    @Bind(R.id.color_button)
    Button mColorButton;
    @Bind(R.id.recurrence_spinner)
    Spinner mRecurrenceSpinner;
    @Bind(R.id.activity_payment_options)
    RelativeLayout mActivityPaymentOptions;
    @Bind(R.id.members_recycler)
    RecyclerView mRecyclerview;
    @Bind(R.id.bottom_naviation)
    BottomNavigationView mBottomNavigationView;

    private int mSelectedColor;
    private List<GroupMembers> mMembers = new ArrayList<>(); //Group Members in a List.
    private String name;
    private String username;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        ButterKnife.bind(this);
        initiateBottomView();

        //Extra from other activity.
        groupMembers = (List<UserList>) getIntent().getSerializableExtra("users"); //Selected Members

        /**
         * Grabs the current user from their ID since it matches the database.
         */
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mRef = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference newRef = mRef.child(currentUserID);
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "Name: " + dataSnapshot.child("name").getValue());

                GroupMembers mCurrentMember = new GroupMembers();

                name = String.valueOf(dataSnapshot.child("name").getValue());
                username = String.valueOf(dataSnapshot.child("username").getValue());

                mCurrentMember.setName(name);
                mCurrentMember.setUsername(username);

                mMembers.add(mCurrentMember);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //Convert UserList -> Members
        for (UserList users : groupMembers) {
            name = users.getName();
            username = users.getUsername();

            GroupMembers mGroupMembers = new GroupMembers(); //Individual Group Members
            mGroupMembers.setName(name);
            mGroupMembers.setUsername(username);

            //List<GroupMembers> now has however members were selected
            mMembers.add(mGroupMembers);
        }

        //RecyclerView + Adapter
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        MembersAdapter adapter = new MembersAdapter(mMembers);
        mRecyclerview.setAdapter(adapter);

        //Recurrence Spinner
        mRecurrenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String choice = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(PaymentOptionsActivity.this, "Recurrence: " + choice, Toast.LENGTH_SHORT).show();
                //TODO : Extra for this recurrence.
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                //Default
            }
        });

        //Drop Down Elements
        List<String> elements = new ArrayList<>();
        elements.add("Weekly");
        elements.add("Biweekly");
        elements.add("Monthly");
        elements.add("Quarterly");
        elements.add("Yearly");

        //Adapter for displaying drop down menu with elements
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                elements);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecurrenceSpinner.setAdapter(dataAdapter);

        //Color Picker
        mSelectedColor = ContextCompat.getColor(this, R.color.flamingo);

        mColorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int[] mColors = getResources().getIntArray(R.array.default_rainbow);

                ColorPickerDialog dialog = ColorPickerDialog.newInstance(R.string.color_picker_default_title,
                        mColors,
                        mSelectedColor,
                        5,
                        ColorPickerDialog.SIZE_SMALL);
                dialog.setOnColorSelectedListener(new ColorPickerSwatch.OnColorSelectedListener() {
                    @Override
                    public void onColorSelected(int color) {
                        mSelectedColor = color;
                        mColorButton.setBackgroundColor(mSelectedColor);
                    }
                });
                dialog.show(getFragmentManager(), "color_dialog_test");
            }
        });
    }

    public void onBackPressed() {
        // Disable going back -- Goes to the home screen.
        moveTaskToBack(true);
    }

    public void initiateBottomView() {
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent i;
                switch (item.getItemId()) {
                    case R.id.action_groups:
                        //Refresh
                        i = new Intent(PaymentOptionsActivity.this, PaymentsSearchActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_dashboard:
                        i = new Intent(PaymentOptionsActivity.this, DashboardActivity.class);
                        startActivity(i);
                        return true;
                    case R.id.action_profile:
                        item.setEnabled(true);
                        i = new Intent(PaymentOptionsActivity.this, ProfileActivity.class);
                        startActivity(i);
                        return true;
                }
                return false;
            }
        });
    }
}
