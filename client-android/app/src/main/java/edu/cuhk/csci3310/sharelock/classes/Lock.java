package edu.cuhk.csci3310.sharelock.classes;

import java.util.ArrayList;

public class Lock {
    private int id;
    private String name;
    private ArrayList<LockAccess> lock_accesses;
    private String token;

    public Lock(int id, String name, ArrayList<LockAccess> lock_accesses, String token) {
        this.id = id;
        this.name = name;
        this.lock_accesses = lock_accesses;
        this.token = token;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<LockAccess> getLockAccesses() {
        return lock_accesses;
    }

    public void setLockAccesses(ArrayList<LockAccess> lock_accesses) {
        this.lock_accesses = lock_accesses;
    }

    public String getToken() { return token; }

    public void setToken(String token) { this.token = token; }
}
