import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GestionEmpleados extends JFrame {
    private Connection connection;

    public GestionEmpleados(Connection connection) {
        this.connection = connection;
        setTitle("Gestión de Empleados");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Definir columnas
        String[] columnNames = {"ID", "Nombre", "Email", "Rol", "Sueldo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            // Consulta modificada con JOIN para acceder a la columna salary de la tabla employees
            String sql = "SELECT u.id, u.name, u.email, u.role, e.salary " +
                    "FROM users u " +
                    "JOIN employees e ON u.id = e.user_id " +
                    "WHERE u.role IN ('empleado', 'maestro')";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role"),
                        rs.getDouble("salary")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener los datos de los empleados: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Crear la tabla
        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);

        // Botón para registrar un nuevo empleado
        JButton registrarEmpleadoButton = new JButton("Registrar Empleado");
        registrarEmpleadoButton.addActionListener(e -> registrarEmpleado());

        // Panel para organizar los componentes
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(registrarEmpleadoButton, BorderLayout.SOUTH);

        add(panel);
    }

    private void registrarEmpleado() {
        JTextField nombreField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JTextField sueldoField = new JTextField();

        Object[] mensaje = {
                "Nombre del Empleado:", nombreField,
                "Email:", emailField,
                "Contraseña:", passField,
                "Sueldo:", sueldoField
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Empleado", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String sueldoStr = sueldoField.getText().trim();

            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || sueldoStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Intentar convertir el sueldo a double
            double sueldo = 0;
            try {
                sueldo = Double.parseDouble(sueldoStr);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "El sueldo debe ser un número válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection con = null;
            PreparedStatement pstmt = null;
            PreparedStatement pstmtEmpleado = null;
            ResultSet rs = null;

            try {
                // Iniciar transacción
                con = connection;
                con.setAutoCommit(false);  // Desactivamos el autocommit para poder hacer rollback si ocurre un error

                // Insertar en la tabla users
                String sql = "INSERT INTO users (name, email, pass, role) VALUES (?, ?, ?, 'empleado')";
                pstmt = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                pstmt.setString(1, nombre);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.executeUpdate();

                // Obtener el ID del usuario recién insertado
                rs = pstmt.getGeneratedKeys();
                int userId = -1;
                if (rs.next()) {
                    userId = rs.getInt(1);
                }

                if (userId == -1) {
                    JOptionPane.showMessageDialog(this, "Error al obtener el ID del usuario.", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Insertar en la tabla employees con el ID del usuario
                String sqlEmpleado = "INSERT INTO employees (user_id, position, salary, email) VALUES (?, ?, ?,?)";
                String position = "maestro";
                pstmtEmpleado = con.prepareStatement(sqlEmpleado);
                pstmtEmpleado.setInt(1, userId);
                pstmtEmpleado.setString(2, position);
                pstmtEmpleado.setDouble(3, sueldo);
                pstmtEmpleado.setString(4, email);
                pstmtEmpleado.executeUpdate();

                // Confirmar la transacción
                con.commit();

                JOptionPane.showMessageDialog(this, "Empleado registrado exitosamente.");
            } catch (SQLException e) {
                // Si hay un error, revertir la transacción
                if (con != null) {
                    try {
                        con.rollback();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(this, "Error al hacer rollback de la transacción.", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                JOptionPane.showMessageDialog(this, "Error al registrar el empleado.", "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            } finally {
                // Cerrar los recursos
                try {
                    if (rs != null) rs.close();
                    if (pstmt != null) pstmt.close();
                    if (pstmtEmpleado != null) pstmtEmpleado.close();
                    if (con != null) con.setAutoCommit(true); // Restaurar el autocommit
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
