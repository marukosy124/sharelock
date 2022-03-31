package edu.cuhk.csci3310.sharelock.activities;

import static edu.cuhk.csci3310.sharelock.globals.Constants.AUTH_EMAIL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SHARED_PREF_FILE;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;

import edu.cuhk.csci3310.sharelock.R;
import edu.cuhk.csci3310.sharelock.globals.Helpers;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "Login";
    private FirebaseAuth auth;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        // event handler for email field
        TextInputEditText emailField = (TextInputEditText) findViewById(R.id.email_field);
        Button loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {
            // save email to preference
            email = emailField.getText().toString();
            SharedPreferences pref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString(AUTH_EMAIL, email);
            editor.commit();
            // send sign in email
            performSignIn(email);
            loginButton.setEnabled(false);
        });
        emailField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            if (loginButton.isEnabled()) {
                loginButton.performClick();
            }
            return false;
        });

    }

    private void performSignIn(String email) {
        ActionCodeSettings actionCodeSettings =
                ActionCodeSettings.newBuilder()
                        // URL you want to redirect back to. The domain (www.example.com) for this
                        // URL must be whitelisted in the Firebase Console.
                        .setUrl("https://sharelock-3310.firebaseapp.com")
                        // This must be true
                        .setHandleCodeInApp(true)
                        .setAndroidPackageName(
                                getApplicationContext().getPackageName(),
                                true, /* installIfNotAvailable */
                                null    /* minimumVersion */)
                        .build();
        auth.sendSignInLinkToEmail(email, actionCodeSettings)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast toast = Toast.makeText(this, "A login email has been sent to you, please check.", Toast.LENGTH_LONG);
                        if (toast.getView() != null) {
                            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                            if(v != null) v.setGravity(Gravity.CENTER);
                        }
                        toast.show();

                        Button loginButton = findViewById(R.id.login_button);
                        loginButton.setText("Login Email Sent");
                        loginButton.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_done_24, 0, 0,0);
                        loginButton.setBackgroundColor(0xFF95F095);
                    } else {
                        Log.e(TAG, task.getResult().toString());
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "send email failed", e));
    }
}