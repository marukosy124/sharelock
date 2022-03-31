package edu.cuhk.csci3310.sharelock.adapters;

import static android.content.Context.MODE_PRIVATE;
import static edu.cuhk.csci3310.sharelock.globals.Constants.AUTH_EMAIL;
import static edu.cuhk.csci3310.sharelock.globals.Constants.LOCK_ID;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SELECTED_LOCK;
import static edu.cuhk.csci3310.sharelock.globals.Constants.SHARED_PREF_FILE;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.Toolbar;

import java.util.ArrayList;
import java.util.LinkedList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.google.gson.Gson;

import edu.cuhk.csci3310.sharelock.R;
import edu.cuhk.csci3310.sharelock.classes.Lock;

public class LockListAdapter extends Adapter<LockListAdapter.LockViewHolder>  {
    private static final String TAG = "LockListAdapter";
    private final Context context;
    private final LayoutInflater mInflater;
    private int lastCheckedPosition = 0;
    private String selectedLock;
    private ArrayList<Lock> lockList;

    class LockViewHolder extends RecyclerView.ViewHolder {
        final LockListAdapter mAdapter;
        public RadioButton lockRadioButton;

        public LockViewHolder(View itemView, LockListAdapter adapter) {
            super(itemView);
            lockRadioButton = itemView.findViewById(R.id.lock_radio_button);
            this.mAdapter = adapter;
            lockRadioButton.setOnClickListener(v -> {
                // update view holder UI
                int copyOfLastCheckedPosition = lastCheckedPosition;
                lastCheckedPosition = getAdapterPosition();
                notifyItemChanged(copyOfLastCheckedPosition);
                notifyItemChanged(lastCheckedPosition);

                saveSelectedLock();
            });
        }
    }

    public LockListAdapter(Context context, ArrayList<Lock> lockList) {
        mInflater = LayoutInflater.from(context);
        this.lockList = lockList;
        this.context = context;
    }

    @NonNull
    @Override
    public LockViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.locklist_item, parent, false);
        return new LockViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LockViewHolder holder, int position) {
        String lockName = lockList.get(position).getName();
        holder.lockRadioButton.setText(lockName);
        holder.lockRadioButton.setChecked(position == lastCheckedPosition);
        saveSelectedLock();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return lockList.size();
    }

    // save selected lock in preferences for edit activity to use
    private void saveSelectedLock() {
        Lock currentLock = lockList.get((lastCheckedPosition));
        Gson gson = new Gson();
        Lock lock = new Lock(currentLock.getId(), currentLock.getName(), currentLock.getLockAccesses(), "");
        selectedLock = gson.toJson(lock);
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREF_FILE, MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(SELECTED_LOCK, selectedLock);
        editor.commit();
        selectLock(currentLock);
    }

    public interface OnLockChangeListener{
        public void onDataChanged(Lock selectedLock);
    }

    OnLockChangeListener mOnLockChangeListener;
    public void setOnDataChangeListener(OnLockChangeListener onLockChangeListener){
        mOnLockChangeListener = onLockChangeListener;
    }

    private void selectLock(Lock selectedLock) {
        if(mOnLockChangeListener != null){
            mOnLockChangeListener.onDataChanged(selectedLock);
        }
    }


}