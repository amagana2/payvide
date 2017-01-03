package maganacode.payvide.Models;

/**
 * Created by Andrew on 12/23/2016.
 * This is the renter, or the person being charged.
 */

class Renter {

    private User user;
    private int percent;

    public Renter(){

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public Renter(User user, int percent) {

        this.user = user;
        this.percent = percent;
    }
}
