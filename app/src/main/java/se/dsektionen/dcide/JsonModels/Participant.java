package se.dsektionen.dcide.JsonModels;

public class Participant {

    private int id;
    private User user;

    private String special_diet;
    private String drink_preference;
    private String group_name;
    private String other_info;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getSpecial_diet() {
        return special_diet;
    }

    public void setSpecial_diet(String special_diet) {
        this.special_diet = special_diet;
    }

    public String getDrink_preference() {
        return drink_preference;
    }

    public void setDrink_preference(String drink_preference) {
        this.drink_preference = drink_preference;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getOther_info() {
        return other_info;
    }

    public void setOther_info(String other_info) {
        this.other_info = other_info;
    }
}
