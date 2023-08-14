package splash_screen;


import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JFrame {
    private static final long serialVersionUID = 1L;
    private JPanel contentPane;
    private JLabel imgLabel;
    private JLabel textLabel;
    private JProgressBar progressBar;

    public SplashScreen() {
        setTitle("People Counter");

        setResizable(false);
        setDefaultCloseOperation(3);
        setSize(800, 500);
        setLocationRelativeTo(null);
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\ACER\\Downloads\\pngegg.png");
        setIconImage(icon);

        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBackground(Color.WHITE);

        ImageIcon imgIcon = new ImageIcon("C:\\Users\\ACER\\Downloads\\pngegg.png"); // Replace "logo.png" with your logo image file path
        Image img = imgIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        imgIcon = new ImageIcon(img);
        imgLabel = new JLabel(imgIcon);
        imgLabel.setPreferredSize(new Dimension(300, 200));

        textLabel = new JLabel("People Counter");
        textLabel.setFont(new Font("Times New Roman", Font.BOLD, 48));
        textLabel.setForeground(Color.darkGray);


        progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(300, 20));

        contentPane.add(imgLabel, BorderLayout.WEST);
        contentPane.add(textLabel, BorderLayout.CENTER);

        contentPane.add(progressBar, BorderLayout.NORTH);

        setContentPane(contentPane);
        contentPane.setBackground(Color.lightGray);
        setVisible(true);
        try {
            Thread.sleep(4000);
            setVisible(false);
            // Sleep for 3 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        new SplashScreen();

        MainApplicationWindow abc = new MainApplicationWindow();
        // abc.run();


        // Close the splash screen and launch the main application window
        //  new MainApplicationWindow();
    }
}

