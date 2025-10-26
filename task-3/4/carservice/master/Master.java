package carservice.master;

import carservice.IHasId;

public class Master implements IHasId {
    private static int lastId = 0;
    private final int id;
    private final String fullname;
    private String phone;

    Master(String fullname, String phone) {
        this.id = getNextId();
        this.fullname = fullname;
        this.phone = phone;
    }

    private int getNextId() {
        return ++lastId;
    }

    public int getId() {
        return id;
    }

    public String getFullname() {
        return fullname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
