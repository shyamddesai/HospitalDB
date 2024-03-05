import java.sql.*;
import java.util.Scanner;

public class HospitalDatabaseApp {
    private static Connection conn = null;
    private static Scanner scanner = new Scanner(System.in);
    private static boolean exit = false;

    private static void connectToDatabase() throws SQLException {
        System.out.println("Connecting to the database...");
        String DB_URL = "jdbc:db2://winter2024-comp421.cs.mcgill.ca:50000/COMP421";
        String USER = "cs421g46";
        String PASS = "group46slay!";

        try {
            DriverManager.registerDriver(new COM.ibm.db2.jdbc.net.DB2Driver());
        } catch (Exception e) {
            System.out.println("Class Not Found");
        }

        try {
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        connectToDatabase();

        int option;
        while (!exit) {
            System.out.println("\n======== Hospital Menu ========");
            System.out.println("1. View Equipment Inventory");
            System.out.println("2. Add New Equipment");
            System.out.println("3. Schedule an Appointment");
            System.out.println("4. Cancel an Appointment");
            System.out.println("5. Update Patient Records");
            System.out.println("0. Quit");
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
                case 0:
                    System.out.println("Exiting the program...");
                    exit = true;
                    try {
                        if (conn != null)
                            conn.close();
                    } catch (SQLException se) {
                        se.printStackTrace();
                    }
                    scanner.close();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
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