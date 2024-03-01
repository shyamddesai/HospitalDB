import java.sql.*;
import java.util.Scanner;

public class HospitalDatabaseApp {

    // Database connection details
    static final String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/COMP421";
    static final String USER = "sdesai14";
    static final String PASS = "password";

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        Scanner scanner = new Scanner(System.in);
        int option;

        try {
            // Open a connection
            System.out.println("Connecting to the database...");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // Loop for the menu
            do {
                System.out.println("\nHospital Main Menu");
                System.out.println("1. View Equipment Inventory");
                System.out.println("2. Add New Equipment");
                System.out.println("3. Schedule an Appointment");
                System.out.println("4. Cancel an Appointment");
                System.out.println("5. Update Patient Records");
                System.out.println("6. Quit");
                System.out.print("Please Enter Your Option: ");

                option = scanner.nextInt();

                switch (option) {
                    case 1:
                        viewEquipmentInventory(conn);
                        break;
                    case 2:
                        addNewEquipment(conn, scanner);
                        break;
                    case 3:
                        scheduleAppointment(conn, scanner);
                        break;
                    case 4:
                        cancelAppointment(conn, scanner);
                        break;
                    case 5:
                        updatePatientRecords(conn, scanner);
                        break;
                    case 6:
                        System.out.println("Exiting the program...");
                        break;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } while (option != 6);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Clean up environment
            try {
                if (stmt != null) conn.close();
            } catch (SQLException se) {
            } // do nothing
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
            scanner.close();
        }
        System.out.println("Goodbye!");
    }

    private static void viewEquipmentInventory(Connection conn) {
        // Implementation of viewing equipment inventory
    }

    private static void addNewEquipment(Connection conn, Scanner scanner) {
        // Implementation of adding new equipment
    }

    private static void scheduleAppointment(Connection conn, Scanner scanner) {
        // Implementation of scheduling an appointment
    }

    private static void cancelAppointment(Connection conn, Scanner scanner) {
        // Implementation of canceling an appointment
    }

    private static void updatePatientRecords(Connection conn, Scanner scanner) {
        // Implementation of updating patient records
    }
}