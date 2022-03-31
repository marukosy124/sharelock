package edu.cuhk.csci3310.sharelock.apis.interfaces;

import java.util.ArrayList;

import edu.cuhk.csci3310.sharelock.classes.Lock;

public interface OnGetLocksCompleted {
    void onGetLocksCompleted(ArrayList<Lock> locks);
}
