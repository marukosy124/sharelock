package edu.cuhk.csci3310.sharelock.adapters;

import static android.content.Context.MODE_PRIVATE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.FORM_MODE_EDIT;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ACCESS;
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
import edu.cuhk.csci3310.sharelock.classes.LockHistory;
import edu.cuhk.csci3310.sharelock.globals.Helpers;

public class LogListAdapter extends Adapter<LogListAdapter.LogViewHolder>  {
    private static final String TAG = "LogListAdapter";
    private Context context;
    private final LayoutInflater mInflater;
    private ArrayList<LockHistory> logList;

    class LogViewHolder extends RecyclerView.ViewHolder {
        final LogListAdapter mAdapter;
        public TextView emailTextView;
        public TextView accessTimeTextView;

        public LogViewHolder(View itemView, LogListAdapter adapter) {
            super(itemView);
            this.mAdapter = adapter;
            context = itemView.getContext();
            emailTextView = itemView.findViewById(R.id.email);
            accessTimeTextView = itemView.findViewById(R.id.access_time);
        }
    }

    public LogListAdapter(Context context, ArrayList<LockHistory> logList) {
        mInflater = LayoutInflater.from(context);
        this.logList = logList;
        this.context = context;
    }

    @NonNull
    @Override
    public LogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.log_item, parent, false);
        return new LogViewHolder(mItemView, this);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull LogViewHolder holder, int position) {
        String email = logList.get(position).getUser().getName();
        String accessedAt = logList.get(position).getAccessedAt();
        holder.emailTextView.setText(email);
        holder.accessTimeTextView.setText("Access Time: " + Helpers.formatISOToDisplay(accessedAt));
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return logList.size();
    }

}