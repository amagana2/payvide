package maganacode.payvide.Models;

import java.io.Serializable;

/**
 * Created by Andrew on 12/23/2016.
 * Group Members that have been selected.
 */

public class GroupMembers implements Serializable {
    private String name;
    private String username;
    private String email;
    private String uid;
    private int percentage;
    private double amount;

    public GroupMembers() {
    }

    public GroupMembers(String name, String username, String email, String uid) {
        this.name = name;
        this.username = username;
        this.email = email;
        this.uid = uid;
        percentage = 0;
        amount = 0;
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

    public int getPercentage() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
