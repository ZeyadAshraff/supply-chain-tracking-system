package boundary;

import javax.swing.JFrame;

public final class Navigation {
    private Navigation() {
    }

    public static void openChild(JFrame parent, JFrame child) {
        parent.setVisible(false);
        child.setVisible(true);
    }

    public static void goBack(JFrame current, JFrame parent) {
        parent.setVisible(true);
        current.dispose();
    }
}

