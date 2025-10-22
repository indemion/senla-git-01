public class Master {
    private final String fullname;
    private String phone;

    public Master(String fullname, String phone) {
        this.fullname = fullname;
        this.phone = phone;
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
