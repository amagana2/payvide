package maganacode.payvide.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 12/18/2016.
 * The users to be displayed for searching. This will be written into the database.
 */

@IgnoreExtraProperties
public class UserList implements Serializable {
    private String email;
    private String name;
    private String uid;
    private String username;
    private boolean isSelected = false;

    //Empty constructor for Firebase
    public UserList() {
    }

    //Constructor
    public UserList(String email, String name, String username) {
        this.email = email;
        this.name = name;
        this.username = username;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("uid", uid);
        result.put("username", username);

        return result;
    }

    //Getters and Setters
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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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
