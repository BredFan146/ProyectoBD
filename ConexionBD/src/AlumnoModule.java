import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AlumnoModule extends JFrame {
    private Connection connection;
    private String alumnoUser;

    public AlumnoModule(Connection connection, String alumnoUser) {
        this.connection = connection;
        this.alumnoUser = alumnoUser;

        // Configuración de la ventana
        setTitle("Módulo de Alumno");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel bienvenidaLabel = new JLabel("Bienvenido, " + alumnoUser, SwingConstants.CENTER);
        bienvenidaLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Botones
        JButton verCalificacionesButton = new JButton("Ver Calificaciones");
        verCalificacionesButton.addActionListener(e -> {
            // Suponiendo que tienes una variable llamada 'alumnoEmail' que almacena el email del alumno actual
            String alumnoEmail = "estudiante1@example.com"; // Este es solo un ejemplo, debes obtenerlo según tu sistema
            verCalificaciones(alumnoEmail);
        });


        JButton cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(e -> cerrarSesion());

        // Agregar botones al panel
        mainPanel.add(bienvenidaLabel);
        mainPanel.add(verCalificacionesButton);
        mainPanel.add(cerrarSesionButton);

        add(mainPanel);
    }

    // Método para ver las calificaciones del alumno
    private void verCalificaciones(String emailAlumno) {
        try {
            // Consulta SQL para obtener las calificaciones del alumno
            String sql = "SELECT DISTINCT c.name AS Curso, g.grade AS Calificación " +
                    "FROM grades g " +
                    "JOIN courses c ON g.course_id = c.id " +
                    "JOIN students s ON g.student_id = s.id " +
                    "JOIN users u ON s.user_id = u.id " +
                    "WHERE u.email = ?"; // Filtro por email del alumno

            // Preparar la consulta
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, emailAlumno); // Usar el email del usuario como filtro único
            ResultSet rs = pstmt.executeQuery();

            // Modelo para la tabla
            String[] columnNames = {"Curso", "Calificación"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Llenar el modelo con los datos obtenidos
            while (rs.next()) {
                Object[] row = new Object[columnNames.length];
                row[0] = rs.getString("Curso");
                row[1] = rs.getDouble("Calificación");
                model.addRow(row);
            }

            // Verificar si hay datos para mostrar
            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "No se encontraron calificaciones para el alumno.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Crear la tabla y el frame
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            JFrame frame = new JFrame("Calificaciones");
            frame.add(scrollPane);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener las calificaciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    // Método para cerrar sesión
    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(
                this, "¿Está seguro que desea cerrar sesión?", "Cerrar Sesión", JOptionPane.YES_NO_OPTION
        );
        if (respuesta == JOptionPane.YES_OPTION) {
            this.dispose();
            SwingUtilities.invokeLater(() -> new ConexionBD().setVisible(true));
        }
    }
}
