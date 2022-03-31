package edu.cuhk.csci3310.sharelock.classes;

public class LockHistory {
    private int id;
    private User user;
    private String accessed_at;

    public LockHistory(int id, User user, String accessed_at) {
        this.id = id;
        this.user = user;
        this.accessed_at = accessed_at;
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

    public String getAccessedAt() {
        return accessed_at;
    }

    public void setAccessedAt(String accessed_at) {
        this.accessed_at = accessed_at;
    }

}
