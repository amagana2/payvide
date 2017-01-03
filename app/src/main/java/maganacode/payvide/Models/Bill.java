package maganacode.payvide.Models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Andrew on 12/23/2016.
 * This will be the actual bill which the group will be assigned.
 */

class Bill {
    private String name;
    private String uid;
    private Date date;
    private Double cost;
    private String color;
    private String recurrence;
    private ArrayList<Renter> userPercentage;

    public Bill() {
    }

    public Bill(String name, String uid, Date date, Double cost, String color, String recurrence, ArrayList<Renter> userPercentage) {
        this.name = name;
        this.uid = uid;
        this.date = date;
        this.cost = cost;
        this.color = color;
        this.recurrence = recurrence;
        this.userPercentage = userPercentage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getRecurrence() {
        return recurrence;
    }

    public void setRecurrence(String recurrence) {
        this.recurrence = recurrence;
    }

    public ArrayList<Renter> getUserPercentage() {
        return userPercentage;
    }

    public void setUserPercentage(ArrayList<Renter> userPercentage) {
        this.userPercentage = userPercentage;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
