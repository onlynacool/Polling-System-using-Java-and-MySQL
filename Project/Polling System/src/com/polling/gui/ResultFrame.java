package com.polling.gui;

import com.polling.database.DatabaseConnection;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ResultFrame extends JFrame {

    public ResultFrame() {
    	setTitle("Polling Result - Column Chart");
    	setSize(800, 600); 
    	setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    	// Create a gradient panel with orange and yellow colors.
    	GradientPanel gradientPanel =
    			new GradientPanel(Color.BLUE,
    					Color.YELLOW);
    	setContentPane(gradientPanel);

    	try (Connection connection =
    			DatabaseConnection.getConnection()) {

            String queryVotesCount =
                    "SELECT vote_option AS option_name , COUNT(*) AS total_votes FROM votes GROUP BY vote_option";
            PreparedStatement statementVotesCount =
                    connection.prepareStatement(queryVotesCount);
            ResultSet resultSetVotesCount =
                    statementVotesCount.executeQuery();

            DefaultCategoryDataset dataset =
                    new DefaultCategoryDataset();

            while (resultSetVotesCount.next()) {
                String optionName =
                        resultSetVotesCount.getString("option_name");
                int totalVotes =
                        resultSetVotesCount.getInt("total_votes");
                dataset.addValue(totalVotes,"Votes", optionName); 
            }

            JFreeChart columnChart =
                    ChartFactory.createBarChart(
                            "Polling Result - Column Chart",
                            "Options",
                            "Votes",
                            dataset);

            ChartPanel chartPanel =
                    new ChartPanel(columnChart);

            add(chartPanel,
                    BorderLayout.CENTER);

     } catch (SQLException ex) { ex.printStackTrace(); }

     setVisible(true);  
   }
}