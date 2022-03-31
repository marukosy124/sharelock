package edu.cuhk.csci3310.sharelock.apis.interfaces;

import java.util.ArrayList;

import edu.cuhk.csci3310.sharelock.classes.Lock;
import edu.cuhk.csci3310.sharelock.classes.LockHistory;

public interface OnGetLockHistoryCompleted {
    void onGetLockHistoryCompleted(ArrayList<LockHistory> logs);
}
