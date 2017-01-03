package maganacode.payvide;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_options);
        ButterKnife.bind(this);

        //Get Selected Members
        List<UserList> groupMembers = (List<UserList>) getIntent().getSerializableExtra("users");

        //Convert UserList -> Members
        List<GroupMembers> members = new ArrayList<>(); //List of Group Members
        GroupMembers member = new GroupMembers(); //Individual Group Members

        for (UserList users : groupMembers) {
            String name = users.getName();
            String email = users.getEmail();
            String username = users.getUsername();
            member.setName(name);
            member.setEmail(email);
            member.setUsername(username);

            members.add(member); //List<GroupMembers> now has however members were selected.
        }

        //RecyclerView + Adapter
        mRecyclerview.setHasFixedSize(true);
        mRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        MembersAdapter mAdapter = new MembersAdapter(members);
        mRecyclerview.setAdapter(mAdapter);

        //Spinner
        mRecurrenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                //Toast
                Toast.makeText(PaymentOptionsActivity.this, "You picked: " + item, Toast.LENGTH_SHORT).show();
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
