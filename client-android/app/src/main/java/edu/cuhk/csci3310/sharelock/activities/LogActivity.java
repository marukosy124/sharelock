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
import android.view.View;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.util.ArrayList;

import edu.cuhk.csci3310.sharelock.R;
import edu.cuhk.csci3310.sharelock.adapters.LockAccessListAdapter;
import edu.cuhk.csci3310.sharelock.adapters.LogListAdapter;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.GetLockAccessesAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.GetLockHistoryAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.GetLocksAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnGetLockAccessesCompleted;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnGetLockHistoryCompleted;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockAccess;
import edu.cuhk.csci3310.sharelock.classes.LockHistory;
import edu.cuhk.csci3310.sharelock.globals.Helpers;

public class LogActivity extends AppCompatActivity implements OnGetLockHistoryCompleted {
    private static final String TAG = "LogActivity";
    private RecyclerView mRecyclerView;
    private LogListAdapter mAdapter;
    private ArrayList<LockHistory> logList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);


        // get and set lock name from shared pref
        SharedPreferences pref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        String selectedLock = pref.getString(SELECTED_LOCK, "");
        Gson gson = new Gson();
        Lock lock = gson.fromJson(selectedLock, Lock.class);
        TextView lockName = findViewById(R.id.lock_name);
        lockName.setText(lock.getName());

        // get lock history
        Helpers.threadPolicy();
        GetLockHistoryAsyncTask getLockHistoryAsyncTasks = new GetLockHistoryAsyncTask(this, this, lock.getId());
        getLockHistoryAsyncTasks.execute();

        // event handlers for close button and update title
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> {
            this.finish();
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onGetLockHistoryCompleted(ArrayList<LockHistory> logs) {
        logList = logs;
        mRecyclerView = findViewById(R.id.log_list);
        if(logs.isEmpty()){
           TextView noLogsTextView = findViewById(R.id.no_logs);
           noLogsTextView.setVisibility(View.VISIBLE);
        }
        mAdapter = new LogListAdapter(this, logList);
        mRecyclerView.setAdapter(mAdapter);
    }
}