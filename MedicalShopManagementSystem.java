import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

class Medicine {
    private String name;
    private double price;
    private Date expirationDate;

    public Medicine(String name, double price, Date expirationDate) {
        this.name = name;
        this.price = price;
        this.expirationDate = expirationDate;
    }

    // Getters and setters for Medicine properties
}

public class MedicalShopManagementSystem {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/medical_shop";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public static void main(String[] args) {
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASSWORD);
            createMedicinesTable(conn); // Create medicines table if not exists
            createSoldMedicinesTable(conn); // Create sold_medicines table if not exists

            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n1. Add Medicine\n2. Sell Medicine\n3. Display Medicines\n4. Exit");
                System.out.print("Enter your choice: ");
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline character

                switch (choice) {
                    case 1:
                        System.out.print("Enter name of the medicine: ");
                        String name = scanner.nextLine();
                        System.out.print("Enter price of the medicine: ");
                        double price = scanner.nextDouble();
                        System.out.print("Enter expiration date of the medicine (YYYY-MM-DD): ");
                        String expDateStr = scanner.next();
                        java.util.Date expirationDate = parseDate(expDateStr);

                        // Add medicine to database
                        stmt = conn.prepareStatement("INSERT INTO medicines (name, price, expiration_date) VALUES (?, ?, ?)");
                        stmt.setString(1, name);
                        stmt.setDouble(2, price);
                        stmt.setDate(3, new java.sql.Date(expirationDate.getTime()));
                        stmt.executeUpdate();
                        System.out.println("Medicine added successfully!");
                        break;
                    case 2:
                        System.out.print("Enter name of the medicine to sell: ");
                        String medicineName = scanner.nextLine();

                        // Retrieve price of medicine
                        double medicinePrice = getMedicinePrice(conn, medicineName);
                        if (medicinePrice == -1) {
                            System.out.println("Medicine not found in inventory!");
                            break;
                        }

                        // Sell medicine (remove from inventory and add to sold medicines)
                        stmt = conn.prepareStatement("DELETE FROM medicines WHERE name = ?");
                        stmt.setString(1, medicineName);
                        int rowsAffected = stmt.executeUpdate();
                        if (rowsAffected > 0) {
                            // Add sold medicine to sold_medicines table
                            java.util.Date soldDate = new java.util.Date(); // Current date
                            stmt = conn.prepareStatement("INSERT INTO sold_medicines (name, price, sold_date) VALUES (?, ?, ?)");
                            stmt.setString(1, medicineName);
                            stmt.setDouble(2, medicinePrice);
                            stmt.setDate(3, new java.sql.Date(soldDate.getTime()));
                            stmt.executeUpdate();
                            System.out.println(medicineName + " sold successfully!");
                        } else {
                            System.out.println("Medicine not found in inventory!");
                        }
                        break;
                    case 3:
                        displayMedicines(conn);
                        break;
                    case 4:
                        System.out.println("Exiting... Thank you!");
                        System.exit(0);
                    default:
                        System.out.println("Invalid choice!");
                }
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            try {
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
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

    private static void displayMedicines(Connection conn) throws SQLException {
        System.out.println("Medicines in Inventory:");
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM medicines");
        while (rs.next()) {
            System.out.println("Name: " + rs.getString("name") +
                               ", Price: " + rs.getDouble("price") +
                               ", Expiration Date: " + rs.getDate("expiration_date"));
        }
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
}
