
package home;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;

public class OtherClass extends JFrame {
    private JTable dataTable;
    private DefaultTableModel tableModel;
    private final String dbUrl = "jdbc:mysql://localhost:3306/people_count";
    private final String username = "root";
    private final String password = "";

    public OtherClass() {
        // Set frame properties
        this.setTitle("View Data");
        this.setSize(600, 500);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Dispose this frame only
        Image icon = Toolkit.getDefaultToolkit().getImage("C:\\Users\\ACER\\Downloads\\pngegg.png");
        this.setIconImage(icon);
        this.setLayout(new BorderLayout());

        // Create the table model
        tableModel = new DefaultTableModel();
        tableModel.addColumn("S.N");
        tableModel.addColumn("Time");
        tableModel.addColumn("Number Of People");
        tableModel.addColumn(""); // Add the "Delete" button column

        // Create the JTable
        dataTable = new JTable(tableModel);
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Decorate the table and cells
        decorateTable();
        decorateTableHeader();
        decorateTableCells();

        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(dataTable);
        this.add(scrollPane, BorderLayout.CENTER);

        // Add a button column to the table to allow data deletion
        addButtonColumn("Delete");

        // Populate the table with data from the database
        fetchDataFromDatabase();

        // Update the frame
        this.pack();
        this.setVisible(true);
    }

    // Fetch data from the database and populate the table
    private void fetchDataFromDatabase() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            Statement statement = connection.createStatement();
            String sqlQuery = "SELECT * FROM count_data;";
            ResultSet resultSet = statement.executeQuery(sqlQuery);

            // Clear the existing table data
            tableModel.setRowCount(0);

            while (resultSet.next()) {
                int id = resultSet.getInt("S.N");
                Timestamp time = resultSet.getTimestamp("Time");
                int numberOfPeople = resultSet.getInt("Number Of People");

                // Add data to the table
                tableModel.addRow(new Object[]{id, formatTimestamp(time), numberOfPeople, "Delete"});
            }

            resultSet.close();
            statement.close();
            connection.close();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Format timestamp to display only the hour part
    private String formatTimestamp(Timestamp timestamp) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(timestamp);
    }
    // Method to add a button column to the table model
    private void addButtonColumn(String buttonText) {
        // Custom renderer to display the button as a JComponent
        dataTable.getColumnModel().getColumn(3).setCellRenderer((table, value, isSelected, hasFocus, row, column) -> {
            JButton button = new JButton(buttonText);
            button.addActionListener(e -> {
                int idToDelete = (int) tableModel.getValueAt(row, 0);
                deleteDataFromDatabase(idToDelete);
            });
            return button;
        });
    }

    // Method to delete data from the database
    private void deleteDataFromDatabase(int idToDelete) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(dbUrl, username, password);
            String deleteQuery = "DELETE FROM count_data WHERE `S.N` = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery);
            preparedStatement.setInt(1, idToDelete);
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();

            // After deletion, update the table to reflect the changes
            fetchDataFromDatabase();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Decorate the table to improve its appearance
    private void decorateTable() {
        dataTable.setRowHeight(30); // Set the row height
        dataTable.setIntercellSpacing(new Dimension(10, 10)); // Add spacing between cells
        dataTable.setShowGrid(false); // Hide cell borders
    }

    // Decorate the table header
    private void decorateTableHeader() {
        JTableHeader tableHeader = dataTable.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14)); // Set header font
        tableHeader.setForeground(Color.WHITE); // Set header text color
        tableHeader.setBackground(Color.gray); // Set header background color
        ((DefaultTableCellRenderer) tableHeader.getDefaultRenderer()).setHorizontalAlignment(JLabel.CENTER); // Center-align header text
    }

    // Decorate the table cells
    private void decorateTableCells() {
        // Custom renderer to format data in the table cells
        dataTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                           boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Format the time column to display HH:mm format
                if (column == 1) {
                    String timeValue = value.toString();
                    String formattedTime = timeValue.substring(0, 5);
                    ((JLabel) c).setText(timeValue);
                }

                // Center-align data in all cells
                setHorizontalAlignment(SwingConstants.CENTER);

                return c;
            }
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new OtherClass().setVisible(true));
    }
}
