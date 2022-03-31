package edu.cuhk.csci3310.sharelock.activities;

import static edu.cuhk.csci3310.sharelock.globals.Constants.AUTH_EMAIL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SHARED_PREF_FILE;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.cuhk.csci3310.sharelock.R;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "Main";
    private FirebaseAuth auth;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseApp.initializeApp(this);
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        pref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        String email = pref.getString(AUTH_EMAIL, "");
        // if user has logged in before
        if (intent.getData() != null) {
            signIn(intent, email);
        }
        // if user logins by link for the first time
        else if(!email.isEmpty()){
            startHomeIfLoggedIn();
        }
        // if user has never logged in
        else {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }

    private void signIn(Intent intent, String email) {
        String emailLink = intent.getData().toString();
        Log.d(TAG, emailLink);
        // Confirm the link is a sign-in with email link.
        if (auth.isSignInWithEmailLink(emailLink)) {
            // Retrieve this from wherever you stored it
            // The client SDK will parse the code from the link for you.
            auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, getString(R.string.signin_success));
                            AuthResult result = task.getResult();
                            // You can access the new user via result.getUser()
                            // Additional user info profile *not* available via:
                            // result.getAdditionalUserInfo().getProfile() == null
                            // You can check if the user is new or existing:
                            // result.getAdditionalUserInfo().isNewUser()

                            startHomeIfLoggedIn();
                        } else {
                            Log.e(TAG, getString(R.string.sigin_failed), task.getException());
                        }
                    });
        }
    }

    private void startHomeIfLoggedIn() {
        if (auth.getCurrentUser() == null) {
            // go to login screen if email is stored in pref but user hasn't login using email link
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }
        Intent homeIntent = new Intent(this, HomeActivity.class);
        this.startActivity(homeIntent);
        finish();
    }
}