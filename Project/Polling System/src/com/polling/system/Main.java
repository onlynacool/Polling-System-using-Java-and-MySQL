package com.polling.system;

import com.polling.gui.LoginFrame;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame(); // Start with LoginFrame
            }
        });
    }
}