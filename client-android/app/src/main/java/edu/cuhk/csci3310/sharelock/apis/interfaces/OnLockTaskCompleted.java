package edu.cuhk.csci3310.sharelock.apis.interfaces;

import edu.cuhk.csci3310.sharelock.classes.Lock;

public interface OnLockTaskCompleted {
    void onLockTaskCompleted(Lock newLock);
}
