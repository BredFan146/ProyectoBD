import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GestionAlumnos extends JFrame {
    private Connection connection;

    public GestionAlumnos(Connection connection) {
        this.connection = connection;

        // Configuración de la ventana
        setTitle("Gestión de Alumnos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tabla para mostrar alumnos
        String[] columnNames = {"ID", "Nombre", "Email", "Rol"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            String sql = "SELECT id, name, email, role FROM users WHERE role = 'alumno'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Botón para registrar nuevo alumno
        JButton registrarAlumnoButton = new JButton("Registrar Alumno");
        registrarAlumnoButton.addActionListener(e -> registrarAlumno());

        // Panel para organizar los componentes
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(registrarAlumnoButton, BorderLayout.SOUTH);

        add(panel);
    }

    private void registrarAlumno() {
        JTextField nombreField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();

        Object[] mensaje = {
                "Nombre del Alumno:", nombreField,
                "Email:", emailField,
                "Contraseña:", passField
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Alumno", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String sql = "INSERT INTO users (name, email, pass, role) VALUES (?, ?, ?, 'alumno')";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nombre);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Alumno registrado exitosamente.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al registrar el alumno.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}


