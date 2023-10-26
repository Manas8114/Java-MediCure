import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;

public class MedicalApp extends JFrame {
    private static final String ADMIN_PASSWORD = "admin123";

    private BufferedImage background;
    private JPanel currentPanel;
    private boolean isAdminLoggedIn = false;

    public MedicalApp() {
        setTitle("Medical App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Load background image
        try {
            background = ImageIO.read(new File("background.jpg"));
            background = resizeImage(background, 800, 600);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Create the login panel
        currentPanel = new LoginPanel();
        setContentPane(currentPanel);

        setVisible(true);
    }

    public void showAdminDashboard() {
        if (isAdminLoggedIn) {
            currentPanel = new AdminDashboard();
            setContentPane(currentPanel);
            revalidate();
            repaint();
        } else {
            JOptionPane.showMessageDialog(this, "Please log in as admin first.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void showPatientEntryPanel() {
        currentPanel = new PatientEntryPanel();
        setContentPane(currentPanel);
        revalidate();
        repaint();
    }

    private BufferedImage resizeImage(BufferedImage image, int width, int height) {
        Image temp = image.getScaledInstance(width, height, Image.SCALE_SMOOTH);
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();
        g2d.drawImage(temp, 0, 0, null);
        g2d.dispose();
        return resized;
    }

    private class LoginPanel extends JPanel {
        private JPasswordField passwordField;

        public LoginPanel() {
            setLayout(new BorderLayout());

            JLabel loginLabel = new JLabel("Admin Login");
            loginLabel.setFont(new Font("Arial", Font.BOLD, 24));
            loginLabel.setHorizontalAlignment(JLabel.CENTER);

            JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            passwordField = new JPasswordField(20);
            JButton loginButton = new JButton("Login");
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    adminLogin();
                }
            });

            inputPanel.add(new JLabel("Password:"));
            inputPanel.add(passwordField);
            inputPanel.add(loginButton);

            add(loginLabel, BorderLayout.NORTH);
            add(inputPanel, BorderLayout.CENTER);

            setOpaque(false);
        }

        private void adminLogin() {
            String password = new String(passwordField.getPassword());
            if (password.equals(ADMIN_PASSWORD)) {
                isAdminLoggedIn = true;
                showAdminDashboard();
            } else {
                isAdminLoggedIn = false;
                JOptionPane.showMessageDialog(this, "Invalid admin password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class AdminDashboard extends JPanel {
        public AdminDashboard() {
            setLayout(new FlowLayout());

            JButton patientEntryButton = new JButton("Patient Entry");
            patientEntryButton.setFont(new Font("Arial", Font.PLAIN, 18));
            patientEntryButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    showPatientEntryPanel();
                }
            });

            add(patientEntryButton);

            setOpaque(false);
        }
    }

    private class PatientEntryPanel extends JPanel {
        private JTextField nameField;
        private JTextField ageField;
        private JTextField symptomField;

        public PatientEntryPanel() {
            setLayout(new GridLayout(4, 2));
            setOpaque(false);

            JLabel nameLabel = new JLabel("Name:");
            nameField = new JTextField();
            JLabel ageLabel = new JLabel("Age:");
            ageField = new JTextField();
            JLabel symptomLabel = new JLabel("Symptoms:");
            symptomField = new JTextField();
            JButton submitButton = new JButton("Submit");

            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    insertPatientData();
                }
            });

            add(nameLabel);
            add(nameField);
            add(ageLabel);
            add(ageField);
            add(symptomLabel);
            add(symptomField);
            add(new JLabel()); // Empty label for spacing
            add(submitButton);
        }

        private void insertPatientData() {
            String name = nameField.getText();
            String age = ageField.getText();
            String symptoms = symptomField.getText();

            // Perform database insertion here (similar to your original code)

            // Clear fields after submission
            nameField.setText("");
            ageField.setText("");
            symptomField.setText("");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MedicalApp();
            }
        });
    }
}

public class MedicalAppMain {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MedicalApp();
            }
        });
    }
}
