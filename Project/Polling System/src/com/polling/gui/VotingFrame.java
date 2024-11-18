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

public class VotingFrame extends JFrame {
    private String aadhaarNumber; // This will now represent the Aadhaar number
    private boolean hasVoted;
    private boolean isAdmin; // Variable to check if the user is an admin

    public VotingFrame(String aadhaarNumber, boolean hasVoted, boolean isAdmin) {
    	this.aadhaarNumber = aadhaarNumber; // Store Aadhaar number
    	this.hasVoted = hasVoted;
    	this.isAdmin = isAdmin; // Store admin status

    	setTitle("Polling System - Vote");
    	setSize(400, 300);
    	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    	// Create a gradient panel with orange and yellow colors
    	GradientPanel gradientPanel = new GradientPanel(Color.YELLOW, Color.CYAN);
    	setContentPane(gradientPanel);
    	gradientPanel.setLayout(new GridLayout(7, 1)); 

    	JLabel userInfo = new JLabel("Aadhaar Number: " + aadhaarNumber + " | Status: " + (hasVoted ? "Voted" : "Not Voted"));
    	userInfo.setForeground(hasVoted ? Color.GREEN : Color.RED);
    	add(userInfo);

    	if (!hasVoted) {
        	addVoteButtons(); // Add voting buttons if user hasn't voted
    	} else {
        	JLabel message = new JLabel("You have already voted!");
        	add(message);
        	if (!isAdmin) { // Show result button only for normal users who have voted
            	JButton showResultButton = new JButton("Show Result");
            	showResultButton.addActionListener(new ShowResultAction());
            	add(showResultButton);
        	}
    	}

    	if (isAdmin) { // Check if the user is an admin
        	JButton showResultButton = new JButton("Show Result");
        	showResultButton.addActionListener(new ShowResultAction());
        	add(showResultButton);

        	JButton resetButton = new JButton("Reset"); // Reset button for admin
        	resetButton.addActionListener(new ResetAction());
        	add(resetButton);
    	}

    	setVisible(true); // Make sure this is called after all components are added
   }

   private void addVoteButtons() {
       String[] options = {"Black Eagle", "Ryder", "Rowen", "NACSA"};
       for (String option : options) {
           JButton button = new JButton(option);
           button.addActionListener(new VoteAction(option));
           add(button); // Add each voting option button
       }
   }

   private class VoteAction implements ActionListener {
       private String voteOption;

       public VoteAction(String voteOption) {
           this.voteOption = voteOption; // Store the selected vote option
       }

       public void actionPerformed(ActionEvent e) {
           try (Connection connection = DatabaseConnection.getConnection()) {
               connection.setAutoCommit(false);

               // Check if user has voted based on Aadhaar number
               String checkVoteQuery = "SELECT has_voted FROM users WHERE aadhaar_number = ?";
               PreparedStatement checkVoteStatement = connection.prepareStatement(checkVoteQuery);
               checkVoteStatement.setString(1, aadhaarNumber); // Use Aadhaar number to check voting status
               ResultSet resultSet = checkVoteStatement.executeQuery();

               if (resultSet.next() && resultSet.getBoolean("has_voted")) {
                   JOptionPane.showMessageDialog(null, "You have already voted!");
                   return; // Exit if user has already voted
               }

               // Insert vote into votes table
               String voteQuery = "INSERT INTO votes (user_aadhaar, vote_option) VALUES (?, ?)";
               PreparedStatement voteStatement = connection.prepareStatement(voteQuery);
               voteStatement.setString(1, aadhaarNumber); // Use Aadhaar number for voting
               voteStatement.setString(2, voteOption);
               voteStatement.executeUpdate();

               // Update user status to indicate they have voted
               String updateUserQuery = "UPDATE users SET has_voted = TRUE WHERE aadhaar_number = ?";
               PreparedStatement updateUserStatement = connection.prepareStatement(updateUserQuery);
               updateUserStatement.setString(1, aadhaarNumber); // Use Aadhaar number for update
               updateUserStatement.executeUpdate();

               connection.commit(); // Commit transaction

               JOptionPane.showMessageDialog(null, "Vote cast successfully!");

               dispose();
               
               new VotingFrame(aadhaarNumber, true, isAdmin); // Pass admin status to new frame
           } catch (SQLException ex) {
               ex.printStackTrace();
               JOptionPane.showMessageDialog(null, "Error while voting!");
           }
       }
   }

   private class ShowResultAction implements ActionListener {
       public void actionPerformed(ActionEvent e) {
           new ResultFrame(); // Open result frame to show polling results
       }
   }

   private class ResetAction implements ActionListener { // Reset action for admin users
       public void actionPerformed(ActionEvent e) {
           try (Connection connection = DatabaseConnection.getConnection()) {
               connection.setAutoCommit(false);

               // Reset votes in votes table
               String deleteVotesQuery = "DELETE FROM votes";
               PreparedStatement deleteVotesStatement = connection.prepareStatement(deleteVotesQuery);
               deleteVotesStatement.executeUpdate();

               // Reset users' voting status except for admins
               String resetUsersQuery = "UPDATE users SET has_voted = FALSE WHERE is_admin = FALSE";
               PreparedStatement resetUsersStatement =
                       connection.prepareStatement(resetUsersQuery);
               resetUsersStatement.executeUpdate();

               connection.commit(); // Commit transaction

               JOptionPane.showMessageDialog(null,
                       "Votes and user statuses reset successfully!");
           } catch (SQLException ex) {
               ex.printStackTrace();
               JOptionPane.showMessageDialog(null,
                       "Error while resetting votes and user statuses!");
           }
       }
   }
}