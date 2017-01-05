package maganacode.payvide;
/**
 * Author: Andrew Magana
 * Date: 12/13/2016
 * Revisions: 12/15/2016 -- Finished interface
 * 12/16/2016 -- Completed Login Authentication
 * Activity: Register Activity
 * Goal: To sign up users via Firebase by Google.
 */

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.Bind;
import butterknife.ButterKnife;


/**
 * Precondition: User must have a registered account
 * Postcondition: If email and password match the database, the user may proceed.
 */
public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;

    //Firebase Authentication Login
    private FirebaseAuth mAuth;

    //Listener to check if user is authenticated or not.
    private FirebaseAuth.AuthStateListener mAuthStateListener;


    @Bind(R.id.input_email)
    EditText mInputEmail;
    @Bind(R.id.input_password)
    EditText mInputPassword;
    @Bind(R.id.btn_register)
    TextView mRegisterButton;
    @Bind(R.id.btn_login)
    Button mLoginButton;
    @Bind(R.id.link_password)
    TextView mLinkPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //Gets the authenticated users.
        mAuth = FirebaseAuth.getInstance();

        //Auth Listener -- Check if user sign in or not
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    //User signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in  " + user.getUid());
                } else {
                    //User signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };

        //Forgot Password? New activity starts.
        mLinkPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Forgot Password Activity
            }
        });

        //Button runs the login class
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });

        //Register activity opens with animation
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new activity intent
                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivityForResult(i, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    /**
     * Precondition: User must have valid and authenticated email and password.
     * Postcondition: User begins the main activity!
     */
    public void login() {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        mLoginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this, R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String username = mInputEmail.getText().toString();
        String password = mInputPassword.getText().toString();

        mAuth.signInWithEmailAndPassword(username, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(TAG, "signInWithEmail:failed", task.getException());
                    Toast.makeText(LoginActivity.this, "Login Failed, Please Try Again",
                            Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                } else {
                    onLoginSuccess();
                    progressDialog.dismiss();
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess() {
        mLoginButton.setEnabled(true);
        finish();

        //Start new activity
        startActivity(new Intent(LoginActivity.this, DashboardActivity.class));
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        mLoginButton.setEnabled(false);
    }

    //Validates user according to criteria
    public boolean validate() {
        //valid flag.
        boolean valid = true;

        //User's inputs.
        String email = mInputEmail.getText().toString();
        String password = mInputPassword.getText().toString();

        //Checks inputs.
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mInputEmail.setError("Enter a valid username");
            valid = false;
        } else {
            mInputEmail.setError(null);
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
