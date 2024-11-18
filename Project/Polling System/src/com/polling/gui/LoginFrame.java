package com.polling.gui;

import com.polling.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFrame extends JFrame {
    private JTextField usernameField; // Username input
    private JPasswordField passwordField; // Password input
    private JButton loginButton; // Login button
    private JButton registerButton; // Register button

    public LoginFrame() {
        setTitle("Polling System - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create a gradient panel with orange and yellow colors
        GradientPanel gradientPanel = new GradientPanel(Color.ORANGE, Color.YELLOW);
        setContentPane(gradientPanel);
        gradientPanel.setLayout(new GridLayout(3, 2));

        usernameField = new JTextField(); // Initialize username field
        passwordField = new JPasswordField(); // Initialize password field
        loginButton = new JButton("Login"); // Initialize login button
        registerButton = new JButton("Register"); // Initialize register button

        gradientPanel.add(new JLabel("Username:")); // Label for username field
        gradientPanel.add(usernameField);
        gradientPanel.add(new JLabel("Password:")); // Label for password field
        gradientPanel.add(passwordField);
        gradientPanel.add(loginButton); // Add login button
        gradientPanel.add(registerButton); // Add register button

        loginButton.addActionListener(new LoginAction()); // Action listener for login button
        registerButton.addActionListener(new RegisterAction()); // Action listener for register button

        setVisible(true); // Make frame visible
    }

    private class LoginAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String username = usernameField.getText(); // Get username input
            String password = new String(passwordField.getPassword()); // Get password input

            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT aadhaar_number, has_voted, is_admin FROM users WHERE username = ? AND password = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, username); // Use username for login
                statement.setString(2, password); // Use password for login
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String aadhaarNumber = resultSet.getString("aadhaar_number"); // Fetch Aadhaar number
                    boolean hasVoted = resultSet.getBoolean("has_voted"); // Fetch voting status
                    boolean isAdmin = resultSet.getBoolean("is_admin"); // Fetch admin status
                    
                    new VotingFrame(aadhaarNumber, hasVoted, isAdmin); // Pass Aadhaar number and voting status to VotingFrame
                    dispose(); // Close the login frame
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid credentials!"); // Show error message for invalid credentials
                }
            } catch (SQLException ex) {
                ex.printStackTrace(); // Print stack trace for SQL exceptions
            }
        }
    }

    private class RegisterAction implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            new RegistrationFrame(); // Open the registration frame
            dispose(); // Close the login frame
        }
    }
}