import javax.swing.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;

public class RegistroInscripciones extends JFrame {
    private Connection connection;

    public RegistroInscripciones(Connection connection) {
        this.connection = connection;
        setTitle("Registro de Inscripciones");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField alumnoIdField = new JTextField();
        JTextField grupoIdField = new JTextField();
        JTextField fechaField = new JTextField();

        Object[] mensaje = {
                "ID del Alumno:", alumnoIdField,
                "ID del Grupo:", grupoIdField,
                "Fecha de Ingreso:", fechaField,
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Inscripción", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                // Insertar la inscripción en la base de datos
                String sql = "INSERT INTO inscriptions (student_id, group_id, inscription_date) VALUES (?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(alumnoIdField.getText()));  // Establecer ID de alumno
                pstmt.setInt(2, Integer.parseInt(grupoIdField.getText()));   // Establecer ID de grupo
                pstmt.setDate(3, Date.valueOf(fechaField.getText()));        // Establecer fecha de inscripción
                pstmt.executeUpdate();

                // Mensaje de éxito
                JOptionPane.showMessageDialog(this, "Inscripción registrada exitosamente.");

                // Mostrar las inscripciones después de registrar
                mostrarInscripciones();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void mostrarInscripciones() {
        try {
            String sql = "SELECT u.name AS Alumno, c.name AS Curso, g.schedule AS Horario, g.room AS Salon " +
                    "FROM inscriptions i " +
                    "JOIN students s ON i.student_id = s.id " +
                    "JOIN users u ON s.user_id = u.id " +
                    "JOIN groups g ON i.group_id = g.id " +
                    "JOIN courses c ON g.course_id = c.id";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            // Crear el modelo de la tabla
            String[] columnNames = {"Alumno", "Curso", "Horario", "Salón"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Agregar las filas con los resultados
            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Alumno"),
                        rs.getString("Curso"),
                        rs.getString("Horario"),
                        rs.getString("Salon")
                });
            }

            // Mostrar los resultados en un JFrame
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            JFrame frame = new JFrame("Lista de Inscripciones");
            frame.add(scrollPane);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error al mostrar las inscripciones: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
