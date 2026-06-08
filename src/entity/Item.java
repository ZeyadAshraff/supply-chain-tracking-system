package entity;

import java.time.LocalDate;

public class Item {
    private final String itemId;
    private int quantity;
    private LocalDate manufactureDate;
    private final Good good;

    public Item(String itemId, int quantity, LocalDate manufactureDate, Good good) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.manufactureDate = manufactureDate;
        this.good = good;
    }

    public String getItemId() {
        return itemId;
    }

    // Compatibility getter used by existing UI/repositories.
    public int getQuantity() {
        return quantity;
    }

    // Occurrence-oriented alias.
    public int getOnHandQuantity() {
        return quantity;
    }

    // Compatibility getter used by existing UI/repositories.
    public LocalDate getManufactureDate() {
        return manufactureDate;
    }

    // Occurrence-oriented alias.
    public LocalDate getOccurrenceDate() {
        return manufactureDate;
    }

    public Good getGood() {
        return good;
    }

    public static void assertPositiveQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero.");
        }
    }

    public void increaseQuantity(int amount) {
        assertPositiveQuantity(amount);
        quantity += amount;
    }

    public void increaseOnHand(int amount) {
        increaseQuantity(amount);
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0 || amount > quantity) {
            throw new IllegalArgumentException("Invalid amount");
        }
        quantity -= amount;
    }

    public void decreaseOnHand(int amount) {
        decreaseQuantity(amount);
    }

    public boolean belongsToGood(String goodId) {
        return goodId != null
                && good != null
                && good.getGoodId().equalsIgnoreCase(goodId);
    }
}
