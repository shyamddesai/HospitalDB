import java.sql.*;
import java.util.HashSet;
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
        HashSet<String> patientIds = new HashSet<>();

        System.out.println("\nFetching patients...");

        String query = "SELECT patient.phealthcare_No AS Patient, "
                + "patient.pname AS \"Patient Name\", "
                + "patient.contact_info AS \"Contact Info\" "
                + "FROM patient "
                + "ORDER BY patient.pname";

        try (Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(query)) {

            System.out.println(String.format("%-30s%-20s%-20s",
                    "Patient Healthcare No", "Patient Name", "Contact Info"));

            while (rs.next()) {
                String patientHealthcareNo = rs.getString("Patient");
                String patientName = rs.getString("Patient Name");
                String contactInfo = rs.getString("Contact Info");

                patientIds.add(patientHealthcareNo); // Add patient healthcare number to the set

                System.out.println(String.format("%-30s%-20s%-20s",
                        patientHealthcareNo, patientName, contactInfo));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching patients: " + e.getMessage());
        }

        while (true) {
            System.out.print("\nEnter Patient Healthcare Number (or type 0000 to go back to the main menu): ");

            int pid = scanner.nextInt();

            if (pid == 0000) {
                break; // Return to main menu
            }

            String pidStr = String.valueOf(pid);
            if (!patientIds.contains(pidStr)) {
                System.out.println("The entered patient healthcare number does not exist in the list. Please try again.");
                continue; // Ask again
            }

            System.out.println("Fetching patient prescriptions...");

            String query2 = "SELECT patient.pname AS Patient, "
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
                    + "WHERE patient.phealthcare_No = ? "
                    + "ORDER BY patient.pname";

            try (PreparedStatement pstmt = conn.prepareStatement(query2)) {
                pstmt.setInt(1, pid);

                try (ResultSet resultSet = pstmt.executeQuery()) {
                    if (!resultSet.next()) {
                        System.out.println("No prescriptions found for the given patient healthcare number.");
                    } else {
                        System.out.println(String.format("%-30s%-20s%-10s%-25s%-30s%-30s%-30s",
                                "Patient", "Contact Info", "Doctor", "Doctor's Department",
                                "Prescription", "Pharmacy", "Pharmacy Address"));

                        do {
                            String patient = resultSet.getString("Patient");
                            String contactInfo = resultSet.getString("Contact Info");
                            int doctor = resultSet.getInt("Doctor");
                            String doctorsDepartment = resultSet.getString("Doctor's Department");
                            String prescription = resultSet.getString("Prescription");
                            String pharmacy = resultSet.getString("Pharmacy");
                            String pharmacyAddress = resultSet.getString("Pharmacy Address");

                            System.out.println(String.format("%-30s%-20s%-10d%-25s%-30s%-30s%-30s",
                                    patient, contactInfo, doctor, doctorsDepartment,
                                    prescription, pharmacy, pharmacyAddress));
                        } while (resultSet.next());
                    }
                }
            } catch (SQLException e) {
                System.out.println("Error fetching patient prescriptions: " + e.getMessage());
            }
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

    private static void removeEquipment(Connection conn) {
        while(true) {
            System.out.print("Enter the serial number of the equipment to remove (or type 0 to go back to the main menu): ");
            String serialNumber = scanner.next();

            if (serialNumber.equals("0")) {
                break; // Return to main menu
            }

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

    private static void scheduleAppointment(Connection conn) {
        try {
            CallableStatement cs = conn.prepareCall("{call ScheduleAppointment(?, ?, ?, ?, ?)}");

            System.out.print("Enter Patient Healthcare Number: ");
            int phealthcare_no = scanner.nextInt();

            // Check if the patient exists
            if (!entityExists(conn, "patient", "phealthcare_No", phealthcare_no)) {
                System.out.println("Patient does not exist.");
                return;
            }

            System.out.print("Enter Employee Number: ");
            int employee_no = scanner.nextInt();

            // Check if the employee exists
            if (!entityExists(conn, "personnel", "employee_No", employee_no)) {
                System.out.println("Employee does not exist. Please try again.");
                return;
            }

            System.out.print("Enter Appointment Date (YYYY-MM-DD): ");
            String adate = scanner.next();

            System.out.print("Enter Appointment Time (HH:MM): ");
            String atime = scanner.next();

            System.out.print("Enter Equipment Type: ");
            String equipment_type = scanner.next();
            equipment_type = "Type " + equipment_type;

            // Check if the equipment type exists
            if (!entityExists(conn, "equipment", "equipment_type", equipment_type)) {
                System.out.println("Equipment type does not exist. Please try again.");
                return;
            }

            cs.setInt(1, phealthcare_no);
            cs.setInt(2, employee_no);
            cs.setDate(3, java.sql.Date.valueOf(adate));
            cs.setString(4, atime);
            cs.setString(5, equipment_type);

            // Execute the stored procedure
            cs.executeUpdate();

            System.out.println("\nAppointment scheduled successfully!");

            cs.close();
        } catch (NumberFormatException e) {
            System.out.println("Invalid number format. Please enter valid numbers.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid date format. Please enter the date in the format YYYY-MM-DD.");
        } catch (SQLException e) {
            if (e.getSQLState().equals("45000")) {
                System.out.println("Error scheduling appointment: " + e.getMessage());
            } else {
                System.out.println("SQL Error: " + e.getMessage());
            }
        }
    }

    private static boolean entityExists (Connection conn, String tableName, String columnName, Object value){
        String query = "SELECT 1 FROM " + tableName + " WHERE " + columnName + " = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            if (value instanceof Integer) {
                pstmt.setInt(1, (Integer) value);
            } else if (value instanceof String) {
                pstmt.setString(1, (String) value);
            } else {
                return false; // Unsupported type for this utility method
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // If there's a result, the entity exists
            }
        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
            return false;
        }
    }
}