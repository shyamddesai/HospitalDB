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

        try { DriverManager.registerDriver ( new com.ibm.db2.jcc.DB2Driver() ) ; }
        catch (Exception cnfe){ System.out.println("Class not found"); }

        conn = DriverManager.getConnection(DB_URL, USER, PASS);
    }

    public static void main(String[] args) {
        try {
			connectToDatabase();
		} catch (SQLException e) {
            e.printStackTrace();
        }

        int option;
        while (!exit) {
            System.out.println("\n======== Hospital Menu ========");
            System.out.println("1. View Patient Prescriptions");
            System.out.println("2. View Equipment Inventory");
            System.out.println("3. Add New Equipment");
            System.out.println("4. Schedule an Appointment");
            System.out.println("5. Cancel an Appointment");
            System.out.println("6. Update Patient Records");
            System.out.println("0. Quit");
            System.out.print("Please Enter Your Option: ");

            option = scanner.nextInt();

            switch (option) {
                case 1:
                    viewPatientPrescriptions(conn);
                case 2:
                    viewEquipmentInventory(conn);
                    break;
                case 3:
                    addNewEquipment(conn, scanner);
                    break;
                case 4:
                    scheduleAppointment(conn, scanner);
                    break;
                case 5:
                    cancelAppointment(conn, scanner);
                    break;
                case 6:
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

    private static void viewPatientPrescriptions(Connection conn) {
        System.out.println("\nFetching patient prescriptions...");

        String query = "SELECT patient.pname AS Patient, "
                + "patient.contact_info AS \"Contact Info\", "
                + "personnel.employee_No AS Doctor, "
                + "personnel.department AS \"Doctor's Department\", "
                + "prescribe.medication AS Prescription, "
                + "pharmacy.phname AS Pharmacy, "
                + "pharmacy.phaddress AS \"Pharmacy Address\" "
                + "FROM patient "
                + "JOIN prescribe ON patient.phealthcare_No = prescribe.phealthcare_No "
                + "JOIN personnel ON prescribe.employee_No = personnel.employee_No "
                + "JOIN pharmacy ON (prescribe.phname = pharmacy.phname AND prescribe.phaddress = pharmacy.phaddress) "
                + "ORDER BY patient.pname";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println(String.format("%-30s%-20s%-10s%-25s%-30s%-30s%-30s",
                    "Patient", "Contact Info", "Doctor", "Doctor's Department",
                    "Prescription", "Pharmacy", "Pharmacy Address"));

            while (rs.next()) {
                String patient = rs.getString("Patient");
                String contactInfo = rs.getString("Contact Info");
                int doctor = rs.getInt("Doctor");
                String doctorsDepartment = rs.getString("Doctor's Department");
                String prescription = rs.getString("Prescription");
                String pharmacy = rs.getString("Pharmacy");
                String pharmacyAddress = rs.getString("Pharmacy Address");

                System.out.println(String.format("%-30s%-20s%-10d%-25s%-30s%-30s%-30s",
                        patient, contactInfo, doctor, doctorsDepartment,
                        prescription, pharmacy, pharmacyAddress));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching patient prescriptions: " + e.getMessage());
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