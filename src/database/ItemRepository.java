package database;

import entity.Item;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ItemRepository {
    private final DBConnection dbConnection;

    public ItemRepository(DBConnection dbConnection) {
        this.dbConnection = dbConnection;
    }

    public void addItem(Item item) {
        String sql = "INSERT INTO items(item_id, good_id, quantity, manufacture_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = dbConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, item.getItemId());
            ps.setString(2, item.getGood().getGoodId());
            ps.setInt(3, item.getOnHandQuantity());
            ps.setDate(4, Date.valueOf(item.getOccurrenceDate()));
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to add item", ex);
        }
    }
}
