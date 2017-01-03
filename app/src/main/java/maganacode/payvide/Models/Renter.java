package maganacode.payvide.Models;

/**
 * Created by Andrew on 12/23/2016.
 * This is the renter, or the person being charged.
 */

class Renter {

    private GroupMembers GroupMember;
    private int percent;
    private double amount;

    public Renter() {

    }

    public Renter(GroupMembers groupMember, int percent, double amount) {
        GroupMember = groupMember;
        this.percent = percent;
        this.amount = amount;
    }

    public GroupMembers getGroupMember() {
        return GroupMember;
    }

    public void setGroupMember(GroupMembers groupMember) {
        GroupMember = groupMember;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
