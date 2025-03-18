/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.review;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginPage() {
        // Set the title and size of the window
        setTitle("Login");
        setSize(1600, 900); // Fixed size 1600x900
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Prevent resizing the window
 ImageIcon logoIcon = new ImageIcon("src/images/logo.png");  // Path to your logo
    setIconImage(logoIcon.getImage());
        // Set the background image
        ImageIcon backgroundIcon = new ImageIcon("src/backgroundl.png"); // Adjust the path as necessary
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);

        // Create the card panel for the login form
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(3, 2, 10, 10)); // Add gaps between components
        cardPanel.setOpaque(false); // Make the card panel transparent

        // Create and customize the components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        
        JButton loginButton = new JButton("Login");
        JButton signupButton = new JButton("Signup");

        // Add components to the card panel
        cardPanel.add(usernameLabel);
        cardPanel.add(usernameField);
        cardPanel.add(passwordLabel);
        cardPanel.add(passwordField);
        cardPanel.add(loginButton);
        cardPanel.add(signupButton);

        // Center the card panel in the frame
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        mainPanel.setOpaque(false); // Make the main panel transparent
        mainPanel.add(cardPanel);

        // Add the main panel to the background label
        backgroundLabel.add(mainPanel, BorderLayout.CENTER);

        // Add action listeners
        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> {
            new SignupPage().setVisible(true);
            dispose(); // Close the login page
        });

        // Center the window
        setLocationRelativeTo(null);
    }


    private void login() {
    String username = usernameField.getText();
    String password = new String(passwordField.getPassword());

    try (Connection conn = DatabaseConnection.connect()) {
        String query = "SELECT * FROM users WHERE username=? AND password=?";
        PreparedStatement ps = conn.prepareStatement(query);
        ps.setString(1, username);
        ps.setString(2, password);
        
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            int userId = rs.getInt("id"); // Retrieve user_id from the result set
            JOptionPane.showMessageDialog(this, "Login successful!");
            new HomePage(userId).setVisible(true); // Redirect to Home Page with userId
            dispose(); // Close the login page
        } else {
            JOptionPane.showMessageDialog(this, "Invalid credentials, please try again.");
        }
    } catch (SQLException ex) {
        ex.printStackTrace();
    }
}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginPage().setVisible(true);
        });
    }
}
