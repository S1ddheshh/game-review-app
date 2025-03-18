/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.review;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class HomePage extends JFrame {
    private JTextField searchField;
    private JComboBox<String> sortByBox;
    private JPanel gamePanel;
    private Connection conn;
    int userId; 

     public HomePage(int userId) {
        this.userId = userId;
        setTitle("Home Page");
        setSize(1600, 900);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        setResizable(false);
 ImageIcon logoIcon = new ImageIcon("src/images/logo.png");  // Path to your logo
    setIconImage(logoIcon.getImage());
        // Background panel for setting background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon backgroundImage = new ImageIcon("src\\images\\background.jpg");
                Image img = backgroundImage.getImage();
                g.drawImage(img, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // Database connection
        connectToDatabase();

        // Navigation bar with links to pages
        JPanel navBar = new JPanel(new FlowLayout(FlowLayout.LEFT));
        navBar.setOpaque(false); // Make navigation bar transparent over background image
        JButton homeButton = new JButton("Home");
        
        JButton reviewButton = new JButton("Review");
        JButton addGameButton = new JButton("Add Game");
        JButton profileButton = new JButton("Profile");
        
        navBar.setOpaque(false);  // Ensure it is opaque so the background is visible



        navBar.add(homeButton);
        
        navBar.add(reviewButton);
        navBar.add(addGameButton);
        navBar.add(profileButton);

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

        // Top bar with search and sort
        JPanel topPanel = new JPanel(new FlowLayout());
        topPanel.setOpaque(false); // Make top panel transparent
        searchField = new JTextField(30);
        
        JButton searchButton = new JButton("Search");
        sortByBox = new JComboBox<>(new String[]{"Highest Rating", "Lowest Rating", "Most Reviews", "Least Reviews", "Newest", "Oldest"});
        JLabel searchLabel = new JLabel("Search:");
searchLabel.setForeground(Color.WHITE); 
topPanel.add(searchLabel);// Set text color to white
        topPanel.add(searchField);
        topPanel.add(searchButton);
        JLabel sortByLabel = new JLabel("Sort by:");
sortByLabel.setForeground(Color.WHITE);
topPanel.add(sortByLabel);  // Set text color to white
        topPanel.add(sortByBox);

        // Game display area
        gamePanel = new JPanel();
        gamePanel.setLayout(new GridLayout(0, 4, 10, 10)); // 4 games per row
        gamePanel.setOpaque(false); // Make game panel transparent so background is visible

        // Populate games from the database
        loadGamesFromDatabase();

        // Scrollable pane for the games
        JScrollPane scrollPane = new JScrollPane(gamePanel);
        scrollPane.getViewport().setOpaque(false); // Make the scroll pane transparent
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

       // Create a new panel to hold both navBar and topPanel
JPanel topContainer = new JPanel();
topContainer.setLayout(new BorderLayout());  // Set a layout for the new container

// Make the topContainer transparent
topContainer.setOpaque(false);

// Add both the navBar and topPanel to this new container
topContainer.add(navBar, BorderLayout.NORTH);  // navBar goes on top
topContainer.add(topPanel, BorderLayout.SOUTH);  // topPanel (search and sort) goes below the navBar

// Now add this container to the top of the backgroundPanel
backgroundPanel.add(topContainer, BorderLayout.NORTH);  // Add combined container to the top

// Keep the rest of your layout unchanged
backgroundPanel.add(scrollPane, BorderLayout.CENTER);  // Add game cards in the center area

        // Search and sorting actions
        searchButton.addActionListener(e -> performSearch());
        sortByBox.addActionListener(e -> performSort());
         
        setVisible(true);
        setLocationRelativeTo(null);
    }

    // Method to create database connection
    private void connectToDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/game_reviews", "Sid", "Siddhesh@12345"); // Update with your DB credentials
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Load games dynamically from the database
    private void loadGamesFromDatabase() {
        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM games");

            while (rs.next()) {
                String gameTitle = rs.getString("title");
                double rating = rs.getDouble("avg_rating");
                int numReviews = rs.getInt("num_reviews");
                String imagePath = rs.getString("game_photo"); // Load the image path

                gamePanel.add(createGameCard(gameTitle, rating, numReviews, imagePath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Creates a card layout for each game
    private JPanel createGameCard(String gameName, double rating, int reviews, String imagePath) {
    JPanel card = new JPanel();
    card.setLayout(new BorderLayout());
    card.setBackground(Color.BLACK);  // Set the card background to black
    card.setPreferredSize(new Dimension(200, 250)); // Set size for the cards

    // Create and set the image label
    ImageIcon gameImage = new ImageIcon(imagePath);
    Image img = gameImage.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH); // Resize to fit the card
    gameImage = new ImageIcon(img);
    JLabel imageLabel = new JLabel(gameImage);
    imageLabel.setHorizontalAlignment(JLabel.CENTER);

    JLabel nameLabel = new JLabel(gameName);
    nameLabel.setHorizontalAlignment(JLabel.CENTER);
    nameLabel.setForeground(Color.WHITE);  // Set text color to white
    JLabel ratingLabel = new JLabel("Rating: " + rating + "/10");
    ratingLabel.setHorizontalAlignment(JLabel.CENTER);
    ratingLabel.setForeground(Color.WHITE);  // Set text color to white
    JLabel reviewsLabel = new JLabel("Reviews: " + reviews);
    reviewsLabel.setHorizontalAlignment(JLabel.CENTER);
    reviewsLabel.setForeground(Color.WHITE);  // Set text color to white

    // Create a panel for text and add it to the card
    JPanel textPanel = new JPanel();
    textPanel.setLayout(new GridLayout(3, 1)); // 3 labels in a column
    textPanel.add(nameLabel);
    textPanel.add(ratingLabel);
    textPanel.add(reviewsLabel);
    textPanel.setOpaque(false); // Make text panel transparent over the black card background

    card.add(imageLabel, BorderLayout.NORTH); // Add image label at the top
    card.add(textPanel, BorderLayout.CENTER); // Add text panel to center

    card.setBorder(BorderFactory.createLineBorder(Color.WHITE));  // White border around the game card

    // Click to navigate to review page
    card.addMouseListener(new MouseAdapter() {
        public void mouseClicked(MouseEvent e) {
            new ReviewPage(gameName, userId); // Navigate to review page with gameName
            dispose();
        }
    });

    return card;
}

    // Perform search based on input in the search field
    private void performSearch() {
        String query = searchField.getText();
        gamePanel.removeAll();

        try {
            PreparedStatement stmt = conn.prepareStatement("SELECT * FROM games WHERE title LIKE ?");
            stmt.setString(1, "%" + query + "%");
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String gameTitle = rs.getString("title");
                double rating = rs.getDouble("avg_rating");
                int numReviews = rs.getInt("num_reviews");
                String imagePath = rs.getString("game_photo"); // Load the image path
                gamePanel.add(createGameCard(gameTitle, rating, numReviews, imagePath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        gamePanel.revalidate();
        gamePanel.repaint();
    }

    // Sort the games based on selected criteria
    private void performSort() {
        String selectedSort = (String) sortByBox.getSelectedItem();
        String query = "SELECT * FROM games ORDER BY ";

        switch (selectedSort) {
            case "Highest Rating":
                query += "avg_rating DESC";
                break;
            case "Lowest Rating":
                query += "avg_rating ASC";
                break;
            case "Most Reviews":
                query += "num_reviews DESC";
                break;
            case "Least Reviews":
                query += "num_reviews ASC";
                break;
            case "Newest":
                query += "release_year DESC";
                break;
            case "Oldest":
                query += "release_year ASC";
                break;
        }

        gamePanel.removeAll();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String gameTitle = rs.getString("title");
                double rating = rs.getDouble("avg_rating");
                int numReviews = rs.getInt("num_reviews");
                String imagePath = rs.getString("game_photo"); // Load the image path
                gamePanel.add(createGameCard(gameTitle, rating, numReviews, imagePath));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        gamePanel.revalidate();
        gamePanel.repaint();
    }

    public static void main(String[] args) {
        //SwingUtilities.invokeLater(() -> new HomePage());
    }
}
