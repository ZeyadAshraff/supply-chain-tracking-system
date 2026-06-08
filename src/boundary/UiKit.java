package boundary;

import java.awt.Color;
import java.awt.Font;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public final class UiKit {
    public static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 15);
    public static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 14);

    private UiKit() {
    }

    public static JPanel paddedPanel() {
        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        return panel;
    }

    public static JLabel title(String text) {
        JLabel label = new JLabel(text);
        label.setFont(TITLE_FONT);
        return label;
    }

    public static JLabel subtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(SUBTITLE_FONT);
        label.setForeground(new Color(70, 70, 70));
        return label;
    }

    public static JButton button(String text) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setMargin(new Insets(10, 16, 10, 16));
        return button;
    }

    public static void styleReadOnlyArea(JTextArea area) {
        area.setEditable(false);
        area.setFont(FIELD_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
    }
}

