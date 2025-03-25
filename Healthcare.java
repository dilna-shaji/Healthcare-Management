import java.sql.*;
import java.util.Scanner;

class InvalidContactNumberException extends Exception {
    public InvalidContactNumberException(String message) {
        super(message);
    }
}

class InvalidDateFormatException extends Exception {
    public InvalidDateFormatException(String message) {
        super(message);
    }
}

abstract class Person {
    private String name;
    private String contactNumber;

    public Person(String name, String contactNumber) throws InvalidContactNumberException {
        this.name = name;
        setContactNumber(contactNumber);
    }

    public String getName() {
        return name;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    protected void setContactNumber(String contactNumber) throws InvalidContactNumberException {
        if (contactNumber == null || contactNumber.trim().length() != 10 || !contactNumber.matches("\\d+")) {
            throw new InvalidContactNumberException("Contact number must be exactly 10 digits!");
        }
        this.contactNumber = contactNumber;
    }

    public abstract void displayDetails();
}

class Patient extends Person {
    private int patientID;
    private int age;
    private String gender;
    private StringBuffer medicalHistory;

    public Patient(int patientID, String name, int age, String gender, String contactNumber) throws InvalidContactNumberException {
        super(name, contactNumber);
        this.patientID = patientID;
        this.age = age;
        this.gender = gender;
        this.medicalHistory = new StringBuffer();
    }

    public int getPatientID() { return patientID; }
    public int getAge() { return age; }
    public String getGender() { return gender; }
    public String getMedicalHistory() { return medicalHistory.toString(); }

    public synchronized void addMedicalCondition(String condition) {
        if (medicalHistory.length() > 0) {
            medicalHistory.append("; ");
        }
        medicalHistory.append(condition);
    }

    public void displayDetails() {
        System.out.println("--- Patient Details ---");
        System.out.println("Patient ID: " + patientID);
        System.out.println("Name: " + getName());
        System.out.println("Age: " + age);
        System.out.println("Gender: " + gender);
        System.out.println("Contact: " + getContactNumber());
        System.out.println("Medical History: " + medicalHistory.toString());
    }
}

class Doctor extends Person {
    private int doctorID;
    private String specialization;

    public Doctor(int doctorID, String name, String specialization, String contactNumber) throws InvalidContactNumberException {
        super(name, contactNumber);
        this.doctorID = doctorID;
        this.specialization = specialization;
    }

    public int getDoctorID() { return doctorID; }
    public String getSpecialization() { return specialization; }

    public void displayDetails() {
        System.out.println("--- Doctor Details ---");
        System.out.println("Doctor ID: " + doctorID);
        System.out.println("Name: " + getName());
        System.out.println("Specialization: " + specialization);
        System.out.println("Contact: " + getContactNumber());
    }
}

class Appointment {
    private int appointmentID;
    private Patient patient;
    private Doctor doctor;
    private String appointmentDate;

    public Appointment(int appointmentID, Patient patient, Doctor doctor, String appointmentDate) throws InvalidDateFormatException {
        this.appointmentID = appointmentID;
        this.patient = patient;
        this.doctor = doctor;
        setAppointmentDate(appointmentDate);
    }
    
    public int getAppointmentID() { return appointmentID; }
    public Patient getPatient() { return patient; }
    public Doctor getDoctor() { return doctor; }
    public String getAppointmentDate() { return appointmentDate; }

    private void setAppointmentDate(String date) throws InvalidDateFormatException {
        if (!date.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new InvalidDateFormatException("Invalid date format! Use YYYY-MM-DD");
        }
        this.appointmentDate = date;
    }

    public void displayDetails() {
        System.out.println("--- Appointment Details ---");
        System.out.println("Appointment ID: " + appointmentID);
        System.out.println("Patient: " + patient.getName());
        System.out.println("Doctor: " + doctor.getName());
        System.out.println("Date: " + appointmentDate);
    }
}

class DatabaseManager {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/healthcare";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "#Dilna@2007"; // Using password from your second file

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }

    // Method to create or update tables if they don't exist
    public static void initDatabase() {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Creating patients table
            String createPatientsTable = "CREATE TABLE IF NOT EXISTS patients (" +
                    "patient_id INT PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "age INT NOT NULL," +
                    "gender VARCHAR(10) NOT NULL," +
                    "contact_number VARCHAR(10) NOT NULL," +
                    "medical_history TEXT)";
            stmt.executeUpdate(createPatientsTable);
            
            // Creating doctors table
            String createDoctorsTable = "CREATE TABLE IF NOT EXISTS doctors (" +
                    "doctor_id INT PRIMARY KEY," +
                    "name VARCHAR(100) NOT NULL," +
                    "specialization VARCHAR(100) NOT NULL," +
                    "contact_number VARCHAR(10) NOT NULL)";
            stmt.executeUpdate(createDoctorsTable);
            
            // Creating appointments table
            String createAppointmentsTable = "CREATE TABLE IF NOT EXISTS appointments (" +
                    "appointment_id INT PRIMARY KEY," +
                    "patient_id INT NOT NULL," +
                    "doctor_id INT NOT NULL," +
                    "appointment_date DATE NOT NULL," +
                    "FOREIGN KEY (patient_id) REFERENCES patients(patient_id)," +
                    "FOREIGN KEY (doctor_id) REFERENCES doctors(doctor_id))";
            stmt.executeUpdate(createAppointmentsTable);
            
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void createPatient(Patient patient) {
        String insertPatientSQL = "INSERT INTO patients (patient_id, name, age, gender, contact_number, medical_history) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertPatientSQL)) {
            pstmt.setInt(1, patient.getPatientID());
            pstmt.setString(2, patient.getName());
            pstmt.setInt(3, patient.getAge());
            pstmt.setString(4, patient.getGender());
            pstmt.setString(5, patient.getContactNumber());
            pstmt.setString(6, patient.getMedicalHistory());
            pstmt.executeUpdate();
            System.out.println("Patient record inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting patient: " + e.getMessage());
        }
    }

    public static void createDoctor(Doctor doctor) {
        String insertDoctorSQL = "INSERT INTO doctors (doctor_id, name, specialization, contact_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertDoctorSQL)) {
            pstmt.setInt(1, doctor.getDoctorID());
            pstmt.setString(2, doctor.getName());
            pstmt.setString(3, doctor.getSpecialization());
            pstmt.setString(4, doctor.getContactNumber());
            pstmt.executeUpdate();
            System.out.println("Doctor record inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting doctor: " + e.getMessage());
        }
    }

    public static void createAppointment(Appointment appointment) {
        String insertAppointmentSQL = "INSERT INTO appointments (appointment_id, patient_id, doctor_id, appointment_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(insertAppointmentSQL)) {
            pstmt.setInt(1, appointment.getAppointmentID());
            pstmt.setInt(2, appointment.getPatient().getPatientID());
            pstmt.setInt(3, appointment.getDoctor().getDoctorID());
            pstmt.setDate(4, java.sql.Date.valueOf(appointment.getAppointmentDate()));
            pstmt.executeUpdate();
            System.out.println("Appointment record inserted successfully.");
        } catch (SQLException e) {
            System.out.println("Error inserting appointment: " + e.getMessage());
        }
    }

    public static Patient getPatientById(int patientId) {
        String selectPatientSQL = "SELECT * FROM patients WHERE patient_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(selectPatientSQL)) {
            pstmt.setInt(1, patientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String gender = rs.getString("gender");
                String contactNumber = rs.getString("contact_number");
                String medicalHistory = rs.getString("medical_history");
                
                Patient patient = new Patient(patientId, name, age, gender, contactNumber);
                if (medicalHistory != null && !medicalHistory.isEmpty()) {
                    for (String condition : medicalHistory.split("; ")) {
                        patient.addMedicalCondition(condition);
                    }
                }
                return patient;
            }
        } catch (SQLException | InvalidContactNumberException e) {
            System.out.println("Error retrieving patient: " + e.getMessage());
        }
        return null;
    }

    public static Doctor getDoctorById(int doctorId) {
        String selectDoctorSQL = "SELECT * FROM doctors WHERE doctor_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(selectDoctorSQL)) {
            pstmt.setInt(1, doctorId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String name = rs.getString("name");
                String specialization = rs.getString("specialization");
                String contactNumber = rs.getString("contact_number");
                
                return new Doctor(doctorId, name, specialization, contactNumber);
            }
        } catch (SQLException | InvalidContactNumberException e) {
            System.out.println("Error retrieving doctor: " + e.getMessage());
        }
        return null;
    }

    public static void deleteDoctor(int doctorId) {
        String deleteAppointments = "DELETE FROM appointments WHERE doctor_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(deleteAppointments)) {
            pstmt.setInt(1, doctorId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error deleting doctor's appointments: " + e.getMessage());
            return;
        }

        String deleteDoctor = "DELETE FROM doctors WHERE doctor_id = ?";
        try (Connection conn = getConnection(); PreparedStatement pstmt = conn.prepareStatement(deleteDoctor)) {
            pstmt.setInt(1, doctorId);
            int rowsAffected = pstmt.executeUpdate();
            System.out.println(rowsAffected + " doctor record(s) deleted successfully.");
        } catch (SQLException e) {
            System.out.println("Error deleting doctor: " + e.getMessage());
        }
    }
}

public class Healthcare {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;
        
        try {
            // Initialize database tables if they don't exist
            DatabaseManager.initDatabase();
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
        
        do {
            System.out.println("\n--- Healthcare Management System ---");
            System.out.println("1. Add Patient");
            System.out.println("2. Add Doctor");
            System.out.println("3. Schedule Appointment");
            System.out.println("4. Add Medical Condition to Patient");
            System.out.println("5. Display Patient Details");
            System.out.println("6. Display Doctor Details");
            System.out.println("7. Delete Doctor");
            System.out.println("8. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            try {
                switch (choice) {
                    case 1:
                        System.out.println("\nEnter Patient Details:");
                        System.out.print("ID: ");
                        int patientID = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Name: ");
                        String patientName = scanner.nextLine();
                        System.out.print("Age: ");
                        int patientAge = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Gender: ");
                        String patientGender = scanner.nextLine();
                        System.out.print("Contact Number: ");
                        String patientContact = scanner.nextLine();

                        Patient patient = new Patient(patientID, patientName, patientAge, patientGender, patientContact);
                        DatabaseManager.createPatient(patient);
                        break;

                    case 2:
                        System.out.println("\nEnter Doctor Details:");
                        System.out.print("ID: ");
                        int doctorID = scanner.nextInt();
                        scanner.nextLine();
                        System.out.print("Name: ");
                        String doctorName = scanner.nextLine();
                        System.out.print("Specialization: ");
                        String specialization = scanner.nextLine();
                        System.out.print("Contact Number: ");
                        String doctorContact = scanner.nextLine();

                        Doctor doctor = new Doctor(doctorID, doctorName, specialization, doctorContact);
                        DatabaseManager.createDoctor(doctor);
                        break;
                        
                    case 3:
                        System.out.println("\nEnter Appointment Details:");
                        System.out.print("Appointment ID: ");
                        int appointmentID = scanner.nextInt();
                        scanner.nextLine();
                        
                        System.out.print("Patient ID: ");
                        int apptPatientID = scanner.nextInt();
                        scanner.nextLine();
                        Patient apptPatient = DatabaseManager.getPatientById(apptPatientID);
                        if (apptPatient == null) {
                            System.out.println("Patient not found!");
                            break;
                        }
                        
                        System.out.print("Doctor ID: ");
                        int apptDoctorID = scanner.nextInt();
                        scanner.nextLine();
                        Doctor apptDoctor = DatabaseManager.getDoctorById(apptDoctorID);
                        if (apptDoctor == null) {
                            System.out.println("Doctor not found!");
                            break;
                        }
                        
                        System.out.print("Appointment Date (YYYY-MM-DD): ");
                        String appointmentDate = scanner.nextLine();
                        
                        Appointment appointment = new Appointment(appointmentID, apptPatient, apptDoctor, appointmentDate);
                        DatabaseManager.createAppointment(appointment);
                        appointment.displayDetails();
                        break;
                        
                    case 4:
                        System.out.print("\nEnter Patient ID: ");
                        int medPatientID = scanner.nextInt();
                        scanner.nextLine();
                        
                        Patient medPatient = DatabaseManager.getPatientById(medPatientID);
                        if (medPatient == null) {
                            System.out.println("Patient not found!");
                            break;
                        }
                        
                        System.out.print("Enter medical condition to add: ");
                        String condition = scanner.nextLine();
                        
                        medPatient.addMedicalCondition(condition);
                        DatabaseManager.createPatient(medPatient); // Updates the existing record
                        System.out.println("Medical condition added successfully.");
                        break;
                        
                    case 5:
                        System.out.print("\nEnter Patient ID to display: ");
                        int dispPatientID = scanner.nextInt();
                        Patient dispPatient = DatabaseManager.getPatientById(dispPatientID);
                        if (dispPatient == null) {
                            System.out.println("Patient not found!");
                        } else {
                            dispPatient.displayDetails();
                        }
                        break;
                        
                    case 6:
                        System.out.print("\nEnter Doctor ID to display: ");
                        int dispDoctorID = scanner.nextInt();
                        Doctor dispDoctor = DatabaseManager.getDoctorById(dispDoctorID);
                        if (dispDoctor == null) {
                            System.out.println("Doctor not found!");
                        } else {
                            dispDoctor.displayDetails();
                        }
                        break;

                    case 7:
                        System.out.print("\nEnter Doctor ID to delete: ");
                        int deleteDoctorID = scanner.nextInt();
                        DatabaseManager.deleteDoctor(deleteDoctorID);
                        break;

                    case 8:
                        System.out.println("Exiting the system. Thank you!");
                        break;

                    default:
                        System.out.println("Invalid choice! Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        } while (choice != 8);

        scanner.close();
    }
}