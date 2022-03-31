package edu.cuhk.csci3310.sharelock.activities;

import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_ADD;
import static edu.cuhk.csci3310.sharelock.globals.Constants.IS_MODIFIED;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SELECTED_LOCK;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SHARED_PREF_FILE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;

import edu.cuhk.csci3310.sharelock.R;
import edu.cuhk.csci3310.sharelock.adapters.LockAccessListAdapter;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.GetLockAccessesAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnGetLockAccessesCompleted;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockAccess;
import edu.cuhk.csci3310.sharelock.globals.Helpers;

public class ShareListActivity extends AppCompatActivity implements OnGetLockAccessesCompleted {
    private static final String TAG = "LockAccessListActivity";
    private RecyclerView mRecyclerView;
    private LockAccessListAdapter mAdapter;
    private ArrayList<LockAccess> lockAccessList = new ArrayList<>();
    private String email;
    private FirebaseAuth auth;
    private int lockId;
    private boolean needReload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sharelist);

        // get JWT token and email
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        // get and set lock access list from shared pref
        SharedPreferences pref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        String selectedLock = pref.getString(SELECTED_LOCK, "");
        Gson gson = new Gson();
        Lock lock = gson.fromJson(selectedLock, Lock.class);
        lockId = lock.getId();
        lockAccessList = lock.getLockAccesses();
        Log.d(TAG, "on create" + needReload);
        setLockAccessList();

        // reload lock access list if redirect from share form
        Intent intent = getIntent();
        boolean isModified = intent.getBooleanExtra(IS_MODIFIED, false);
        if(isModified){
            Helpers.threadPolicy();
            GetLockAccessesAsyncTask getLockAccessesAsyncTasks = new GetLockAccessesAsyncTask(this, this, lock.getId());
            getLockAccessesAsyncTasks.execute();
        }

        // event handlers for close button and update title
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            Intent backIntent = new Intent(this, HomeActivity.class);
            this.startActivity(backIntent);

        });
        toolbar.setTitle("Share " + lock.getName());

        // event handler for add lock access button
        FloatingActionButton addLockAccessButton = findViewById(R.id.add_lock_access_button);
        addLockAccessButton.setOnClickListener(v -> {
            Intent addAccessIntent = new Intent(this, ShareActivity.class);
            addAccessIntent.putExtra(FORM_MODE, FORM_MODE_ADD);
            this.startActivity(addAccessIntent);
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if(needReload) {
            Helpers.threadPolicy();
            GetLockAccessesAsyncTask getLockAccessesAsyncTasks = new GetLockAccessesAsyncTask(this, this, lockId);
            getLockAccessesAsyncTasks.execute();
        }
        needReload = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onGetLockAccessesCompleted(ArrayList<LockAccess> lockAccesses) {
        lockAccessList = lockAccesses;
        setLockAccessList();
    }

    private void setLockAccessList() {
        mRecyclerView = findViewById(R.id.share_list);
        mAdapter = new LockAccessListAdapter(this, lockAccessList);
        mRecyclerView.setAdapter(mAdapter);
    }
}