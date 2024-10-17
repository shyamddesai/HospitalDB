# Montreal Hospital Management System
The Montreal Hospital Management System is a database application designed to streamline hospital management by providing functionalities for managing patient records, scheduling appointments, prescribing medication, handling equipment, and tracking hospital resources. This project was developed for COMP421 and demonstrates advanced database management techniques, including stored procedures, indexing, and data visualization.

## Key Features
1. **Appointment Scheduling**: A stored procedure (`ScheduleAppointment`) ensures appointments are conflict-free and schedules available equipment with minimum life expectancy.
2. **Medication Management**: A stored procedure (`PrescribeAndUpdatePatientRecord`) manages patient prescriptions and updates medical records simultaneously.
3. **Indexing**: Implements indexes to improve query performance on frequently accessed fields, such as date/time in the Appointment table and life expectancy in the Equipment table.

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

## Project Highlights
- **Efficiency**: Uses indexing and optimized queries to ensure fast access to frequently accessed data.
- **Data Consistency**: Stored procedures enforce data consistency by managing conflicts during appointment scheduling and prescription updates.
- **Modularity**: Each module (Patient, Equipment, Appointments, etc.) is designed to operate independently, supporting modularity and ease of maintenance.

## Project Members
- Shyam Desai
- Sanjna Puri
- Charles Villegas
- Stevan Vujicic
