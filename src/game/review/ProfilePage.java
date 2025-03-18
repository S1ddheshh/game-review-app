/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.review;

import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class ProfilePage extends JFrame {
    int userId;

    // JDBC connection variables
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    // Components for the first container
    JLabel usernameLabel, numReviewsLabel;
    JPasswordField passwordField, confirmPasswordField;
    JButton resetPasswordButton, logoutButton;

    // Components for the second container (reviews)
    JPanel reviewsContainer;
    JScrollPane scrollPane;

    // Constructor
    public ProfilePage(int userId) {
        this.userId = userId;

        // Establish the database connection
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_reviews", "Sid", "Siddhesh@12345");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
            return;
        }

        // Set up frame
        setTitle("Profile Page");
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
          ImageIcon logoIcon = new ImageIcon("src/images/logo.png");  // Path to your logo
    setIconImage(logoIcon.getImage());
        // Set background image
        setContentPane(new JLabel(new ImageIcon("src/images/background.jpg")));
        getContentPane().setLayout(null);

        // First container: User info and password reset
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(5, 1, 10, 10));
        infoPanel.setBounds(150, 100, 500, 300);  // Position and size
        infoPanel.setBackground(Color.BLACK);

        usernameLabel = new JLabel("Username: ");
        usernameLabel.setForeground(Color.WHITE);
        numReviewsLabel = new JLabel("Number of Reviews: ");
        numReviewsLabel.setForeground(Color.WHITE);

        passwordField = new JPasswordField(20);
        confirmPasswordField = new JPasswordField(20);

        resetPasswordButton = new JButton("Reset Password");
        logoutButton = new JButton("Logout");

        infoPanel.add(usernameLabel);
        infoPanel.add(numReviewsLabel);
        infoPanel.add(new JLabel("New Password:"));
        infoPanel.add(passwordField);
        infoPanel.add(new JLabel("Confirm Password:"));
        infoPanel.add(confirmPasswordField);
        infoPanel.add(resetPasswordButton);
        infoPanel.add(logoutButton);

        add(infoPanel);

        // Second container: User reviews
        reviewsContainer = new JPanel();
        reviewsContainer.setLayout(new BoxLayout(reviewsContainer, BoxLayout.Y_AXIS));
        reviewsContainer.setBackground(Color.BLACK);

        scrollPane = new JScrollPane(reviewsContainer);
        scrollPane.setBounds(700, 100, 700, 600);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        add(scrollPane);

        // Load user details and reviews
        loadUserInfo();
        loadUserReviews();

        // Reset password functionality
        resetPasswordButton.addActionListener(e -> resetPassword());

        // Logout button functionality
        logoutButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose(); // Close the ProfilePage
        });

        // Navigation bar setup (Top right corner)
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navBar.setOpaque(false); // Make navigation bar transparent over background image

        JButton homeButton = new JButton("Home");
        JButton reviewButton = new JButton("Review");
        JButton addGameButton = new JButton("Add Game");
        JButton profileButton = new JButton("Profile");

        navBar.add(homeButton);
        navBar.add(reviewButton);
        navBar.add(addGameButton);
        navBar.add(profileButton);

        // Add action listeners to navigate to different pages
        homeButton.addActionListener(e -> {
            new HomePage(userId).setVisible(true);
            dispose(); // Dispose current page and navigate to HomePage
        });

        reviewButton.addActionListener(e -> {
            new ReviewPage("Default Game", userId).setVisible(true);
            dispose(); // Dispose current page and navigate to ReviewPage
        });

        addGameButton.addActionListener(e -> {
            new AddGamePage(userId).setVisible(true);
            dispose(); // Dispose current page and navigate to AddGamePage
        });

        profileButton.addActionListener(e -> {
            new ProfilePage(userId).setVisible(true);
            dispose(); // Dispose current page and stay on ProfilePage
        });

        // Position the navbar at the top right
        navBar.setBounds(5, 5, 400, 30);
        add(navBar);

        // Center the window
        setLocationRelativeTo(null);
    }

    // Load user info (username and number of reviews)
    private void loadUserInfo() {
        try {
            String query = "SELECT username, num_reviews FROM users WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            rs = pst.executeQuery();

            if (rs.next()) {
                usernameLabel.setText("Username: " + rs.getString("username"));
                numReviewsLabel.setText("Number of Reviews: " + rs.getInt("num_reviews"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load user reviews from the database
    private void loadUserReviews() {
        try {
            String query = "SELECT g.title, r.review_date, r.rating, r.review_text FROM reviews r JOIN games g ON r.game_id = g.id WHERE r.user_id = ?";
            pst = conn.prepareStatement(query);
            pst.setInt(1, userId);
            rs = pst.executeQuery();

            while (rs.next()) {
                String gameTitle = rs.getString("title");
                String reviewDate = rs.getString("review_date");
                String rating = rs.getString("rating");
                String reviewText = rs.getString("review_text");

                // Create a panel for each review
                JPanel reviewPanel = new JPanel();
                reviewPanel.setLayout(new GridLayout(3, 1));
                reviewPanel.setBackground(Color.BLACK);
                reviewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

                JLabel gameTitleLabel = new JLabel(gameTitle + " " + reviewDate);
                gameTitleLabel.setForeground(Color.WHITE);
                JLabel ratingLabel = new JLabel("Rating: " + rating);
                ratingLabel.setForeground(Color.WHITE);
                JLabel reviewTextLabel = new JLabel("<html>" + reviewText + "</html>");
                reviewTextLabel.setForeground(Color.WHITE);

                reviewPanel.add(gameTitleLabel);
                reviewPanel.add(ratingLabel);
                reviewPanel.add(reviewTextLabel);

                reviewsContainer.add(reviewPanel);
            }

            // Refresh the panel
            reviewsContainer.revalidate();
            reviewsContainer.repaint();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Reset password functionality
    private void resetPassword() {
        String newPassword = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!newPassword.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.");
            return;
        }

        try {
            String query = "UPDATE users SET password = ? WHERE id = ?";
            pst = conn.prepareStatement(query);
            pst.setString(1, newPassword);
            pst.setInt(2, userId);

            int rowsAffected = pst.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Password reset successful.");
            } else {
                JOptionPane.showMessageDialog(this, "Password reset failed.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ProfilePage(1).setVisible(true); // Pass the user ID (1 for example)
        });
    }
}



