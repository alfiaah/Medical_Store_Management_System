import javax.swing.*;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MedicalShopManagementSystemGUI {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_shop";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    private JFrame frame;
    private Connection conn;

    public MedicalShopManagementSystemGUI() {
        initialize();
        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            createMedicinesTable(conn); // Create medicines table if not exists
            createSoldMedicinesTable(conn); // Create sold_medicines table if not exists
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 450, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        JLabel lblHeading = new JLabel("Medical Shop Management System");
        lblHeading.setBounds(90, 10, 300, 30);
        frame.getContentPane().add(lblHeading);

        JButton btnAddMedicine = new JButton("Add Medicine");
        btnAddMedicine.setBounds(144, 50, 140, 23);
        frame.getContentPane().add(btnAddMedicine);

        JButton btnSellMedicine = new JButton("Sell Medicine");
        btnSellMedicine.setBounds(144, 90, 140, 23);
        frame.getContentPane().add(btnSellMedicine);

        JButton btnDisplayMedicines = new JButton("Display Medicines");
        btnDisplayMedicines.setBounds(144, 130, 140, 23);
        frame.getContentPane().add(btnDisplayMedicines);

        btnAddMedicine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addMedicine();
            }
        });

        btnSellMedicine.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sellMedicine();
            }
        });

        btnDisplayMedicines.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayMedicines();
            }
        });
    }

    private void addMedicine() {
        JTextField nameField = new JTextField();
        JTextField priceField = new JTextField();
        JTextField expDateField = new JTextField();

        Object[] fields = {"Name:", nameField, "Price:", priceField, "Expiration Date (YYYY-MM-DD):", expDateField};

        int result = JOptionPane.showConfirmDialog(null, fields, "Add Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String name = nameField.getText();
            double price = Double.parseDouble(priceField.getText());
            String expDateStr = expDateField.getText();
            Date expirationDate = parseDate(expDateStr);

            try {
                conn.setAutoCommit(false); // Disable autocommit
                PreparedStatement stmt = conn.prepareStatement("INSERT INTO medicines (name, price, expiration_date) VALUES (?, ?, ?)");
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setDate(3, new java.sql.Date(expirationDate.getTime()));
                stmt.executeUpdate();
                conn.commit(); // Commit the transaction
                conn.setAutoCommit(true); // Re-enable autocommit
                JOptionPane.showMessageDialog(null, "Medicine added successfully!");
            } catch (SQLException ex) {
                try {
                    conn.rollback(); // Rollback the transaction if an error occurs
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to add medicine. Please try again.");
            }
        }
    }

    private void sellMedicine() {
        JTextField nameField = new JTextField();
        Object[] fields = {"Name:", nameField};

        int result = JOptionPane.showConfirmDialog(null, fields, "Sell Medicine", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String medicineName = nameField.getText();

            try {
                conn.setAutoCommit(false); // Disable autocommit
                double medicinePrice = getMedicinePrice(conn, medicineName);
                if (medicinePrice == -1) {
                    JOptionPane.showMessageDialog(null, "Medicine not found in inventory!");
                    return;
                }

                PreparedStatement stmt = conn.prepareStatement("DELETE FROM medicines WHERE name = ?");
                stmt.setString(1, medicineName);
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    java.util.Date soldDate = new java.util.Date(); // Current date
                    stmt = conn.prepareStatement("INSERT INTO sold_medicines (name, price, sold_date) VALUES (?, ?, ?)");
                    stmt.setString(1, medicineName);
                    stmt.setDouble(2, medicinePrice);
                    stmt.setDate(3, new java.sql.Date(soldDate.getTime()));
                    stmt.executeUpdate();
                    conn.commit(); // Commit the transaction
                    conn.setAutoCommit(true); // Re-enable autocommit
                    JOptionPane.showMessageDialog(null, medicineName + " sold successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Medicine not found in inventory!");
                }
            } catch (SQLException e) {
                try {
                    conn.rollback(); // Rollback the transaction if an error occurs
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to sell medicine. Please try again.");
            }
        }
    }

    private void displayMedicines() {
        try {
            StringBuilder medicineList = new StringBuilder("Medicines in Inventory:\n");
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM medicines");
            while (rs.next()) {
                medicineList.append("Name: ").append(rs.getString("name")).append(", Price: ").append(rs.getDouble("price"))
                        .append(", Expiration Date: ").append(rs.getDate("expiration_date")).append("\n");
            }
            JOptionPane.showMessageDialog(null, medicineList.toString());
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Failed to display medicines. Please try again.");
        }
    }

    private static void createMedicinesTable(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS medicines (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "price DOUBLE NOT NULL," +
                "expiration_date DATE NOT NULL)";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(createTableSQL);
    }

    private static void createSoldMedicinesTable(Connection conn) throws SQLException {
        String createTableSQL = "CREATE TABLE IF NOT EXISTS sold_medicines (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "name VARCHAR(255) NOT NULL," +
                "price DOUBLE NOT NULL," +
                "sold_date DATE NOT NULL)";
        Statement stmt = conn.createStatement();
        stmt.executeUpdate(createTableSQL);
    }

    private static double getMedicinePrice(Connection conn, String medicineName) throws SQLException {
        String selectPriceSQL = "SELECT price FROM medicines WHERE name = ?";
        PreparedStatement stmt = conn.prepareStatement(selectPriceSQL);
        stmt.setString(1, medicineName);
        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            return rs.getDouble("price");
        }
        return -1;
    }

    private static java.util.Date parseDate(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            return formatter.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MedicalShopManagementSystemGUI window = new MedicalShopManagementSystemGUI();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
