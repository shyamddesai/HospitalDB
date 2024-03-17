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
            DriverManager.registerDriver(new com.ibm.db2.jcc.DB2Driver());
        } catch (Exception cnfe) {
            System.out.println("Class not found");
        }

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
            System.out.println("2. View Hospital Equipment and Staff");
            System.out.println("3. View Patient Appointments");
            System.out.println("4. Schedule an Appointment");
            System.out.println("5. Remove an Equipment");
            System.out.println("0. Quit");
            System.out.print("Please Enter Your Option: ");

            option = scanner.nextInt();

            switch (option) {
                case 1:
                    viewPatientPrescriptions(conn);
                    break;
                case 2:
                    viewHospitalEquipmentAndStaff(conn);
                    break;
                case 3:
                    viewPatientAppointmentDetails(conn);
                    break;
                case 4:
                    scheduleAppointment(conn);
                    break;
                case 5:
                    removeEquipment(conn);
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

    private static void viewHospitalEquipmentAndStaff(Connection conn) {
        String query = "WITH HospitalEquipment AS ("
                + "SELECT h.hname, h.haddress, e.equipment_type, "
                + "AVG(e.life_expetancy) AS AvgLifeExpectancy "
                + "FROM equipment e "
                + "JOIN hospital h "
                + "ON e.hname = h.hname "
                + "AND e.haddress = h.haddress "
                + "GROUP BY h.hname, h.haddress, e.equipment_type "
                + "), "
                + "PersonnelCount AS ("
                + "SELECT hname, haddress, COUNT(employee_No) AS TotalPersonnel "
                + "FROM personnel "
                + "GROUP BY hname, haddress "
                + ") "
                + "SELECT "
                + "he.hname AS Hospital, "
                + "he.haddress AS \"Hospital Address\", "
                + "he.equipment_type AS \"Equipment Type\", "
                + "he.AvgLifeExpectancy AS \"Equipment Avg. Life Expectancy\", "
                + "pc.TotalPersonnel AS \"Total Staff\" "
                + "FROM HospitalEquipment he "
                + "JOIN PersonnelCount pc "
                + "ON he.hname = pc.hname "
                + "AND he.haddress = pc.haddress "
                + "ORDER BY he.hname, he.equipment_type;";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println(String.format("%-30s%-30s%-20s%-35s%-15s",
                    "Hospital", "Hospital Address", "Equipment Type",
                    "Equipment Avg. Life Expectancy", "Total Staff"));

            while (rs.next()) {
                String hospital = rs.getString("Hospital");
                String hospitalAddress = rs.getString("Hospital Address");
                String equipmentType = rs.getString("Equipment Type");
                double avgLifeExpectancy = rs.getDouble("Equipment Avg. Life Expectancy");
                int totalStaff = rs.getInt("Total Staff");

                System.out.println(String.format("%-30s%-30s%-20s%-35f%-15d",
                        hospital, hospitalAddress, equipmentType,
                        avgLifeExpectancy, totalStaff));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching hospital equipment and staff information: " + e.getMessage());
        }
    }

    private static void viewPatientAppointmentDetails(Connection conn) {
        String query = "WITH AppointmentEquipment AS ( "
                + "SELECT a.phealthcare_No, a.adate, a.atime, c.equipment_type "
                + "FROM appointment a "
                + "JOIN conduct c "
                + "ON a.phealthcare_No = c.phealthcare_No "
                + "AND a.adate = c.adate "
                + "AND a.atime = c.atime "
                + "), "
                + "PatientAppointments AS ( "
                + "SELECT "
                + "pat.pname AS PatientName, "
                + "a.adate, "
                + "a.atime, "
                + "pat.employee_No AS DoctorEmployeeNumber, "
                + "per.department, "
                + "ae.equipment_type "
                + "FROM appointment a "
                + "JOIN patient pat ON a.phealthcare_No = pat.phealthcare_No "
                + "JOIN AppointmentEquipment ae "
                + "ON a.phealthcare_No = ae.phealthcare_No "
                + "AND a.adate = ae.adate "
                + "AND a.atime = ae.atime "
                + "JOIN personnel per ON pat.employee_No = per.employee_No "
                + ") "
                + "SELECT "
                + "pa.PatientName AS Patient, "
                + "pa.adate AS \"Appointment Date\", "
                + "pa.atime AS \"Appointment Time\", "
                + "pa.DoctorEmployeeNumber AS Doctor, "
                + "pa.department AS \"Doctor's Department\", "
                + "pa.equipment_type AS \"Required Equipment\" "
                + "FROM PatientAppointments pa "
                + "ORDER BY pa.adate, pa.atime, pa.PatientName;";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            System.out.println(String.format("%-30s%-20s%-15s%-10s%-25s%-20s",
                    "Patient", "Appointment Date", "Appointment Time",
                    "Doctor", "Doctor's Department", "Required Equipment"));
//            System.out.printf("===============================================================================================================%n");

            while (rs.next()) {
                String patient = rs.getString("Patient");
                Date appointmentDate = rs.getDate("Appointment Date");
                Time appointmentTime = rs.getTime("Appointment Time");
                int doctorEmployeeNumber = rs.getInt("Doctor");
                String department = rs.getString("Doctor's Department");
                String equipmentType = rs.getString("Required Equipment");

                System.out.println(String.format("%-30s%-20s%-15s%-10d%-25s%-20s",
                        patient, appointmentDate.toString(), appointmentTime.toString(),
                        doctorEmployeeNumber, department, equipmentType));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching patient appointment details: " + e.getMessage());
        }
    }

    private static void scheduleAppointment(Connection conn) {
        try {
            // Preparing the CallableStatement for the stored procedure
            CallableStatement cs = conn.prepareCall("{call ScheduleAppointment(?, ?, ?, ?, ?)}");

            System.out.print("Enter Patient Healthcare Number: ");
            int phealthcare_no = scanner.nextInt();

            System.out.print("Enter Employee Number: ");
            int employee_no = scanner.nextInt();
            scanner.nextLine(); // Consume the newline left-over

            System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
            String adate = scanner.next();

            System.out.print("Enter Appointment Time (HH:MM): ");
            String atime = scanner.next();
            scanner.nextLine(); // Consume the newline left-over

            System.out.print("Enter Equipment Type: ");
            String equipment_type = scanner.nextLine();

            cs.setInt(1, phealthcare_no);
            cs.setInt(2, employee_no);
            cs.setDate(3, Date.valueOf(adate)); // Converts string to SQL date
            cs.setString(4, atime);
            cs.setString(5, equipment_type);

            // Execute the stored procedure
            cs.executeUpdate();

            System.out.println("Appointment scheduled successfully.");

            // Close the CallableStatement
            cs.close();
        } catch (SQLException e) {
            System.out.println("SQL exception occurred: " + e.getMessage());
        }
    }

    private static void removeEquipment(Connection conn) {
        System.out.print("Enter the serial number of the equipment to remove: ");
        String serialNumber = scanner.next();
        try {
            String query = "DELETE FROM equipment WHERE SERIAL_NO = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, serialNumber);
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("Equipment with serial number " + serialNumber + " removed successfully.");
            } else {
                System.out.println("No equipment found with serial number " + serialNumber);
            }
        } catch (SQLException e) {
            System.out.println("Error removing equipment");
        }
    }
}