/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.review;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReviewPage extends JFrame {
    int gameId;
    int userId; // To store user ID

    // JDBC connection variables
    Connection conn;
    PreparedStatement pst;
    ResultSet rs;

    // Components
    JLabel gameImageLabel;
    JLabel titleLabel, descriptionLabel, genreLabel, yearLabel, developerLabel, numberOfReviewsLabel, ratingLabel;
    JPanel reviewsPanel;
    String gameName;

    // Constructor
    public ReviewPage(String gameName, int userId) {
        this.gameName = gameName;
        this.userId = userId; // Store user ID passed from the previous page

        // Establish the database connection
        try {
            this.conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_reviews", "Sid", "Siddhesh@12345");
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to connect to the database.");
            return;
        }

        // Set up frame
        setTitle("Game Review Page");
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setResizable(false);
 ImageIcon logoIcon = new ImageIcon("src/images/logo.png");  // Path to your logo
    setIconImage(logoIcon.getImage());
        // Set background image
        setContentPane(new JLabel(new ImageIcon("src\\images\\background.jpg")));
        getContentPane().setLayout(null);

        // Top transparent navigation bar
        JPanel navBar = new JPanel();
        navBar.setBounds(5, 5, 325, 28);
        navBar.setLayout(new GridLayout(1, 5));
        navBar.setOpaque(false);

        // Create buttons for navigation
        JButton homeButton = new JButton("Home");
        
        JButton reviewButton = new JButton("Review");
        JButton addGameButton = new JButton("Add Game");
        JButton profileButton = new JButton("Profile");

        // Add buttons to navBar
        navBar.add(homeButton);
        
        navBar.add(reviewButton);
        navBar.add(addGameButton);
        navBar.add(profileButton);
        add(navBar);

        // Navigation actions
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

        // First container: Game image
        gameImageLabel = new JLabel();
        JPanel imagePanel = new JPanel();
        imagePanel.setBounds(50, 100, 500, 700);
        imagePanel.setBackground(Color.BLACK);
        imagePanel.add(gameImageLabel);
        add(imagePanel);

        // Second container: Game info
        JPanel infoPanel = new JPanel();
        infoPanel.setBounds(600, 100, 500, 300);
        infoPanel.setBackground(Color.BLACK);
        infoPanel.setLayout(new GridLayout(7, 1));

        titleLabel = new JLabel();
        descriptionLabel = new JLabel();
        genreLabel = new JLabel();
        yearLabel = new JLabel();
        developerLabel = new JLabel();
        ratingLabel = new JLabel();
        numberOfReviewsLabel = new JLabel();

        titleLabel.setForeground(Color.WHITE);
        descriptionLabel.setForeground(Color.WHITE);
        genreLabel.setForeground(Color.WHITE);
        yearLabel.setForeground(Color.WHITE);
        developerLabel.setForeground(Color.WHITE);
        ratingLabel.setForeground(Color.WHITE);
        numberOfReviewsLabel.setForeground(Color.WHITE);

        infoPanel.add(titleLabel);
        infoPanel.add(descriptionLabel);
        infoPanel.add(genreLabel);
        infoPanel.add(yearLabel);
        infoPanel.add(developerLabel);
        infoPanel.add(ratingLabel);
        infoPanel.add(numberOfReviewsLabel);
        add(infoPanel);

        // Third container: Write a review
        JPanel writeReviewPanel = new JPanel();
        writeReviewPanel.setBounds(600, 420, 500, 200);
        writeReviewPanel.setBackground(Color.BLACK);
        writeReviewPanel.setLayout(new GridLayout(4, 2));

        JLabel writeReviewLabel = new JLabel("Write a Review");
        JLabel blank = new JLabel("");
        writeReviewLabel.setForeground(Color.WHITE);
        JLabel rateoutoften = new JLabel("Rating Out of Ten:");
        rateoutoften.setForeground(Color.WHITE);
        JTextField ratingField = new JTextField();
        JLabel review = new JLabel("Review:");
        review.setForeground(Color.WHITE);
        JTextArea reviewTextArea = new JTextArea();

        JButton submitButton = new JButton("Submit Review");
        JButton loadButton = new JButton("Load Reviews");
        submitButton.addActionListener(e -> submitReview(ratingField.getText(), reviewTextArea.getText()));
        loadButton.addActionListener(e -> loadReviews());
        
        writeReviewPanel.add(writeReviewLabel);
        writeReviewPanel.add(blank);
        writeReviewPanel.add(rateoutoften);
        writeReviewPanel.add(ratingField);
        writeReviewPanel.add(review);
        writeReviewPanel.add(reviewTextArea);
        writeReviewPanel.add(submitButton);
        writeReviewPanel.add(loadButton);
        add(writeReviewPanel);

        // Fourth container: Reviews section (translucent outer panel)
reviewsPanel = new JPanel();
reviewsPanel.setBounds(1120, 100, 450, 700);
reviewsPanel.setBackground(new Color(0, 0, 0, 50)); // Translucent background for the main container
reviewsPanel.setLayout(new BoxLayout(reviewsPanel, BoxLayout.Y_AXIS)); // Vertical layout for reviews

JScrollPane scrollPane = new JScrollPane(reviewsPanel);
scrollPane.setBounds(1120, 100, 450, 700);
scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
add(scrollPane);

// Load reviews from database
loadReviews();
        // Load game data
        loadGameDetails();

        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Submit review to the database
    // Submit review to the database
private void submitReview(String rating, String reviewText) {
    try {
        if (rating.isEmpty() || reviewText.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in both fields.");
            return;
        }

        // Check if the user has already reviewed this game
        String checkReviewQuery = "SELECT * FROM reviews WHERE game_id = ? AND user_id = ?";
        pst = conn.prepareStatement(checkReviewQuery);
        pst.setInt(1, gameId);
        pst.setInt(2, userId);
        rs = pst.executeQuery();

        if (rs.next()) {
            // User has already reviewed this game
            JOptionPane.showMessageDialog(null, "You have already reviewed this game.");
            return;
        }

        // Insert the new review
        String insertQuery = "INSERT INTO reviews (game_id, user_id, rating, review_text, review_date) VALUES (?, ?, ?, ?, ?)";
        pst = conn.prepareStatement(insertQuery);
        pst.setInt(1, gameId);
        pst.setInt(2, userId);
        pst.setString(3, rating);
        pst.setString(4, reviewText);
        pst.setString(5, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())); // Current timestamp
        pst.executeUpdate();

        JOptionPane.showMessageDialog(null, "Review submitted successfully");

        // Increment the number of reviews for the user in the users table
        incrementUserReviewCount();

        // Update average rating and review count
        updateGameRatingAndReviewCount();

        // Reload reviews after submission
        loadReviews();
        loadGameDetails();
    } catch (Exception e) {
        e.printStackTrace();
    }
}
// Increment the user's review count in the users table
private void incrementUserReviewCount() {
    try {
        String updateQuery = "UPDATE users SET num_reviews = num_reviews + 1 WHERE id = ?";
        pst = conn.prepareStatement(updateQuery);
        pst.setInt(1, userId);
        pst.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // Load reviews from the database and display them
   private void loadReviews() {
    try {
        String query = "SELECT u.username, r.rating, r.review_text, r.review_date FROM reviews r JOIN users u ON r.user_id = u.id WHERE r.game_id = ?";
        pst = conn.prepareStatement(query);
        pst.setInt(1, gameId);
        rs = pst.executeQuery();

        reviewsPanel.removeAll(); // Clear the panel before adding new reviews

        while (rs.next()) {
            String username = rs.getString("username");
            String rating = rs.getString("rating");
            String reviewText = rs.getString("review_text");
            String reviewDate = rs.getString("review_date");

            // Each review will have its own panel (black background, white text)
            JPanel singleReviewPanel = new JPanel();
            singleReviewPanel.setLayout(new GridLayout(3, 1));
            singleReviewPanel.setBackground(new Color(0, 0, 0)); // Black background for each review
            singleReviewPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding inside each review box

            JLabel usernameLabel = new JLabel(username + " - " + reviewDate);
            usernameLabel.setForeground(Color.WHITE); // White text for username and date
            JLabel ratingLabel = new JLabel("Rating: " + rating);
            ratingLabel.setForeground(Color.WHITE); // White text for rating
            JLabel reviewTextLabel = new JLabel("<html>" + reviewText + "</html>");
            reviewTextLabel.setForeground(Color.WHITE); // White text for review content

            // Add components to singleReviewPanel
            singleReviewPanel.add(usernameLabel);
            singleReviewPanel.add(ratingLabel);
            singleReviewPanel.add(reviewTextLabel);

            // Add each review to the main reviews panel
            reviewsPanel.add(singleReviewPanel);
        }

        reviewsPanel.revalidate(); // Refresh the panel to show new reviews
        reviewsPanel.repaint(); // Repaint the panel

    } catch (SQLException e) {
        e.printStackTrace();
    }
}


    // Load game details from the database
    private void loadGameDetails() {
    try {
        String query = "SELECT * FROM games WHERE title = ?";
        pst = conn.prepareStatement(query);
        pst.setString(1, gameName);
        rs = pst.executeQuery();

        if (rs.next()) {
            // Load the game data into components
            titleLabel.setText(rs.getString("title"));
            descriptionLabel.setText("Description: " + rs.getString("description"));
            genreLabel.setText("Genre: " + rs.getString("genre"));
            yearLabel.setText("Year Released: " + rs.getInt("release_year"));
            developerLabel.setText("Developer: " + rs.getString("developer"));
            ratingLabel.setText("Rating: " + rs.getDouble("avg_rating") + "/10");
            numberOfReviewsLabel.setText("Number of Reviews: " + rs.getInt("num_reviews")); // Add this line

            // Set gameId
            gameId = rs.getInt("id");

            // Load game image from file path
            String imagePath = rs.getString("game_photo");
            ImageIcon icon = new ImageIcon(imagePath);
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(500, 700, Image.SCALE_SMOOTH); // Resize image while maintaining aspect ratio
            gameImageLabel.setIcon(new ImageIcon(scaledImg));  // Set the scaled image
        }

    } catch (Exception e) {
        e.printStackTrace();
    }
}

    // Update game rating and number of reviews after submission
  private void updateGameRatingAndReviewCount() {
    try {
        // Calculate the new average rating and review count
        String avgRatingQuery = "SELECT AVG(rating) AS avg_rating, COUNT(*) AS num_reviews FROM reviews WHERE game_id = ?";
        pst = conn.prepareStatement(avgRatingQuery);
        pst.setInt(1, gameId);
        ResultSet rs = pst.executeQuery();

        if (rs.next()) {
            double averageRating = rs.getDouble("avg_rating");
            int reviewCount = rs.getInt("num_reviews");

            // Update the games table with the new values
            String updateGameQuery = "UPDATE games SET avg_rating = ?, num_reviews = ? WHERE id = ?";
            pst = conn.prepareStatement(updateGameQuery);
            pst.setDouble(1, averageRating);
            pst.setInt(2, reviewCount);
            pst.setInt(3, gameId); // Assuming gameId is correctly set to the current game's ID
            pst.executeUpdate();
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
}

    // Main method to run the program
    public static void main(String[] args) {
        // Example usage
        new ReviewPage("The Witcher 3", 1);  // Pass the game name and user ID
    }
}
