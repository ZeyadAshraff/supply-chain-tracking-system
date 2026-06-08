package entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manufacturer extends User {
    private String companyName;
    private final List<Good> goods;

    public Manufacturer(String userId, String name, String email, String password, boolean active, String companyName) {
        super(userId, name, email, password, active);
        this.companyName = companyName;
        this.goods = new ArrayList<>();
    }

    @Override
    public Role getRole() {
        return Role.MANUFACTURER;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public void addGood(Good good) {
        goods.add(good);
    }

    public void removeGood(Good good) {
        goods.remove(good);
    }

    public List<Good> getGoods() {
        return Collections.unmodifiableList(goods);
    }

    public void assertCanManageGoodsAs(User actor) {
        if (actor.getRole() == Role.MANUFACTURER
                && actor.getUserId().equalsIgnoreCase(getUserId())) {
            return;
        }
        throw new IllegalArgumentException("You are not allowed to manage goods for this manufacturer.");
    }
}
