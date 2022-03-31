package edu.cuhk.csci3310.sharelock.classes;

public class LockAccess {
    private int id;
    private User user;
    private String permission;
    private String expired_at;

    public LockAccess(int id, User user, String permission, String expired_at) {
        this.id = id;
        this.user = user;
        this.permission = permission;
        this.expired_at = expired_at;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPermission() {
        return permission;
    }

    public void setPermission(String permission) {
        this.permission = permission;
    }

    public String getExpiredAt() {
        return expired_at;
    }

    public void setExpiredAt(String expired_at) {
        this.expired_at = expired_at;
    }
}
