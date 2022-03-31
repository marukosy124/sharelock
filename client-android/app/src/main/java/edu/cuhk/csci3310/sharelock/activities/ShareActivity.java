package edu.cuhk.csci3310.sharelock.activities;

import static edu.cuhk.csci3310.sharelock.globals.Constants.DISPLAY_FORMAT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_ADD;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_EDIT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.ISO_FORMAT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.IS_MODIFIED;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_EXPIRED_AT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_PERMISSION;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_USER_EMAIL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_NAME;
import static edu.cuhk.csci3310.sharelock.globals.Constants.NEVER;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SELECTED_LOCK;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SELECTED_LOCK_ACCESS;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SHARED_PREF_FILE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicReference;

import edu.cuhk.csci3310.sharelock.R;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.AddLockAccessAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.DeleteLockAccessAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.DeleteLockAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.EditLockAccessAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnLockAccessTaskCompleted;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockAccess;
import edu.cuhk.csci3310.sharelock.globals.Helpers;

@RequiresApi(api = Build.VERSION_CODES.O)
public class ShareActivity extends AppCompatActivity implements OnLockAccessTaskCompleted {
    private static final String TAG = "ShareActivity";
    private ArrayList<LockAccess> lockAccessList = new ArrayList<>();
    private String mode;
    private Bundle lockAccessData;
    SimpleDateFormat isoFormatSimple = new SimpleDateFormat(ISO_FORMAT);
    DateTimeFormatter isoFormatDatetime = DateTimeFormatter.ofPattern(ISO_FORMAT);
    SimpleDateFormat displayFormat = new SimpleDateFormat(DISPLAY_FORMAT);
    FragmentManager datePickerFragmentManager;
    FragmentManager timePickerFragmentManager;
    AtomicReference<LocalDateTime> expiryDate = new AtomicReference<>();
    TextInputEditText expiryTimeField;
    private FirebaseAuth auth;
    private String email = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        // get JWT token and email
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        // event handlers for back button
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            this.finish();
        });

        // get mode
        Intent intent = getIntent();
        mode = intent.getStringExtra(FORM_MODE);

        // event handler for email field
        TextInputEditText emailField = (TextInputEditText) findViewById(R.id.access_email_field);
        emailField.setOnEditorActionListener((textView, actionId, keyEvent) -> {
            Helpers.closeKeyboard(this);
            return false;
        });

        // create bundle for lock access data & update UI for edit mode
        lockAccessData = new Bundle();
        expiryTimeField = findViewById(R.id.expiry_time_field);
        AutoCompleteTextView autoCompleteRoleField = findViewById(R.id.auto_complete_role);
        Gson gson = new Gson();

        // get lock access data according to mode
        switch(mode){
            case FORM_MODE_ADD:
                // get lock id from pref
                SharedPreferences pref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
                String selectedLock = pref.getString(SELECTED_LOCK, "");
                Lock lock = gson.fromJson(selectedLock, Lock.class);
                lockAccessData.putInt(LOCK_ID, lock.getId());
                break;

            case FORM_MODE_EDIT:
                // set lock access data
                Bundle selectedLockAccess = intent.getBundleExtra(SELECTED_LOCK_ACCESS);
                String lockAccessString = selectedLockAccess.getString(LOCK_ACCESS);
                LockAccess lockAccess = gson.fromJson(lockAccessString, LockAccess.class);
                lockAccessData.putInt(LOCK_ID, selectedLockAccess.getInt(LOCK_ID));
                lockAccessData.putString(LOCK_NAME, selectedLockAccess.getString(LOCK_NAME));
                lockAccessData.putInt(LOCK_ACCESS_ID, lockAccess.getId());
                String email = lockAccess.getUser().getName();
                String permission = lockAccess.getPermission();
                String expiredAt = lockAccess.getExpiredAt();
                lockAccessData.putString(LOCK_ACCESS_USER_EMAIL, email);
                lockAccessData.putString(LOCK_ACCESS_PERMISSION, permission);
                lockAccessData.putString(LOCK_ACCESS_EXPIRED_AT, expiredAt);

                // update UI
                toolbar.setTitle(R.string.edit_share);
                toolbar.inflateMenu(R.menu.top_app_bar);
                emailField.setText(email);
                if(expiredAt.equals("null") || expiredAt.isEmpty()){
                    expiryTimeField.setText(NEVER);
                } else {
                    expiryTimeField.setText(Helpers.formatISOToDisplay(expiredAt));
                }
                autoCompleteRoleField.setText(permission.substring(0, 1).toUpperCase() + permission.substring(1));
                break;

            default:
                break;
        }

        // set dropdown menu for role
        String[] roles = getResources().getStringArray(R.array.roles);
        ArrayAdapter roleArrayAdapter = new ArrayAdapter(this, R.layout.dropdown_item, roles);
        autoCompleteRoleField.setAdapter(roleArrayAdapter);

        // event handler for delete button
        toolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) item -> {
            String clickedTitle = (String) item.getTitle();
            if(clickedTitle.equals("Delete")){
                DeleteLockAccessAsyncTask deletelockAccessAsyncTask = new DeleteLockAccessAsyncTask(this, this, lockAccessData);
                deletelockAccessAsyncTask.execute();
            }
            return false;
        });

        // event handler for datetime picker
        datePickerFragmentManager = getSupportFragmentManager();
        timePickerFragmentManager = getSupportFragmentManager();
        Button neverButton = findViewById(R.id.never_button);
        neverButton.setOnClickListener(v -> {
            lockAccessData.putString(LOCK_ACCESS_EXPIRED_AT, "");
            expiryTimeField.setText(NEVER);
        });
        Button selectTimeButton = findViewById(R.id.select_time_button);
        selectTimeButton.setOnClickListener(v -> {
            getDatetime();
        });

        // disable all touchables except back button if user tries to modify his own access
        if(emailField.getText().toString().equals(email)){
            disableAllTouchables();
        }

        // event handler for share button
        Button shareButton = findViewById(R.id.save_share_button);
        shareButton.setOnClickListener(v -> {
            String userEmail = emailField.getText().toString();
            String permission = autoCompleteRoleField.getText().toString();
            lockAccessData.putString(LOCK_ACCESS_USER_EMAIL, userEmail);
            lockAccessData.putString(LOCK_ACCESS_PERMISSION, permission);
            if((permission == null || permission == "" || userEmail == null || userEmail == "")){
                Toast toast = Toast.makeText(this, R.string.missing_fields, Toast.LENGTH_LONG);
                TextView toastView = (TextView) toast.getView().findViewById(android.R.id.message);
                if(toastView != null) toastView.setGravity(Gravity.CENTER);
                toast.setGravity(Gravity.BOTTOM, 0, 250);
                toast.show();
            } else {
                Helpers.threadPolicy();
                switch(mode){
                    case FORM_MODE_ADD:
                        AddLockAccessAsyncTask addlockAccessAsyncTask = new AddLockAccessAsyncTask(this, this, lockAccessData);
                        addlockAccessAsyncTask.execute();
                        break;
                    case FORM_MODE_EDIT:
                        EditLockAccessAsyncTask editlockAccessAsyncTask = new EditLockAccessAsyncTask(this, this, lockAccessData);
                        editlockAccessAsyncTask.execute();
                        break;
                    default:
                        return;
                }
            }
        });
    }

    @Override
    public void onLockAccessTaskCompleted(LockAccess newLockAccess) {
        // trigger share sheet
        String msg;
        if(newLockAccess == null){
            msg = "Your email (" + lockAccessData.getString(LOCK_ACCESS_USER_EMAIL) + ") has been removed from the access to " + lockAccessData.getString(LOCK_NAME) + " as " + lockAccessData.getString(LOCK_ACCESS_PERMISSION);
        } else if(mode.equals(FORM_MODE_ADD)){
            msg = "Your email (" + newLockAccess.getUser().getName() + ") has been added to the access to " + lockAccessData.getString(LOCK_NAME) + " as " + newLockAccess.getPermission();
        } else {
            msg = "Your access (" + newLockAccess.getUser().getName() + ") to " + lockAccessData.getString(LOCK_NAME) + " has been updated";
        }
        msg += ". Please download ShareLook to manage your lock accesses.";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, msg);
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
        finish();
    }

    private void getDatetime() {
        // event handler for date picker
        MaterialDatePicker datePicker = MaterialDatePicker.Builder.datePicker()
                .setTheme(R.style.DatePicker)
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds()).build();
        datePicker.show(datePickerFragmentManager, "DatePicker");
        datePicker.addOnPositiveButtonClickListener((MaterialPickerOnPositiveButtonClickListener<Long>) selection -> {
            Calendar calendarDate = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
            calendarDate.setTimeInMillis(selection);
            String formattedDate  = isoFormatSimple.format(calendarDate.getTime());
            expiryDate.set(LocalDateTime.parse(formattedDate, isoFormatDatetime));

            // event handler for time picker
            MaterialTimePicker timePicker = (new MaterialTimePicker.Builder().setTheme(R.style.TimePicker)).setTimeFormat(TimeFormat.CLOCK_12H).build();
            timePicker.show(timePickerFragmentManager, "TimePicker");
            timePicker.addOnPositiveButtonClickListener(v -> {
                Calendar specificTime = new GregorianCalendar(expiryDate.get().getYear(),
                        expiryDate.get().getMonthValue() - 1,
                        expiryDate.get().getDayOfMonth(),
                        timePicker.getHour(),
                        timePicker.getMinute());
                String formattedDatetime  = displayFormat.format(specificTime.getTime());

                // set formatted new expiry time to text field
                expiryTimeField.setText(formattedDatetime);
                lockAccessData.putString(LOCK_ACCESS_EXPIRED_AT, isoFormatSimple.format(specificTime.getTime()));
            });
        });
    }

    private void disableAllTouchables() {
        View currentView = this.findViewById(android.R.id.content);
        ArrayList<View> layoutButtons = currentView.getTouchables();
        for(View v : layoutButtons){
            if(!(v instanceof AppCompatImageButton)) {
                v.setEnabled(false);
            }
        }
    }
}