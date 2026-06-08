package core;

import boundary.LoginUI;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        try {
            AppContext context = new AppContext();
            MockDataSeeder.seed(context);
            SwingUtilities.invokeLater(() -> new LoginUI(context).setVisible(true));
        } catch (Exception ex) {
            String details = ex.getMessage();
            if (ex.getCause() != null && ex.getCause().getMessage() != null) {
                details = details + "\nCause: " + ex.getCause().getMessage();
            }
            JOptionPane.showMessageDialog(null,
                    "Startup failed. Make sure MySQL is running and schema.sql has been executed.\n" + details,
                    "SCTS Startup Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}
