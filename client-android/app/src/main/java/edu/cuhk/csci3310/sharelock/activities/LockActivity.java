package edu.cuhk.csci3310.sharelock.activities;

import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_ADD;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_EDIT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_NAME;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_PRIVATE_KEY;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SELECTED_LOCK;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SHARED_PREF_FILE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.UUID;

import edu.cuhk.csci3310.sharelock.R;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.AddLockAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.DeleteLockAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.EditLockAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnLockTaskCompleted;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.globals.Helpers;
import edu.cuhk.csci3310.sharelock.globals.nfc.NfcServiceController;

public class LockActivity extends AppCompatActivity implements OnLockTaskCompleted {
    private static final String TAG = "LockActivity";
    private Boolean isTransmittingKey = false;
    private FirebaseAuth auth;
    private String privateKey = "";
    private String mode = FORM_MODE_ADD;
    private Bundle lockData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock);

        // get mode
        Intent intent = getIntent();
        mode = intent.getStringExtra(FORM_MODE);

        // create bundle for lock data
        lockData = new Bundle();
        SharedPreferences pref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        String selectedLock = pref.getString(SELECTED_LOCK, "");
        TextInputEditText nameField = (TextInputEditText) findViewById(R.id.lock_name_field);

        // update lock data and UI for edit mode
        Toolbar toolbar = findViewById(R.id.toolbar);
        if(mode.equals(FORM_MODE_EDIT) && selectedLock != null){
           Gson gson = new Gson();
           Lock lock = gson.fromJson(selectedLock, Lock.class);
           lockData.putInt(LOCK_ID, lock.getId());
           lockData.putString(LOCK_NAME, lock.getName());
           nameField.setText(lock.getName());
           toolbar.setTitle(R.string.edit_lock);
           toolbar.inflateMenu(R.menu.top_app_bar);
        }

        // close keyboard when enter is clicked
        nameField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            Helpers.closeKeyboard(this);
            return false;
        });

        // event handlers for save lock button
        ImageButton saveLockButton = findViewById(R.id.lock_button);
        saveLockButton.setOnClickListener(v -> onSaveLockButtonClicked());

        // set UI initial state
        updateRingUIBasedOnTransmitState();

        // event handlers for close button
        toolbar.setNavigationOnClickListener(v -> {
            this.finish();
        });

        // event handlers for save button
        Button saveButton = findViewById(R.id.save_button);
        saveButton.setOnClickListener(v -> {
            String lockName = nameField.getText().toString();
            lockData.putString(LOCK_NAME, lockName);
            String privateKey = lockData.getString(LOCK_PRIVATE_KEY);

            Boolean isShownAlready = false;
            Toast toast = Toast.makeText(LockActivity.this, R.string.missing_fields, Toast.LENGTH_LONG);
            if (toast.getView() != null) {
                TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
                if(toastView != null) toastView.setGravity(Gravity.CENTER);
                toast.setGravity(Gravity.BOTTOM, 0, 250);
            }

            if(lockName == null || lockName.trim().isEmpty()){
                toast.show();
                isShownAlready = true;
            }
            if((privateKey == null || privateKey.trim().isEmpty()) && mode.equals(FORM_MODE_ADD) && !isShownAlready){
                toast.show();
            } else {
                switch(mode){
                    case FORM_MODE_ADD:
                        AddLockAsyncTask addlockAsyncTask = new AddLockAsyncTask(this, this, lockData);
                        addlockAsyncTask.execute();
                        break;
                    case FORM_MODE_EDIT:
                        EditLockAsyncTask editlockAsyncTask = new EditLockAsyncTask(this, this, lockData);
                        editlockAsyncTask.execute();
                        break;
                    default:
                        return;
                }
            }
        });

        // event handler for delete button
        toolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) item -> {
            String clickedTitle = (String) item.getTitle();
            if(clickedTitle.equals("Delete")){
                DeleteLockAsyncTask deletelockAsyncTask = new DeleteLockAsyncTask(this, this, lockData);
                deletelockAsyncTask.execute();
            }
            return false;
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // stop NFC before navigating away
        if (isTransmittingKey) { onSaveLockButtonClicked(); }
    }

    // when add lock completed
    @Override
    public void onLockTaskCompleted(Lock newLock) {
        int msg;
        if(newLock == null){
            msg = R.string.lock_deleted;
        } else if(mode.equals(FORM_MODE_ADD)){
            msg = R.string.lock_added;
        } else {
            msg = R.string.lock_edited;
        }
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        if (toast.getView() != null) {
            TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
            if(v != null) v.setGravity(Gravity.CENTER);
        }
        toast.show();
        Intent homeIntent = new Intent(this, HomeActivity.class);
        this.startActivity(homeIntent);
    }

    private void onSaveLockButtonClicked() {
        if (!isTransmittingKey) {
            // new UUID as key
            UUID uuid = UUID.randomUUID();
            privateKey = uuid.toString();
            lockData.putString(LOCK_PRIVATE_KEY, privateKey);
            // turn on NFC
            NfcServiceController.getInstance()
                    .enableNfcWithString(LockActivity.this, privateKey);
        } else {
            // turn off NFC
            NfcServiceController.getInstance().stopNfc(LockActivity.this);
        }

        isTransmittingKey = !isTransmittingKey;
        updateRingUIBasedOnTransmitState();
    }

    private void updateRingUIBasedOnTransmitState() {
        TextView saveLockMessage = findViewById(R.id.lock_message);
        ImageView saveIcon = findViewById(R.id.lock_icon);
        Drawable signalIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_speaker_phone_24);
        ImageButton saveLockButton = findViewById(R.id.lock_button);
        VectorChildFinder vector = new VectorChildFinder(this, R.drawable.lock_button_ring, saveLockButton);
        VectorDrawableCompat.VFullPath ring = vector.findPathByName("ring");

        if(!isTransmittingKey){
            saveIcon.setImageDrawable(signalIcon);
            ring.setFillColor(Color.parseColor(getString(R.string.ring_normal_color)));
            saveLockButton.invalidate();
            saveLockMessage.setText(mode.equals(FORM_MODE_EDIT) ? R.string.tap_to_save_new : R.string.tap_to_save);
        } else {
            Drawable successIcon = ContextCompat.getDrawable(this, R.drawable.ic_baseline_check_24);
            saveIcon.setImageDrawable(successIcon);
            ring.setFillColor(Color.parseColor(getString(R.string.ring_success_color)));
            saveLockMessage.setText(R.string.signal_success);

        }
    }
}