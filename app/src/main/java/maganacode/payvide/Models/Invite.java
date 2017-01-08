package maganacode.payvide.Models;

/**
 * Created by Andrew on 1/3/2017.
 * Invitation of the group.
 */

public class Invite {
    private String uid;
    private User admin;
    private boolean joined;
    private String name;

    public Invite() {

    }

    public Invite(String uid, User admin, boolean joined, String name) {
        this.uid = uid;
        this.admin = admin;
        joined = false;
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public User getAdmin() {
        return admin;
    }

    public void setAdmin(User admin) {
        this.admin = admin;
    }

    public boolean isJoined() {
        return joined;
    }

    public void setJoined(boolean joined) {
        this.joined = joined;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
