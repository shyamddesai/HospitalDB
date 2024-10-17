# Montreal Hospital Management System
The Montreal Hospital Management System is a database application designed to streamline hospital management by providing functionalities for managing patient records, scheduling appointments, prescribing medication, handling equipment, and tracking hospital resources. This project demonstrates advanced database management techniques, including stored procedures, indexing, and data visualization.

## Key Features
1. **Appointment Scheduling**: A stored procedure (`ScheduleAppointment`) ensures appointments are conflict-free and schedules available equipment with minimum life expectancy.
2. **Medication Management**: A stored procedure (`PrescribeAndUpdatePatientRecord`) manages patient prescriptions and updates medical records simultaneously.
3. **Indexing**: Implements indexes to improve query performance on frequently accessed fields, such as date/time in the Appointment table and life expectancy in the Equipment table.

---

## SQL Queries
### Query 1: Patient Prescription and Pharmacy Information
This query retrieves patient details, including contact information, their assigned doctor, prescribed medication, and the pharmacy for filling the prescription.

```sql
SELECT patient.pname AS Patient,
       patient.contact_info AS "Contact Info",
       personnel.employee_No AS Doctor,
       personnel.department AS "Doctor's Department",
       prescribe.medication AS Prescription,
       pharmacy.phname AS Pharmacy,
       pharmacy.phaddress AS "Pharmacy's Address"
FROM patient
JOIN prescribe ON patient.phealthcare_No = prescribe.phealthcare_No
JOIN personnel ON prescribe.employee_No = personnel.employee_No
JOIN pharmacy ON (prescribe.phname = pharmacy.phname AND prescribe.phaddress = pharmacy.phaddress)
ORDER BY pname;
```

### Query 2: Hospital Resources Overview
Provides a summary of equipment types and personnel counts for each hospital. Useful for administrators to assess resource levels.

```sql
WITH HospitalEquipment AS (
    SELECT h.hname, h.haddress, e.equipment_type,
           AVG(e.life_expectancy) AS AvgLifeExpectancy
    FROM equipment e
    JOIN hospital h ON e.hname = h.hname AND e.haddress = h.haddress
    GROUP BY h.hname, h.haddress, e.equipment_type
),
PersonnelCount AS (
    SELECT hname, haddress, COUNT(employee_No) AS TotalPersonnel
    FROM personnel
    GROUP BY hname, haddress
)
SELECT he.hname AS Hospital,
       he.haddress AS "Hospital Address",
       he.equipment_type AS "Equipment Type",
       he.AvgLifeExpectancy AS "Equipment Avg. Life Expectancy",
       pc.TotalPersonnel AS "Total Staff"
FROM HospitalEquipment he
JOIN PersonnelCount pc ON he.hname = pc.hname AND he.haddress = pc.haddress
ORDER BY he.hname, he.equipment_type;
```

### Query 3: Patient Appointment Schedule
Generates a schedule of appointments with details on patient names, dates, times, doctors, and required equipment.

```sql
WITH AppointmentEquipment AS (
    SELECT a.phealthcare_No, a.adate, a.atime, c.equipment_type
    FROM appointment a
    JOIN conduct c ON a.phealthcare_No = c.phealthcare_No AND a.adate = c.adate AND a.atime = c.atime
),
PatientAppointments AS (
    SELECT pat.pname AS PatientName,
           a.adate,
           a.atime,
           pat.employee_No AS DoctorEmployeeNumber,
           per.department,
           ae.equipment_type
    FROM appointment a
    JOIN patient pat ON a.phealthcare_No = pat.phealthcare_No
    JOIN AppointmentEquipment ae ON a.phealthcare_No = ae.phealthcare_No AND a.adate = ae.adate AND a.atime = ae.atime
    JOIN personnel per ON pat.employee_No = per.employee_No
)
SELECT pa.PatientName AS Patient,
       pa.adate AS "Appointment Date",
       pa.atime AS "Appointment Time",
       pa.DoctorEmployeeNumber AS Doctor,
       pa.department AS "Doctor's Department",
       pa.equipment_type AS "Required Equipment"
FROM PatientAppointments pa
ORDER BY pa.adate, pa.atime, pa.PatientName;
```

### Query 4: Average Billing by Patient and Insurance
Calculates the average billing amount for individual patients and their insurance providers, useful for financial assessments.

```sql
WITH PatientBilling AS (
    SELECT phealthcare_No, AVG(appointment_fee) AS AverageBilling
    FROM bill
    GROUP BY phealthcare_No
),
PatientInsurance AS (
    SELECT p.pname, p.phealthcare_No, p.iname, pb.AverageBilling
    FROM patient p
    JOIN PatientBilling pb ON p.phealthcare_No = pb.phealthcare_No
),
InsuranceAverageBilling AS (
    SELECT p.iname AS InsuranceCompany, AVG(b.appointment_fee) AS InsuranceAvgBilling
    FROM bill b
    JOIN patient p ON b.phealthcare_No = p.phealthcare_No
    GROUP BY p.iname
)
SELECT pi.pname AS Patient,
       pi.phealthcare_No AS "Healthcare Number",
       pi.iname AS Insurer,
       pi.AverageBilling AS "Patient Avg. Bill",
       iab.InsuranceAvgBilling AS "Insurer Avg. Bill"
FROM PatientInsurance pi
JOIN InsuranceAverageBilling iab ON pi.iname = iab.InsuranceCompany
ORDER BY pi.iname, pi.phealthcare_No;
```

## Stored Procedure Example

### ScheduleAppointment
The `ScheduleAppointment` stored procedure checks for conflicts before scheduling a new appointment. It ensures no overlap with existing appointments and selects equipment with a minimum life expectancy.

```sql
CREATE PROCEDURE ScheduleAppointment (
    IN patient_id INT,
    IN appointment_date DATE,
    IN appointment_time TIME,
    IN doctor_id INT,
    IN equipment_type VARCHAR(50)
)
BEGIN
    DECLARE conflict_count INT;

    -- Check for appointment conflicts
    SELECT COUNT(*) INTO conflict_count
    FROM appointment
    WHERE phealthcare_No = patient_id
      AND adate = appointment_date
      AND atime = appointment_time;

    -- If there are conflicts, exit the procedure
    IF conflict_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Appointment conflict detected.';
    ELSE
        -- Schedule appointment with available equipment
        INSERT INTO appointment (phealthcare_No, adate, atime, employee_No)
        VALUES (patient_id, appointment_date, appointment_time, doctor_id);

        -- Link the required equipment
        INSERT INTO conduct (phealthcare_No, adate, atime, equipment_type)
        SELECT patient_id, appointment_date, appointment_time, equipment_type
        FROM equipment
        WHERE equipment_type = equipment_type
          AND life_expectancy > 1
        LIMIT 1;
    END IF;
END;
```

This procedure improves scheduling efficiency by checking for conflicts and selecting the necessary equipment based on specified criteria.

---

## Usage
### Application Program
1. **View Patient Prescriptions**: Retrieve and display active prescriptions for a patient.
2. **View Hospital Equipment and Staff**: List hospital resources and personnel.
3. **View Patient Appointments**: Check scheduled appointments for a patient.
4. **Schedule an Appointment**: Allows admin users to book appointments while checking for conflicts.
5. **Remove Equipment**: Provides functionality to delete equipment records from the database.
6. **Quit**: Exits the application.

## Setup
1. **Database Setup**:
   - Ensure that a compatible SQL database (e.g., MySQL) is set up and configured.
   - Load the provided SQL scripts to initialize the database schema and populate tables.

2. **Stored Procedures**:
   - Use the included stored procedure scripts to add `ScheduleAppointment` and `PrescribeAndUpdatePatientRecord` to the database.

3. **JDBC Application**:
   - The application requires a Java Development Kit (JDK) and JDBC driver compatible with the database.
   - Compile and run the Java application, which provides an interface to interact with the hospital database.

### Running the Application
1. **Compile the Java Application**:
   ```bash
   javac HospitalDatabaseApp.java
   ```
2. **Run the Application**:
   ```bash
   java HospitalDatabaseApp
   ```

---

## Relational Schema
![image](https://github.com/user-attachments/assets/b676bd48-7bc1-478e-bb89-820b0252d495)

### Entities
- **Patient**: Stores patient details including health information, medications, and assigned healthcare personnel.
- **Equipment**: Tracks hospital equipment with details on type, model, supplier, and life expectancy.
- **Donor**: Manages donor records with information on blood type, medical history, and donation type.
- **Pharmacy**: Holds pharmacy information including location and services.
- **Insurer**: Lists insurance providers and plan types.
- **Personnel**: Manages hospital staff, including departments, licenses, and expirations.
- **Appointment**: Tracks patient appointments, including date, time, and procedures.
- **Hospital**: Maintains hospital details, including name, address, and company number.

### Relationships
- **Care**: Links personnel to patients.
- **Bill**: Associates appointments with personnel and patients.
- **Schedule**: Manages appointment schedules for personnel and patients.
- **Conduct**: Tracks personnel involvement in appointments requiring specific equipment.
- **Prescribe**: Links prescriptions to personnel, pharmacies, and patients.
- **Donate**: Records donations from patients to hospitals.
- **Share**: Manages patient record sharing between hospitals.

---

## Project Highlights
- **Efficiency**: Uses indexing and optimized queries to ensure fast access to frequently accessed data.
- **Data Consistency**: Stored procedures enforce data consistency by managing conflicts during appointment scheduling and prescription updates.
- **Modularity**: Each module (Patient, Equipment, Appointments, etc.) is designed to operate independently, supporting modularity and ease of maintenance.

## Project Members
- Shyam Desai
- Sanjna Puri
- Charles Villegas
- Stevan Vujicic
