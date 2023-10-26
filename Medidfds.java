import java.awt.CardLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;

public class Medidfds extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/data";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1234";

    private JPanel contentPane;
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JPanel patientPanel;
    private JTextField txtPatientName;
    private JTextField txtSymptoms;
    private JButton btnSubmit;
    private JTextField txtPreventionCure;
    private JLabel lblPreventionCure;

    public Medidfds() {
        setTitle("MediCure");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 450, 300);
        contentPane = new JPanel();
        contentPane.setLayout(new CardLayout());
        setContentPane(contentPane);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.Y_AXIS));

        JLabel lblUsername = new JLabel("Username:");
        loginPanel.add(lblUsername);

        txtUsername = new JTextField();
        loginPanel.add(txtUsername);

        JLabel lblPassword = new JLabel("Password:");
        loginPanel.add(lblPassword);

        txtPassword = new JPasswordField();
        loginPanel.add(txtPassword);

        ImageIcon saveimage = new ImageIcon("C:\\Users\\msgok\\Downloads\\java3.jpg");
        btnLogin = new JButton(saveimage);
        loginPanel.add(btnLogin);

        contentPane.add(loginPanel, "Login");

        patientPanel = new JPanel();
        patientPanel.setLayout(new BoxLayout(patientPanel, BoxLayout.Y_AXIS));

        JLabel lblPatientName = new JLabel("Patient Name:");
        patientPanel.add(lblPatientName);

        txtPatientName = new JTextField();
        patientPanel.add(txtPatientName);

        JLabel lblSymptoms = new JLabel("Symptoms:");
        patientPanel.add(lblSymptoms);

        txtSymptoms = new JTextField();
        patientPanel.add(txtSymptoms);

        ImageIcon submitImageIcon = new ImageIcon("C:\\Users\\msgok\\Downloads\\java2.jpg");
        btnSubmit = new JButton(submitImageIcon);
        patientPanel.add(btnSubmit);
        btnSubmit.setBounds(0, 0, 62, 42);

        lblPreventionCure = new JLabel("Prevention and Cure:");
        patientPanel.add(lblPreventionCure);

        txtPreventionCure = new JTextField();
        patientPanel.add(txtPreventionCure);

        createShowDatabaseButton();

        contentPane.add(patientPanel, "Patient");

        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String username = txtUsername.getText();
                String password = new String(txtPassword.getPassword());
                if (username.equals("admin") && password.equals("password")) {
                    ((CardLayout) contentPane.getLayout()).show(contentPane, "Patient");
                } else {
                    JOptionPane.showMessageDialog(Medidfds.this, "Invalid username or password", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        btnSubmit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String patientName = txtPatientName.getText();
                String symptoms = txtSymptoms.getText();

                try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                        PreparedStatement statement = con
                                .prepareStatement("INSERT INTO patients (name, symptoms) VALUES (?, ?)")) {
                    statement.setString(1, patientName);
                    statement.setString(2, symptoms);
                    statement.executeUpdate();
                    JOptionPane.showMessageDialog(Medidfds.this, "Patient data inserted successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException ex) {
                    JOptionPane.showMessageDialog(Medidfds.this, "Database error: " + ex.getMessage(), "Error",
                            JOptionPane.ERROR_MESSAGE);
                }

                String symptom = txtSymptoms.getText();
                queryDiseaseCure(symptom);
            }
        });
    }

    private void showPatientDatabase() {
        StringBuilder result = new StringBuilder();
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = con.prepareStatement("SELECT * FROM patients");
                ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String symptoms = resultSet.getString("symptoms");

                if (result.length() > 0) {
                    result.append("\n");
                }

                result.append("ID: ").append(id).append("\n");
                result.append("Name: ").append(name).append("\n");
                result.append("Symptoms: ").append(symptoms).append("\n\n");
            }

            if (result.length() == 0) {
                result.append("No patient data found.");
            }

            txtPreventionCure.setText(result.toString());
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void createShowDatabaseButton() {
        JPanel buttonPanel = new JPanel();
        ImageIcon showDatabaseImageIcon = new ImageIcon("C:\\Users\\msgok\\Downloads\\java4.png");
        JButton btnShowDatabase = new JButton(showDatabaseImageIcon);
        buttonPanel.add(btnShowDatabase);
        patientPanel.add(buttonPanel);

        btnShowDatabase.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPatientDatabase();
            }
        });
    }

    private void queryDiseaseCure(String symptom) {
        try (Connection con = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement statement = con
                        .prepareStatement("SELECT Symptoms, Cure, Medicine FROM DiseaseCure WHERE Symptoms = ?")) {
            statement.setString(1, symptom);
            ResultSet resultSet = statement.executeQuery();

            StringBuilder result = new StringBuilder();
            result.append("");

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
                result.append("No related disease, cure, and medicine information found for the symptom.");
            }

            txtPreventionCure.setText(result.toString());
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(Medidfds.this, "Database error: " + ex.getMessage(), "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Medidfds frame = new Medidfds();
                frame.setVisible(true);
            }
        });
    }
}