
package splash_screen;

import home.PeopleCount;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainApplicationWindow {

    public MainApplicationWindow() {
        // Code to run after 3 seconds
        JFrame frame = new JFrame("Home Page");
        frame.setLayout(new BorderLayout());
        frame.setLocationByPlatform(true);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 500); // Set the size of the frame
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\ACER\\Downloads\\pngegg.png");
        frame.setIconImage(icon);
        // Create a JPanel to hold the components
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(Color.LIGHT_GRAY);
        // Create a JLabel for the title
        JLabel titleLabel = new JLabel("Welcome to People Counting Application");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 40));
        titleLabel.setForeground(Color.darkGray);
        int leftMargin = 30;
        titleLabel.setBorder(new EmptyBorder(10, leftMargin, 0, 0));

        panel.add(titleLabel); // Add the title label to the panel
        panel.add(titleLabel, BorderLayout.NORTH);

        ImageIcon logoIcon = new ImageIcon("C:\\Users\\ACER\\Downloads\\home1.png");
        Image image = logoIcon.getImage();
        Image resizedImage = image.getScaledInstance(400, 300, Image.SCALE_SMOOTH);
        ImageIcon resizedLogoIcon = new ImageIcon(resizedImage);
        JLabel logoLabel = new JLabel(resizedLogoIcon);
        logoLabel.setBorder(new EmptyBorder(0, 45, 0, 0)); // Shift the image 50 pixels to the left
        panel.add(logoLabel, BorderLayout.CENTER);

        //for button
        JButton featureButton = new JButton("Start Count");
        featureButton.setPreferredSize(new Dimension(160, 50));
        featureButton.setFont(new Font("Times New Roman", Font.BOLD, 24));
        featureButton.setForeground(Color.WHITE);


        featureButton.setBackground(new Color(52, 152, 219));
        featureButton.setBorderPainted(false);
        featureButton.setFocusPainted(false);

        featureButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        panel.add(featureButton, BorderLayout.SOUTH); // Add the feature button to the panel
        featureButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent args) {
                new PeopleCount();
                frame.setVisible(false);
            }
        });

        JLabel descriptionLabel = new JLabel("<html><div style='font: times new roman;font-weight: bold;text-align: center; width: 250px;'>" + "The system offers real-time data visualization by displaying the count of " + "detected faces on the screen. As individuals are detected, the count is updated " + "dynamically, providing instant feedback on the number of people present in the " + "monitored space.<br><br>" +

                "This is the first Proto-type of the application.This system applications may be, such as crowd management " + "in public places, retail analytics, monitoring occupancy in buildings, and " + "ensuring compliance with social distancing measures. It enables businesses and " + "organizations to make informed decisions based on live data, improving " + "efficiency and enhancing safety.</div></html>");

        descriptionLabel.setBorder(new EmptyBorder(10, 30, 10, 05)); // Add some padding
        panel.add(descriptionLabel, BorderLayout.WEST);
        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainApplicationWindow();
        });
    }
}
