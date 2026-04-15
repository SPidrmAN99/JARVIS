package ui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class InputBox extends JTextArea {

    public InputBox() {

        setLineWrap(true);
        setWrapStyleWord(true);
        setFont(new Font("Consolas", Font.PLAIN, 16));
        setBackground(new Color(45, 45, 45));
        setForeground(Color.WHITE);

        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                // SHIFT + ENTER → new line
                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isShiftDown()) {
                    append("\n");
                    e.consume();
                }

                // ENTER → send
                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    firePropertyChange("SEND", false, true);
                }
            }
        });
    }
}