/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package game.review;


import java.sql.*;
import javax.swing.*;
import java.awt.*;

public class SignupPage extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField emailField;

    public SignupPage() {
        // Set the title and size of the window
        setTitle("Signup");
        setSize(1600, 900); // Fixed size 1600x900
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Prevent resizing the window
 ImageIcon logoIcon = new ImageIcon("src/images/logo.png");  // Path to your logo
    setIconImage(logoIcon.getImage());
        // Set the background image
        ImageIcon backgroundIcon = new ImageIcon("src/backgrounds.png"); // Adjust the path as necessary
        JLabel backgroundLabel = new JLabel(backgroundIcon);
        backgroundLabel.setLayout(new BorderLayout());
        setContentPane(backgroundLabel);

        // Create the card panel for the signup form
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new GridLayout(4, 2, 10, 10)); // 4 rows (for Username, Email, Password, and buttons)
        cardPanel.setOpaque(false); // Make the card panel transparent

        // Create and customize the components
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);

        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(20);

        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);

        JButton signupButton = new JButton("Signup");
        JButton loginButton = new JButton("Login");

        // Add components to the card panel
        cardPanel.add(usernameLabel);
        cardPanel.add(usernameField);
        cardPanel.add(emailLabel);
        cardPanel.add(emailField);
        cardPanel.add(passwordLabel);
        cardPanel.add(passwordField);
        cardPanel.add(signupButton);
        cardPanel.add(loginButton);

        // Center the card panel in the frame
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout()); // Use GridBagLayout for centering
        mainPanel.setOpaque(false); // Make the main panel transparent
        mainPanel.add(cardPanel);

        // Add the main panel to the background label
        backgroundLabel.add(mainPanel, BorderLayout.CENTER);

        // Add action listeners
        signupButton.addActionListener(e -> signup());
        loginButton.addActionListener(e -> {
            new LoginPage().setVisible(true);
            dispose(); // Close the signup page
        });

        // Center the window
        setLocationRelativeTo(null);
    }


    private void signup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.connect()) {
            String query = "INSERT INTO users (username, password) VALUES (?, ?)";
            PreparedStatement ps = conn.prepareStatement(query);
            ps.setString(1, username);
            ps.setString(2, password);

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Signup successful!");
                new LoginPage().setVisible(true); // Redirect to Login Page after signup
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Signup failed, please try again.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new SignupPage().setVisible(true);
        });
    }
}
