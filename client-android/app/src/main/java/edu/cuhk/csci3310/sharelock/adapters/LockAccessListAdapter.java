package edu.cuhk.csci3310.sharelock.adapters;

import static android.content.Context.MODE_PRIVATE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_EDIT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_NAME;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SELECTED_LOCK;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SELECTED_LOCK_ACCESS;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SHARED_PREF_FILE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.google.gson.Gson;

import java.util.ArrayList;

import edu.cuhk.csci3310.sharelock.R;
import edu.cuhk.csci3310.sharelock.activities.ShareActivity;
import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockAccess;
import edu.cuhk.csci3310.sharelock.globals.Helpers;

public class LockAccessListAdapter extends Adapter<LockAccessListAdapter.LockAccessViewHolder>  {
    private static final String TAG = "LockAccessListAdapter";
    private Context context;
    private final LayoutInflater mInflater;
    private ArrayList<LockAccess> lockAccessList;

    class LockAccessViewHolder extends RecyclerView.ViewHolder {
        final LockAccessListAdapter mAdapter;
        public TextView roleTextView;
        public TextView emailTextView;
        public TextView expiryTimeTextView;

        public LockAccessViewHolder(View itemView, LockAccessListAdapter adapter) {
            super(itemView);
            this.mAdapter = adapter;
            context = itemView.getContext();
            roleTextView = itemView.findViewById(R.id.role);
            emailTextView = itemView.findViewById(R.id.email);
            expiryTimeTextView = itemView.findViewById(R.id.expiry_time);
        }
    }

    public LockAccessListAdapter(Context context, ArrayList<LockAccess> lockAccessList) {
        mInflater = LayoutInflater.from(context);
        this.lockAccessList = lockAccessList;
        this.context = context;
    }

    @NonNull
    @Override
    public LockAccessViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.sharelist_item, parent, false);
        return new LockAccessViewHolder(mItemView, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull LockAccessViewHolder holder, int position) {
        String email = lockAccessList.get(position).getUser().getName();
        String role = lockAccessList.get(position).getPermission();
        String expiredAt = lockAccessList.get(position).getExpiredAt();
        holder.emailTextView.setText(email);
        holder.roleTextView.setText(role);
        holder.expiryTimeTextView.setText("Expiry Time: " + Helpers.formatISOToDisplay(expiredAt));
        holder.itemView.setOnClickListener(v -> {
            Bundle lockAccessData = new Bundle();
            SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
            String selectedLock = pref.getString(SELECTED_LOCK, "");
            Gson gson = new Gson();
            Lock lock = gson.fromJson(selectedLock, Lock.class);
            lockAccessData.putInt(LOCK_ID, lock.getId());
            lockAccessData.putString(LOCK_NAME, lock.getName());
            lockAccessData.putString(LOCK_ACCESS, gson.toJson(lockAccessList.get(position)));
            Intent editAccessIntent = new Intent(context, ShareActivity.class);
            editAccessIntent.putExtra(FORM_MODE, FORM_MODE_EDIT);
            editAccessIntent.putExtra(SELECTED_LOCK_ACCESS, lockAccessData);
            context.startActivity(editAccessIntent);
        });
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return lockAccessList.size();
    }

}