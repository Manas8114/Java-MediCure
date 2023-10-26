import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MedicalApp {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";
    private static final String ADMIN_PASSWORD = "admin123";

    private JFrame frame;
    private JTextField nameField;
    private JTextField ageField;
    private JTextField symptomField;
    private JButton insertButton;
    private JButton deleteButton;
    private JButton showCureMedicineButton;
    private JPasswordField adminPasswordField;
    private JButton adminLoginButton;
    private JTextArea displayArea;
    private JTextArea cureMedicineTextArea;

    private boolean isAdminLoggedIn = false;

    public MedicalApp() {
        initializeAdminLoginWindow();
    }

    private void initializeAdminLoginWindow() {
        JFrame adminLoginFrame = new JFrame("Admin Login");
        adminLoginFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        adminLoginFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel adminLoginPanel = new JPanel();
        adminLoginPanel.setLayout(new GridLayout(2, 2));

        JLabel adminLabel = new JLabel("Admin Password:");
        adminLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        adminPasswordField = new JPasswordField(50);
        adminLoginButton = new JButton("Admin Login");
        adminLoginButton.setFont(new Font("Arial", Font.BOLD | Font.ITALIC, 24));
        adminLoginButton.setBackground(new Color(0, 255, 51));

        adminLoginPanel.add(adminLabel);
        adminLoginPanel.add(adminPasswordField);
        adminLoginPanel.add(adminLoginButton);

        adminLoginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adminLogin();
                if (isAdminLoggedIn) {
                    adminLoginFrame.dispose();
                    initializeMainApplication();
                }
            }
        });

        adminLoginFrame.add(adminLoginPanel);
        adminLoginFrame.setVisible(true);
    }

    private void initializeMainApplication() {
        frame = new JFrame("Medical App");
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel patientEntryPanel = createPatientEntryPanel();

        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane displayScrollPane = new JScrollPane(displayArea);

        frame.add(patientEntryPanel, BorderLayout.NORTH);
        frame.add(displayScrollPane, BorderLayout.CENTER);

        frame.setVisible(true);
    }

    private JPanel createPatientEntryPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2));
        panel.setOpaque(false);

        JLabel nameLabel = new JLabel("Name:");
        nameField = new JTextField(100);
        JLabel ageLabel = new JLabel("Age:");
        ageField = new JTextField(100);
        JLabel symptomLabel = new JLabel("Symptoms:");
        symptomField = new JTextField(100);
        insertButton = new JButton("Insert");
        deleteButton = new JButton("Delete Patient");
        showCureMedicineButton = new JButton("Show Cure and Medicine");

        insertButton.setBackground(Color.GREEN);
        deleteButton.setBackground(Color.RED);
        showCureMedicineButton.setBackground(Color.BLUE);

        panel.add(nameLabel);
        panel.add(nameField);
        panel.add(ageLabel);
        panel.add(ageField);
        panel.add(symptomLabel);
        panel.add(symptomField);
        panel.add(new JLabel());
        panel.add(insertButton);
        panel.add(new JLabel());
        panel.add(deleteButton);
        panel.add(showCureMedicineButton);

        insertButton.addActionListener(e -> insertPatientData());
        deleteButton.addActionListener(e -> deletePatientData());
        showCureMedicineButton.addActionListener(e -> showCureAndMedicineFromDiseaseCureTable());

        return panel;
    }

    private void insertPatientData() {
        String name = nameField.getText();
        String age = ageField.getText();
        String symptoms = symptomField.getText();

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = con
                        .prepareStatement("INSERT INTO patients (name, age, symptoms) VALUES (?, ?, ?)")) {
            statement.setString(1, name);
            statement.setString(2, age);
            statement.setString(3, symptoms);
            statement.executeUpdate();
            JOptionPane.showMessageDialog(frame, "Patient data inserted successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            displayPatientData();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }

        nameField.setText("");
        ageField.setText("");
        symptomField.setText("");
    }

    private void deletePatientData() {
        if (!isAdminLoggedIn) {
            JOptionPane.showMessageDialog(frame, "Please log in as admin first.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String patientID = JOptionPane.showInputDialog(frame, "Enter the ID of the patient to delete:");
        if (patientID == null || patientID.isEmpty()) {
            return;
        }

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = con.prepareStatement("DELETE FROM patients WHERE id = ?")) {
            statement.setInt(1, Integer.parseInt(patientID));
            int deletedRows = statement.executeUpdate();
            if (deletedRows > 0) {
                JOptionPane.showMessageDialog(frame, "Patient data deleted successfully.", "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                displayPatientData();
            } else {
                JOptionPane.showMessageDialog(frame, "Patient not found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException | NumberFormatException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adminLogin() {
        String password = new String(adminPasswordField.getPassword());

        if (password.equals(ADMIN_PASSWORD)) {
            isAdminLoggedIn = true;
            JOptionPane.showMessageDialog(frame, "Admin logged in successfully.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            isAdminLoggedIn = false;
            JOptionPane.showMessageDialog(frame, "Invalid admin password.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayPatientData() {
        StringBuilder result = new StringBuilder();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = con.prepareStatement("SELECT * FROM patients");
                ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                String symptoms = resultSet.getString("symptoms");
                result.append("ID: ").append(id).append("\n");
                result.append("Name: ").append(name).append("\n");
                result.append("Age: ").append(age).append("\n");
                result.append("Symptoms: ").append(symptoms).append("\n\n");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
        displayArea.setText(result.toString());
    }

    private void showCureAndMedicineFromDiseaseCureTable() {
        String symptom = symptomField.getText();
        cureMedicineTextArea = new JTextArea();
        cureMedicineTextArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(cureMedicineTextArea);

        JFrame cureMedicineFrame = new JFrame("Cure and Medicine");
        cureMedicineFrame.setSize(800, 400);
        cureMedicineFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        cureMedicineFrame.add(scrollPane);

        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = con
                        .prepareStatement("SELECT Symptoms, Cure, Medicine FROM DiseaseCure WHERE Symptoms = ?")) {

            statement.setString(1, symptom);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder result = new StringBuilder();
            result.append("Cure and Medicine for Disease(s) related to the symptom:\n");

            boolean dataFound = false;

            while (resultSet.next()) {
                String diseaseName = resultSet.getString("Symptoms");
                String cure = resultSet.getString("Cure");
                String medicine = resultSet.getString("Medicine");

                result.append("Disease Name: ").append(diseaseName).append("\n");
                result.append("Cure: ").append(cure).append("\n");
                result.append("Medicine: ").append(medicine).append("\n\n");

                dataFound = true;
            }

            if (!dataFound) {
                JOptionPane.showMessageDialog(frame,
                        "No related disease, cure, and medicine information found for the symptom.",
                        "No Data", JOptionPane.INFORMATION_MESSAGE);
            } else {
                cureMedicineTextArea.setText(result.toString());
                cureMedicineFrame.setVisible(true);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(frame, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MedicalApp::new);
    }
}