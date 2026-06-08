package entity;

public class Retailer extends User {
    private String organizationName;
    private String location;

    public Retailer(String userId, String name, String email, String password, boolean active,
                    String organizationName, String location) {
        super(userId, name, email, password, active);
        this.organizationName = organizationName;
        this.location = location;
    }

    @Override
    public Role getRole() {
        return Role.RETAILER;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }
}
