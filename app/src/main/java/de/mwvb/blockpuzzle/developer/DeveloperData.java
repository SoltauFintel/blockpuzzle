package de.mwvb.blockpuzzle.developer;

public class DeveloperData {
    private String today;

    public String getToday() {
        return today;
    }

    public void setToday(String today) {
        this.today = today;
    }

    public static DeveloperData get() {
        return new DeveloperDataDAO().load();
    }

    public void save() {
        new DeveloperDataDAO().save(this);
    }
}
