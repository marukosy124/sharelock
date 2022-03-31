package edu.cuhk.csci3310.sharelock.apis.interfaces;

import edu.cuhk.csci3310.sharelock.classes.LockAccess;

public interface OnLockAccessTaskCompleted {
    void onLockAccessTaskCompleted(LockAccess newLockAccess);
}
