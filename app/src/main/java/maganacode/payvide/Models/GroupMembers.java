package maganacode.payvide.Models;

/**
 * Created by Andrew on 12/23/2016.
 * Group Members that have been selected.
 */

public class GroupMembers {
    private String name;
    private String username;
    private String email;
    private String uid;

    public GroupMembers() {
    }

    public GroupMembers(String name, String username, String email, String uid) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
