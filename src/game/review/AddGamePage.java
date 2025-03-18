/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.review;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class AddGamePage extends JFrame {
    int userId; // To store user ID

    // JDBC connection variables
    Connection conn;

    // Components
    JTextField titleField, descriptionField, genreField, yearField, developerField;
    JLabel titleLabel, descriptionLabel, genreLabel, yearLabel, developerLabel;
    JButton submitButton, uploadPhotoButton;
    String gamePhotoPath = ""; // Path to store uploaded photo

    // Constructor
    public AddGamePage(int userId) {
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
        setTitle("Add Game");
        setSize(1600, 900);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(null);
 ImageIcon logoIcon = new ImageIcon("src/images/logo.png");  // Path to your logo
    setIconImage(logoIcon.getImage());
        // Set background image
        setContentPane(new JLabel(new ImageIcon("src\\images\\background.jpg")));
        
        // Top right navigation bar
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
        navBar.setBounds(5, 5, 400, 30);
        add(navBar);

        // Create a container for the form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridLayout(6, 2, 10, 10));
        formPanel.setBounds(400, 200, 800, 500); // Center the panel
        formPanel.setBackground(Color.black);

        // Create and customize the components
        titleLabel = new JLabel("Title:");
        titleLabel.setForeground(Color.WHITE); // Set label text color to white
        titleField = new JTextField();

        descriptionLabel = new JLabel("Description:");
        descriptionLabel.setForeground(Color.WHITE);
        descriptionField = new JTextField();

        genreLabel = new JLabel("Genre:");
        genreLabel.setForeground(Color.WHITE);
        genreField = new JTextField();

        yearLabel = new JLabel("Release Year:");
        yearLabel.setForeground(Color.WHITE);
        yearField = new JTextField();

        developerLabel = new JLabel("Developer:");
        developerLabel.setForeground(Color.WHITE);
        developerField = new JTextField();

        uploadPhotoButton = new JButton("Upload Photo");
        uploadPhotoButton.addActionListener(e -> uploadPhoto());

        submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> addGame());

        // Add components to the form panel
        formPanel.add(titleLabel);
        formPanel.add(titleField);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        formPanel.add(genreLabel);
        formPanel.add(genreField);
        formPanel.add(yearLabel);
        formPanel.add(yearField);
        formPanel.add(developerLabel);
        formPanel.add(developerField);
        formPanel.add(uploadPhotoButton);
        formPanel.add(submitButton);

        // Add the form panel to the frame
        add(formPanel);

        setLocationRelativeTo(null); // Center the window
    }

    // Method to upload a photo and save its path
    private void uploadPhoto() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Game Photo");
        int userSelection = fileChooser.showOpenDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToUpload = fileChooser.getSelectedFile();
            String sourcePath = fileToUpload.getAbsolutePath();
            String targetPath = "src/images/games/" + fileToUpload.getName();

            // Move the file to the target directory
            try {
                java.nio.file.Files.copy(fileToUpload.toPath(), java.nio.file.Paths.get(targetPath));
                gamePhotoPath = targetPath; // Save the photo path
                JOptionPane.showMessageDialog(this, "Photo uploaded successfully!");
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to upload photo.");
            }
        }
    }

    // Method to add the game to the database
    private void addGame() {
        String title = titleField.getText();
        String description = descriptionField.getText();
        String genre = genreField.getText();
        String releaseYear = yearField.getText();
        String developer = developerField.getText();

        if (title.isEmpty() || description.isEmpty() || genre.isEmpty() || releaseYear.isEmpty() || developer.isEmpty() || gamePhotoPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields and upload a game photo.");
            return;
        }

        try {
            String query = "INSERT INTO games (title, description, genre, release_year, developer, game_photo) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, title);
            pst.setString(2, description);
            pst.setString(3, genre);
            pst.setString(4, releaseYear);
            pst.setString(5, developer);
            pst.setString(6, gamePhotoPath); // Add the photo path to the database

            pst.executeUpdate();
            JOptionPane.showMessageDialog(this, "Game added successfully!");
            clearFields(); // Clear fields after submission
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to add game to the database.");
        }
    }

    // Method to clear input fields
    private void clearFields() {
        titleField.setText("");
        descriptionField.setText("");
        genreField.setText("");
        yearField.setText("");
        developerField.setText("");
        gamePhotoPath = ""; // Reset the photo path
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new AddGamePage(1).setVisible(true);
        });
    }
}

