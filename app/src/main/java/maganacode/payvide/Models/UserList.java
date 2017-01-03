package maganacode.payvide.Models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by Andrew on 12/18/2016.
 * The users to be displayed for searching. This will be written into the database.
 */

@IgnoreExtraProperties
public class UserList implements Serializable {
    public String email;
    public String name;
    public String username;
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

    public String getEmail() {
        return email;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
