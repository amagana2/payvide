package maganacode.payvide.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 12/16/2016.
 * The user object. (For Registration & Login)
 */

@IgnoreExtraProperties
public class User {
    private String email;
    private String name;
    private String username;
    private String profilePicture;
    private String uid;

    public User() {

    }

    public User(String email, String name, String username, String uid, String profilePicture) {
        this.email = email;
        this.name = name;
        this.username = username;
        this.uid = uid;
        this.profilePicture = profilePicture;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("name", name);
        result.put("profilePicture", profilePicture);
        result.put("uid", uid);
        result.put("username", username);

        return result;
    }
}
