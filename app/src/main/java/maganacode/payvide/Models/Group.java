package maganacode.payvide.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrew on 12/23/2016.
 * The group which are picked by the user who is logged in, or the "admin".
 */

@IgnoreExtraProperties
public class Group {
    private String name; //groupName;
    private String uid; //unique identifier for group.
    private List<GroupMembers> members; //the members of group
    private ArrayList<Bill> bills;

    public Group() {

    }

    public Group(List<GroupMembers> members, String name, String uid) {
        this.name = name;
        this.uid = uid;
        this.members = members;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("members", members);
        result.put("name", name);
        result.put("uid", uid);

        return result;
    }

    public List<GroupMembers> getMembers() {
        return members;
    }

    public void setMembers(List<GroupMembers> members) {
        this.members = members;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
