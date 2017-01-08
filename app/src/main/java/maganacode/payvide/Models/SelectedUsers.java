package maganacode.payvide.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Andrew on 1/6/2017.
 */

public class SelectedUsers {
    String groupName;
    String groupUID;
    String admin;
    List<UserList> selectedUsers;

    public SelectedUsers() {

    }

    public SelectedUsers(String groupName, String groupUID, List<UserList> selectedUsers) {
        this.groupName = groupName;
        this.groupUID = groupUID;
        this.selectedUsers = selectedUsers;
    }

    @Exclude
    public Map<String, Object> toMap() {

        HashMap<String, Object> result = new HashMap<>();
        result.put("admin", admin);
        result.put("members", selectedUsers);
        result.put("name", groupName);
        result.put("uid", groupUID);
        return result;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupUID() {
        return groupUID;
    }

    public void setGroupUID(String groupUID) {
        this.groupUID = groupUID;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }

    public List<UserList> getSelectedUsers() {
        return selectedUsers;
    }

    public void setSelectedUsers(List<UserList> selectedUsers) {
        this.selectedUsers = selectedUsers;
    }
}
