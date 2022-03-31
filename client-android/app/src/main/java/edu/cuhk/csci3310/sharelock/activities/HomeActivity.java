package edu.cuhk.csci3310.sharelock.activities;
import static edu.cuhk.csci3310.sharelock.globals.Constants.AUTH_EMAIL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.JWT_FAILED;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_ADD;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_EDIT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SHARED_PREF_FILE;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.devs.vectorchildfinder.VectorChildFinder;
import com.devs.vectorchildfinder.VectorDrawableCompat;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import edu.cuhk.csci3310.sharelock.R;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.GetLockAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnLockTaskCompleted;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.adapters.LockListAdapter;
import edu.cuhk.csci3310.sharelock.apis.asyncTasks.GetLocksAsyncTask;
import edu.cuhk.csci3310.sharelock.apis.interfaces.OnGetLocksCompleted;
import edu.cuhk.csci3310.sharelock.globals.Helpers;

import edu.cuhk.csci3310.sharelock.globals.nfc.KHostApduService;
import edu.cuhk.csci3310.sharelock.globals.nfc.NfcHelper;
import edu.cuhk.csci3310.sharelock.globals.nfc.NfcServiceController;

public class HomeActivity extends AppCompatActivity implements OnGetLocksCompleted, OnLockTaskCompleted {
    private static final String TAG = "Home";
    private BottomSheetBehavior bottomSheetBehavior;
    private RecyclerView mRecyclerView;
    private Toolbar mToolbar;
    private LockListAdapter mAdapter;
    private ArrayList<Lock> lockList = new ArrayList<>();
    SharedPreferences pref;
    private String email;
    private Boolean isUnlocked = false;
    private FirebaseAuth auth;
    private String token = "";
    private Lock selectedLock;
    MenuItem shareButton;
    MenuItem editLockButton;
    MenuItem viewAccessLogButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mToolbar = findViewById(R.id.toolbar);
        pref = getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);

        // get JWT token and email
        auth = FirebaseAuth.getInstance();
        email = auth.getCurrentUser().getEmail();

        // get locks
        Helpers.threadPolicy();
        GetLocksAsyncTask getLocksAsyncTasks = new GetLocksAsyncTask(this, this);
        getLocksAsyncTasks.execute();

        // layout events for the bottom bar
        LinearLayout appbarView = (LinearLayout) findViewById(R.id.bottom_navigation_container);
        bottomSheetBehavior = BottomSheetBehavior.from(appbarView);
        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.app_bar);
        bottomSheetBehavior.setPeekHeight(appbar.getMinimumHeight());

        // event handler for account button
        Button accountButton = findViewById(R.id.account_button);
        accountButton.setText(email);
        accountButton.setOnClickListener(v -> {
            logout();
        });

        // event handlers for unlock button
        ImageButton lockButton = findViewById(R.id.lock_button);
        lockButton.setOnClickListener(v -> {
            onLockButtonClicked();
        });

        // event handler for add lock button
        Button addLockButton = findViewById(R.id.add_lock_button);
        addLockButton.setOnClickListener(v -> {
            Intent addLockIntent = new Intent(this, LockActivity.class);
            addLockIntent.putExtra(FORM_MODE, FORM_MODE_ADD);
            this.startActivity(addLockIntent);
        });

        // event handler for refresh button
        Button refreshButton = findViewById(R.id.refresh_button);
        refreshButton.setOnClickListener(v -> {
            Helpers.threadPolicy();
            GetLocksAsyncTask refreshAsyncTasks = new GetLocksAsyncTask(this, this);
            refreshAsyncTasks.execute();
        });

        // event handler for buttons on toolbar
        mToolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) item -> {
            String clickedTitle = (String) item.getTitle();
            switch (clickedTitle) {
                case "Share":
                    Intent shareLockIntent = new Intent(this, ShareListActivity.class);
                    this.startActivity(shareLockIntent);
                    break;
                case "Edit":
                    Intent editLockIntent = new Intent(this, LockActivity.class);
                    editLockIntent.putExtra(FORM_MODE, FORM_MODE_EDIT);
                    this.startActivity(editLockIntent);
                    break;
                case "View access log":
                    Intent logIntent = new Intent(this, LogActivity.class);
                    this.startActivity(logIntent);
                    break;
                default:
                    return true;
            }
            return false;
        });

        mToolbar.setNavigationOnClickListener(v -> {
            if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                return;
            }
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        // NOTE: COMMENT IT FOR EMULATOR
        new NfcHelper(HomeActivity.this);
    }

    private void onLockButtonClicked() {
        if (!isUnlocked) {
            // only call api if not unlocked
            GetLockAsyncTask getLockAsyncTasks = new GetLockAsyncTask(this, this, selectedLock.getId());
            getLockAsyncTasks.execute();
        } else {
            NfcServiceController.getInstance().stopNfc(HomeActivity.this);
        }

        // perform toggle
        isUnlocked = !isUnlocked;

        // sync states to UI
        updateUIBasedOnUnlockState();
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // turn off NFC before activity goes background
        if (isUnlocked) { onLockButtonClicked(); }
    }

    // when get locks completed
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onGetLocksCompleted(ArrayList<Lock> locks) {
        if(!lockList.isEmpty()){
            lockList.clear();
        }

        // set lock to recycler list view
        for (Lock lock : locks) {
            lockList.add(lock);
        }
        mRecyclerView = findViewById(R.id.drawer);
        mAdapter = new LockListAdapter(this, lockList);
        mRecyclerView.setAdapter(mAdapter);

        shareButton = mToolbar.getMenu().findItem(R.id.share_button);
        editLockButton = mToolbar.getMenu().findItem(R.id.edit_lock_button);
        viewAccessLogButton = mToolbar.getMenu().findItem(R.id.view_access_log_button);

        // set selected lock at initial
        if(lockList.isEmpty()){
            mToolbar.setTitle(R.string.no_lock);
            // hide all buttons on menu if no lock
            shareButton.setVisible(false);
            editLockButton.setVisible(false);
            viewAccessLogButton.setVisible(false);
        } else {
            mToolbar.setTitle(lockList.get(0).getName());
            selectedLock = lockList.get(0);
        }

        // update UI on selected lock change
        updateUIonLockChange();
    }

    // if selected lock changes from adapter -> update selected lock in home activity and corresponding UI (title and toolbar buttons)
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void updateUIonLockChange() {
        mAdapter.setOnDataChangeListener(lock -> {
            selectedLock = lock;
            mToolbar.setTitle(lock.getName());
            String permission = lock.getLockAccesses().stream().filter(access -> access.getUser().getName().equals(email)).findFirst().orElse(null).getPermission();
            switch(permission) {
                case "owner":
                    shareButton.setVisible(true);
                    editLockButton.setVisible(true);
                    viewAccessLogButton.setVisible(true);
                    break;
                case "manager":
                    shareButton.setVisible(true);
                    editLockButton.setVisible(false);
                    viewAccessLogButton.setVisible(false);
                    break;
                case "user":
                    shareButton.setVisible(false);
                    editLockButton.setVisible(false);
                    viewAccessLogButton.setVisible(false);
                    break;
                default:
                    break;
            }

            // after lock selection, collapse bottom sheet
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

            // if current is unlocked, put it back in locked mode
            if (isUnlocked) { onLockButtonClicked(); }
        });
    }

    // when get locks completed
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onLockTaskCompleted(Lock lock) {
        String token = lock.getToken();

        // turn on NFC and set token as message
        NfcServiceController.getInstance().enableNfcWithString(HomeActivity.this, token);
    }

    // sync UI with state
    private void updateUIBasedOnUnlockState() {
        ImageButton lockButton = findViewById(R.id.lock_button);
        TextView lockMessage = findViewById(R.id.lock_message);

        VectorChildFinder vector = new VectorChildFinder(this, R.drawable.lock_button_ring, lockButton);
        VectorDrawableCompat.VFullPath ring = vector.findPathByName("ring");
        ImageView lockIcon = findViewById(R.id.lock_icon);
        if (!isUnlocked) {
            Drawable openedLockIcon = ContextCompat.getDrawable(this, R.drawable.ic_outline_lock_24);
            lockIcon.setImageDrawable(openedLockIcon);
            ring.setFillColor(Color.parseColor(getString(R.string.ring_normal_color)));
            lockButton.invalidate();
            lockMessage.setText(R.string.tap_to_unlock);
        } else {
            Drawable closedLockIcon = ContextCompat.getDrawable(this, R.drawable.ic_outline_lock_open_24);
            lockIcon.setImageDrawable(closedLockIcon);
            ring.setFillColor(Color.parseColor(getString(R.string.ring_success_color)));
            lockMessage.setText(R.string.unlocked);
        }
    }

    private void logout() {
        auth.signOut();
        SharedPreferences.Editor editor = pref.edit();
        editor.remove(AUTH_EMAIL);
        editor.commit();
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }
}