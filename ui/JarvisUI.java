package ui;

import core.Router;
import utils.ResponseFormatter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JarvisUI {

    private Router router;

    private JFrame frame;
    private JTextArea chatArea;
    private JTextArea inputField;
    private JButton sendButton;

    public JarvisUI() {
        router = new Router();
        initialize();
    }

    private void initialize() {

        frame = new JFrame("JARVIS");
        frame.setSize(900, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // ===== CHAT AREA =====
        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true);
        chatArea.setWrapStyleWord(true);
        chatArea.setFont(new Font("Consolas", Font.PLAIN, 17));
        chatArea.setBackground(new Color(18, 18, 18));
        chatArea.setForeground(new Color(220, 220, 220));
        chatArea.setBorder(new EmptyBorder(15, 15, 15, 15));

        JScrollPane scrollPane = new JScrollPane(chatArea);
        scrollPane.setBorder(null);

        // ===== INPUT AREA =====
        inputField = new JTextArea(2, 20);
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setFont(new Font("Consolas", Font.PLAIN, 16));
        inputField.setBackground(new Color(45, 45, 45));
        inputField.setForeground(Color.WHITE);
        inputField.setCaretColor(Color.WHITE);
        inputField.setBorder(new EmptyBorder(10, 10, 10, 10));

        // SHIFT+ENTER = newline, ENTER = send
        inputField.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_ENTER && e.isShiftDown()) {
                    inputField.append("\n");
                    e.consume();
                }

                else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    e.consume();
                    sendMessage();
                }
            }
        });

        JScrollPane inputScroll = new JScrollPane(inputField);
        inputScroll.setPreferredSize(new Dimension(100, 80));

        sendButton = new JButton("Send");
        sendButton.setBackground(new Color(0, 122, 255));
        sendButton.setForeground(Color.WHITE);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        panel.add(inputScroll, BorderLayout.CENTER);
        panel.add(sendButton, BorderLayout.EAST);

        frame.add(scrollPane, BorderLayout.CENTER);
        frame.add(panel, BorderLayout.SOUTH);

        sendButton.addActionListener(e -> sendMessage());

        frame.setVisible(true);
    }

    private void sendMessage() {

        String userInput = inputField.getText().trim();
        if (userInput.isEmpty())
            return;

        appendUserMessage(userInput);
        inputField.setText("");

        new Thread(() -> {

            String response = router.handle(userInput);
            String clean = ResponseFormatter.format(response);

            SwingUtilities.invokeLater(() -> {
                appendJarvisMessage(clean);
            });

        }).start();
    }

    private void appendUserMessage(String msg) {
        chatArea.append("\nYou:\n" + msg + "\n\n");
    }

    private void appendJarvisMessage(String msg) {
        chatArea.append("JARVIS:\n" + msg + "\n\n");
        chatArea.setCaretPosition(chatArea.getDocument().getLength());
    }
}