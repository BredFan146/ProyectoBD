import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistroUsuario extends JFrame {

    private Connection connection;

    public RegistroUsuario(Connection connection) {
        this.connection = connection;
        setTitle("Registro de Usuario");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 2, 10, 10));

        JLabel nombreLabel = new JLabel("Nombre:");
        JTextField nombreField = new JTextField();
        panel.add(nombreLabel);
        panel.add(nombreField);

        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();
        panel.add(emailLabel);
        panel.add(emailField);

        JLabel passLabel = new JLabel("Contraseña:");
        JPasswordField passField = new JPasswordField();
        panel.add(passLabel);
        panel.add(passField);

        JLabel rolLabel = new JLabel("Rol:");
        JComboBox<String> rolComboBox = new JComboBox<>(new String[]{"Administrador", "Empleado", "Alumno"});
        panel.add(rolLabel);
        panel.add(rolComboBox);

        JButton registrarButton = new JButton("Registrar");
        JButton cancelarButton = new JButton("Cancelar");

        registrarButton.addActionListener(e -> {
            if (validarCampos(nombreField, emailField, passField, rolComboBox)) {
                // Verificar que el email no esté registrado antes de registrar el usuario
                if (!emailExistente(emailField.getText())) {
                    registrarUsuario(nombreField.getText(), emailField.getText(), new String(passField.getPassword()), rolComboBox.getSelectedItem().toString());
                }
            }
        });

        cancelarButton.addActionListener(e -> dispose());

        panel.add(registrarButton);
        panel.add(cancelarButton);

        add(panel);
    }

    private boolean validarCampos(JTextField nombreField, JTextField emailField, JPasswordField passField, JComboBox<String> rolComboBox) {
        if (nombreField.getText().isEmpty() || emailField.getText().isEmpty() || new String(passField.getPassword()).isEmpty()) {
            JOptionPane.showMessageDialog(this, "Por favor, complete todos los campos.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!emailField.getText().contains("@")) {
            JOptionPane.showMessageDialog(this, "Por favor, ingrese un email válido.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (rolComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Por favor, seleccione un rol.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean emailExistente(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                JOptionPane.showMessageDialog(this, "Este email ya está registrado.", "Error", JOptionPane.ERROR_MESSAGE);
                return true;
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al verificar el email.", "Error", JOptionPane.ERROR_MESSAGE);
        }
        return false;
    }

    private void registrarUsuario(String nombre, String email, String password, String role) {
        String sql = "INSERT INTO users (name, email, pass, role) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            pstmt.setString(2, email);
            pstmt.setString(3, password);  // Guardar la contraseña tal como es (sin encriptar)
            pstmt.setString(4, role);

            pstmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.");
            dispose();
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al registrar el usuario.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
