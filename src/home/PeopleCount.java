package home;

import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.highgui.HighGui;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;

import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;


public class PeopleCount extends JFrame {
    private final VideoCapture videoCapture;
    private final String dbUrl = "jdbc:mysql://localhost:3306/people_count";
    private final String username = "root";
    private final String password = "";
    private final int SAVE_INTERVAL_MINUTES = 1; // Set the save interval in minutes
    private mxGraphComponent graphComponent;
    private mxGraph graph;
    private Object parent;
    private JLabel countLabel;
    private int frameWidth;
    private int frameHeight;
    private ArrayList<Integer> countData;
    private ArrayList<Long> timestamps;
    private int maxPeople;
    private long startTimeMillis;
    private boolean showTimestamps;
    private Timer saveTimer;

    public PeopleCount() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        // Load the pre-trained cascade classifier for pedestrian detection
        CascadeClassifier cascade = new CascadeClassifier();
        cascade.load("C:\\Users\\ACER\\IdeaProjects\\PeopleCounter\\cascade.xml"); // path to cascade classifier

        // Open the default webcam
        videoCapture = new VideoCapture(0);
        if (!videoCapture.isOpened()) {
            System.out.println("Failed to open the webcam!");
            return;
        }

        frameWidth = 800;
        frameHeight = 600;

        graph = new mxGraph();
        parent = graph.getDefaultParent();
        graphComponent = new mxGraphComponent(graph);
        graphComponent.getViewport().setOpaque(true);
        graphComponent.getViewport().setBackground(Color.WHITE);

        // Creating a label to display the count
        countLabel = new JLabel("Number of Faces: 0");
        countLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Add the label to the frame
        this.add(countLabel, BorderLayout.NORTH);

        // Create a panel to hold the graph component with margins
        JPanel graphMarginPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        graphMarginPanel.add(graphComponent);

        // Add the panel to the frame with margins
        this.add(graphMarginPanel, BorderLayout.CENTER);

        // Set frame properties
        this.setTitle("People Count");
        this.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\ACER\\Downloads\\pngegg.png"));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(frameWidth, frameHeight);
        this.setLocationRelativeTo(null);
        // Initialize count data for the live line graph
        countData = new ArrayList<>();
        timestamps = new ArrayList<>();
        maxPeople = 10;
        startTimeMillis = System.currentTimeMillis();
        showTimestamps = false;

        // Start the webcam processing in a Timer
        Timer timer = new Timer(30, e -> processVideoFrame());
        timer.start();

        // Start the background graph updating in a separate thread
        Thread graphThread = new Thread(new GraphUpdateRunnable());
        graphThread.start();
        //class to got o another class for data view
        JButton goToOtherClassButton = new JButton("See Data");
        goToOtherClassButton.setPreferredSize(new Dimension(250, 40));
        goToOtherClassButton.setBackground(new Color(51, 200, 250));
        goToOtherClassButton.setForeground(Color.WHITE);
        goToOtherClassButton.setFocusPainted(false);
        goToOtherClassButton.setFont(new Font("Times New Roman", Font.BOLD, 16));
        goToOtherClassButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(51, 200, 250), 3),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));


        goToOtherClassButton.addActionListener(e -> openOtherClass());
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.add(Box.createHorizontalGlue()); // Add glue to push the button to the right
        buttonPanel.add(goToOtherClassButton);
        this.add(buttonPanel, BorderLayout.SOUTH);


        this.setVisible(true);

// Start the timer to save data at one-minute intervals
        saveTimer = new Timer(SAVE_INTERVAL_MINUTES * 60 * 1000, e -> saveDataToDatabase());
        saveTimer.start();

    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PeopleCount());
    }

    private void openOtherClass() {
        SwingUtilities.invokeLater(() -> new OtherClass().setVisible(true));
    }

    private void processVideoFrame() {
        Mat frame1 = new Mat();
        Mat grayFrame = new Mat();
        MatOfRect detectedFaces = new MatOfRect();

        if (videoCapture.read(frame1)) {
            // Convert the frame to grayscale for pedestrian detection
            Imgproc.cvtColor(frame1, grayFrame, Imgproc.COLOR_BGR2GRAY);
            Imgproc.equalizeHist(grayFrame, grayFrame);

            // Detect pedestrians in the frame
            CascadeClassifier cascade = new CascadeClassifier();
            cascade.load("C:\\Users\\ACER\\IdeaProjects\\PeopleCounter\\cascade.xml");
            cascade.detectMultiScale(grayFrame, detectedFaces);
            // Draw green rectangle boxes around the detected faces
            for (Rect rect : detectedFaces.toArray()) {
                // Draw the rectangle on the frame
                Imgproc.rectangle(frame1, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0), 2);
            }

            int numFaces = detectedFaces.toArray().length;
            // Update the count in the countLabel
            countLabel.setText("Number of Faces: " + numFaces);

            // Add the data point to the countData list
            synchronized (countData) {
                countData.add(numFaces);
                timestamps.add(System.currentTimeMillis());
            }

            // Display the frame in HighGui (optional, for debugging)
            HighGui.imshow("Counting.....", frame1);
            this.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\ACER\\Downloads\\pngegg.png"));
            HighGui.waitKey(1);
        }
    }

    private void saveDataToDatabase() {
        try {
            synchronized (countData) {
                int sum = 0;
                for (Integer count : countData) {
                    sum += count;
                }
                int averageCount = countData.isEmpty() ? 0 : sum / countData.size();

                Class.forName("com.mysql.cj.jdbc.Driver");
                Connection connection = DriverManager.getConnection(dbUrl, username, password);
                String insertQuery = "INSERT INTO count_data (`Number Of People`, `Time`) VALUES (?, ?);";
                PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
                preparedStatement.setInt(1, averageCount);
                preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
                preparedStatement.executeUpdate();
                preparedStatement.close();
                connection.close();

                // Clear countData and timestamps after saving data
                countData.clear();
                timestamps.clear();
            }
        } catch (ClassNotFoundException e) {
            System.err.println("JDBC driver not found.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error connecting to the database.");
            e.printStackTrace();
        }
    }

    private void plotLineGraph(List<Integer> data, List<Long> timestamps) {
        graph.getModel().beginUpdate();
        try {
            // Clear the existing graph
            graph.removeCells(graph.getChildVertices(parent));

            // Set the style for the line graph
            String style = "defaultVertex;fillColor=none;strokeColor=#0000FF;strokeWidth=2";
            graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
            graph.getStylesheet().getDefaultVertexStyle().put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);

            // Determine the y-axis scale based on the available height
            int yScale = frameHeight / (maxPeople + 1);

            // Check if data is empty or only has one data point
            if (data.isEmpty() || data.size() == 1) {
                return;
            }

            // Calculate the x-axis scale based on the available width
            int xScale = frameWidth / (data.size() - 1);

            // Add data points to the line graph
            Object previousVertex = null;
            for (int i = 0; i < data.size(); i++) {
                int y = frameHeight - (data.get(i) * yScale);
                int x = i * xScale;

                // Insert the vertex and edge for the data point
                Object vertex = graph.insertVertex(parent, null, "", x, y, 0, 0, style);
                if (previousVertex != null) {
                    graph.insertEdge(parent, null, "", previousVertex, vertex);
                }
                previousVertex = vertex;

                // Add y-axis labels (numbers from 0 to 10)
                int yLabel = (maxPeople - i) * yScale;
                graph.insertVertex(parent, null, String.valueOf(i), -30, yLabel, 0, 0, "align=right;verticalAlign=middle;fontSize=10");

            }

            // Draw y-axis line
            graph.insertEdge(parent, null, "", graph.insertVertex(parent, null, "", 0, 0, 0, 0), previousVertex);
            // Add x-axis ticks

            for (int i = 0; i < data.size(); i++) {
                int xTick = i * xScale;
                Object tickVertex = graph.insertVertex(parent, null, "", xTick, frameHeight, 0, 0, "shape=ellipse;fillColor=#000000");

                // Get the corresponding timestamp for the current data point
                long timestamp = timestamps.get(i);

                // Convert the timestamp to a local date time string
                String currentTime = formatLocalTime(timestamp);

                Object labelVertex = graph.insertVertex(parent, null, currentTime, xTick - 20, frameHeight + 10, 40, 10, "align=center;verticalAlign=middle;fontSize=10");
                graph.insertEdge(parent, null, "", tickVertex, labelVertex);
            }

            // Add y-axis ticks
            for (int i = 0; i <= maxPeople; i++) {
                int yTick = frameHeight - i * yScale;
                Object tickVertex = graph.insertVertex(parent, null, "", -10, yTick, 0, 0, "shape=ellipse;fillColor=#000000");
                Object labelVertex = graph.insertVertex(parent, null, String.valueOf(i), -5, yTick - 1, 30, 10, "align=right;verticalAlign=middle;fontSize=10");
                graph.insertEdge(parent, null, "", tickVertex, labelVertex);
            }
        } finally {
            graph.getModel().endUpdate();
        }
    }

    private String formatLocalTime(long timestamp) {
        // Convert timestamp to Date object
        Date date = new Date(timestamp);

        // Create SimpleDateFormat instance to format the date in the local time zone
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());

        // Format the date to a local time string
        return sdf.format(date);
    }

    private class GraphUpdateRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                // Update the graph component
                synchronized (countData) {
                    plotLineGraph(new ArrayList<>(countData),
                            new ArrayList<>(timestamps));
                }

                // Pause briefly to allow other threads to execute
                try {
                    Thread.sleep(8000); // Update every 3 seconds
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
