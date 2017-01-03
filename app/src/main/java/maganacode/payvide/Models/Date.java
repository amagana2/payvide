package maganacode.payvide.Models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Andrew on 12/27/2016.
 * Date object to be used for recurrence.
 */

@IgnoreExtraProperties
public class Date {
    private String day;
    private String month;
    private String year;

    public Date() {

    }

    public Date(String day, String month, String year) {
        this.day = day;
        this.month = month;
        this.year = year;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("day", day);
        result.put("month", month);
        result.put("year", year);
        return result;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }
}

