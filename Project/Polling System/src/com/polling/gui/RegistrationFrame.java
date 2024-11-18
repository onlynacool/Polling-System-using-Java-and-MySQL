package com.polling.gui;

import com.polling.database.DatabaseConnection;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistrationFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField aadhaarField; // Aadhaar number field
    private JButton registerButton;
    private JCheckBox isAdminCheckBox;

    public RegistrationFrame() {
        setTitle("Polling System - Register");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Create a gradient panel with orange and yellow colors
        GradientPanel gradientPanel = new GradientPanel(Color.ORANGE, Color.YELLOW);
        setContentPane(gradientPanel);
        gradientPanel.setLayout(new GridLayout(6, 2)); // Adjusted for 6 rows

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        aadhaarField = new JTextField(); // Initialize Aadhaar field
        registerButton = new JButton("Register");
        isAdminCheckBox = new JCheckBox("Admin");

        gradientPanel.add(new JLabel("Username:"));
        gradientPanel.add(usernameField);
        gradientPanel.add(new JLabel("Password:"));
        gradientPanel.add(passwordField);
        gradientPanel.add(new JLabel("Aadhaar Number (XXXX-XXXX-XXXX):")); // Label for Aadhaar field
        gradientPanel.add(aadhaarField); // Add Aadhaar field to layout
        gradientPanel.add(isAdminCheckBox);
        gradientPanel.add(registerButton);

        registerButton.addActionListener(new RegisterAction());

		// Key listener to allow only numeric input in Aadhaar field
		aadhaarField.addKeyListener(new java.awt.event.KeyAdapter() {
			public void keyTyped(java.awt.event.KeyEvent evt) {
				char c = evt.getKeyChar();
				if (!Character.isDigit(c) || aadhaarField.getText().length() >= 14) {
					evt.consume(); // Ignore if not a digit or exceeds length
				}
			}
		});

		setVisible(true); // Show frame after adding components
	}

	private class RegisterAction implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			String aadhaarNumber = aadhaarField.getText().replace("-", " "); // Clean input

			boolean isAdmin = false; 
			
			if (isAdminCheckBox.isSelected()) { 
				// Ask for verification code if Admin checkbox is selected 
				String verificationCodeInput = JOptionPane.showInputDialog("Enter Verification Code:");
				if (!"2002".equals(verificationCodeInput)) { 
					JOptionPane.showMessageDialog(null, "Invalid verification code!"); 
					return; 
				} 
				isAdmin = true; 
			}

			try (Connection connection = DatabaseConnection.getConnection()) {
				String query = "INSERT INTO users (username, password, aadhaar_number, is_admin) VALUES (?, ?, ?, ?)";
				PreparedStatement statement = connection.prepareStatement(query); 
				statement.setString(1, username); 
				statement.setString(2, password); 
				statement.setString(3, aadhaarNumber); // Store Aadhaar number
				statement.setBoolean(4, isAdmin); 
				statement.executeUpdate(); 
				JOptionPane.showMessageDialog(null, "Registration successful!"); 
				new LoginFrame(); 
				dispose(); 
			} catch (SQLException ex) { 
				ex.printStackTrace(); 
				JOptionPane.showMessageDialog(null, "Registration failed! Username or Aadhaar might already exist."); 
			} 
		} 
	}
}