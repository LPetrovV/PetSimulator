/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package petsimulator;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * A message box that floats over the bottom left corner of the window.
 *
 * It is added to the frame's layered pane instead of the BorderLayout, so it
 * sits on top of whatever scene is showing without changing that scene's
 * layout. Contains no game logic: the controller decides what to display.
 *
 * @author petro
 */
public class GameMessageGUI {

    private final JFrame frame;
    private final JLayeredPane layeredPane;

    private final JPanel messagePanel = new JPanel(new BorderLayout(10, 0));
    private final JLabel messageLabel = new JLabel();
    private final JButton closeButton = new JButton("x");

    private javax.swing.Timer hideTimer;

    //sizing
    private final int margin = 20;     // gap from the left and bottom edges
    private final int textWidth = 260; // text wraps at this width

    private final Color UIBgColor = new Color(0xDFF0FF);
    private final Color borderColor = new Color(0x4C9FFF);

    public GameMessageGUI(JFrame frame) {
        this.frame = frame;
        this.layeredPane = frame.getLayeredPane();

        //message label
        messageLabel.setForeground(Color.DARK_GRAY);

        //close button
        closeButton.setFocusable(false);
        closeButton.setMargin(new Insets(0, 6, 0, 6));
        closeButton.addActionListener(e -> hideMessage());

        //panel
        messagePanel.setBackground(UIBgColor);
        messagePanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)));
        messagePanel.add(messageLabel, BorderLayout.CENTER);
        messagePanel.add(closeButton, BorderLayout.EAST);
        messagePanel.setVisible(false);

        //POPUP_LAYER is above the normal content, so it draws over the scene
        layeredPane.add(messagePanel, JLayeredPane.POPUP_LAYER);

        //keep the box in the bottom left corner when the window is resized
        layeredPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                reposition();
            }
        });
    }

    // ------------------------------------------------------------------
    // Used by the controller
    // ------------------------------------------------------------------

    /** Shows any message and keeps it up until hideMessage() or the x button. */
    public void showMessage(String message) {
        stopTimer();

        messageLabel.setText(formatMessage(message));
        messagePanel.setSize(messagePanel.getPreferredSize());
        reposition();
        messagePanel.setVisible(true);

        //make sure it is drawn above anything added after it
        layeredPane.moveToFront(messagePanel);
    }

    /** Shows a message and hides it automatically after durationMs milliseconds. */
    public void showMessage(String message, int durationMs) {
        showMessage(message);

        hideTimer = new javax.swing.Timer(durationMs, e -> hideMessage());
        hideTimer.setRepeats(false);
        hideTimer.start();
    }

    public void hideMessage() {
        stopTimer();
        messagePanel.setVisible(false);
    }

    /** Removes the message box from the frame completely. */
    public void remove() {
        hideMessage();
        layeredPane.remove(messagePanel);
        layeredPane.repaint();
    }

    // ------------------------------------------------------------------
    // Internal helpers
    // ------------------------------------------------------------------

    private void reposition() {
        Dimension size = messagePanel.getSize();
        int y = layeredPane.getHeight() - size.height - margin;
        messagePanel.setLocation(margin, Math.max(0, y));
    }

    private void stopTimer() {
        if (hideTimer != null) {
            hideTimer.stop();
            hideTimer = null;
        }
    }

    //Wraps long text at textWidth and stops message text being read as HTML
    private String formatMessage(String message) {
        String safe = message
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\n", "<br>");
        return "<html><body style='width: " + textWidth + "px'>" + safe + "</body></html>";
    }

}
