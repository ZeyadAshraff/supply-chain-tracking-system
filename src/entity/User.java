package entity;

public abstract class User {
    private final String userId;
    private String name;
    private final String email;
    private String password;
    private boolean active;

    protected User(String userId, String name, String email, String password, boolean active) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.password = password;
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isActive() {
        return active;
    }

    public void activate() {
        this.active = true;
    }

    public void deactivate() {
        this.active = false;
    }

    public void assertRole(Role requiredRole, String message) {
        if (getRole() != requiredRole) {
            throw new IllegalArgumentException(message);
        }
    }

    public abstract Role getRole();
}
