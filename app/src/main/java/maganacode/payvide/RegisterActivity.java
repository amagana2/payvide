package maganacode.payvide;
/**
 * Author: Andrew Magana
 * Date: 12/13/2016
 * Revisions: 12/14/2016 -- Finished interface
 * 12/16/2016 -- Completed registration authentication
 * Activity: Register Activity
 * Goal: To sign up users via Firebase by Google.
 */


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import maganacode.payvide.Models.User;


/**
 * Register Activity
 * Preconditions: User must have valid email address and appropriate alphanumeric password
 * Postconditions: User is registered, and the Dashboard Activity starts if successfully registered.
 */
public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";

    //Firebase Authentication Login
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //Firebase Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference mDatabase;

    //Interjections via butterknife -- intializing all views.
    @Bind(R.id.input_name)
    EditText mInputName;
    @Bind(R.id.input_email)
    EditText mInputEmail;
    @Bind(R.id.input_username)
    EditText mInputUsername;
    @Bind(R.id.input_password)
    EditText mInputPassword;
    @Bind(R.id.btn_signup)
    AppCompatButton mBtnSignup;
    @Bind(R.id.link_login)
    TextView mLinkLogin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mDatabase = database.getReference("users");

        //Authenticates the registered user.
        mAuth = FirebaseAuth.getInstance();

        //Tells us whether the user is signed in or not.
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //Sign up button to begin signup class.
        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signup();
            }
        });

        //Button for if they have a login.
        mLinkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent to dashboard.
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }


    //Checks authentication
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    //Signs them up and writes to the database
    //Email, Name, and Username.
    public void signup() {
        Log.d(TAG, "RegisterActivity");

        if (!validate()) {
            onRegisterFailed();
            return;
        }

        //User Info
        String name = mInputName.getText().toString();
        String username = mInputUsername.getText().toString();
        String email = mInputEmail.getText().toString();
        String password = mInputPassword.getText().toString();

        //Animation for loading.
        final ProgressDialog progressDialog = new ProgressDialog(RegisterActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Creating Account...");
        progressDialog.show();

        //Verify and authenticate user input
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (task.isSuccessful()) {
                            onAuthSuccess(task.getResult().getUser());
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Sign Up Failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        //They can redo if necessary.
        mBtnSignup.setEnabled(false);
    }

    public void onAuthSuccess(FirebaseUser user) {
        mBtnSignup.setEnabled(true);
        setResult(RESULT_OK, null);

        //User Info
        String name = mInputName.getText().toString();
        String username = mInputUsername.getText().toString();
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String profilePic = " ";

        //On Auth Success
        writeNewUser(user.getEmail(), name, username, uid, profilePic);

        //Start new activity
        startActivity(new Intent(RegisterActivity.this, DashboardActivity.class));
        finish();
    }

    private void writeNewUser(String email, String name, String username, String uid, String profilePic) {
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        User user = new User(email, name, username, uid, profilePic);
        Map<String, Object> userValues = user.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put(uid, userValues);
        mDatabase.updateChildren(childUpdates);
    }

    public void onRegisterFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mBtnSignup.setEnabled(true);
    }

    /**
     * Preconditions: Name and username should be appropriate and not empty.
     * Email should match email patterns
     * Password should be alphanumeric between 4 and 10 characters.
     * Postconditions: The user should be validated, and no errors should come up.
     *
     * @return validation complete/successful
     */
    public boolean validate() {
        boolean valid = true;

        //User Info
        String name = mInputName.getText().toString();
        String username = mInputUsername.getText().toString();
        String email = mInputEmail.getText().toString();
        String password = mInputPassword.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            mInputName.setError("at least 3 characters");
            valid = false;
        } else {
            mInputName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mInputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            mInputEmail.setError(null);
        }

        //Optional
        if (username.isEmpty()) {
            mInputUsername.setError("Enter Valid Username");
            valid = false;
        } else {
            mInputUsername.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            mInputPassword.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            mInputPassword.setError(null);
        }

        return valid;
    }
}
