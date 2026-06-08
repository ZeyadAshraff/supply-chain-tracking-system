package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import entity.events.Event;

public class Admin extends User {
    private String adminLevel;

    public Admin(String userId, String name, String email, String password, boolean active, String adminLevel) {
        super(userId, name, email, password, active);
        this.adminLevel = adminLevel;
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }

    public String getAdminLevel() {
        return adminLevel;
    }

    public void setAdminLevel(String adminLevel) {
        this.adminLevel = adminLevel;
    }

    public List<Event> viewAuditInformation(List<Event> events) {
        return Collections.unmodifiableList(new ArrayList<>(events));
    }
}
