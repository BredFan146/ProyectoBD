import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class RegistroCalificaciones extends JFrame {
    private Connection connection;

    public RegistroCalificaciones(Connection connection) {
        this.connection = connection;
        setTitle("Registro de Calificaciones");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField inscripcionIdField = new JTextField();
        JTextField calificacionField = new JTextField();
        JTextField courseField = new JTextField();  // Campo para el curso

        Object[] mensaje = {
                "ID de Inscripción:", inscripcionIdField,
                "Calificación:", calificacionField,
                "ID de Curso:", courseField  // Añadimos el campo de curso
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Calificación", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                // Obtener el student_id a partir del inscripcion_id
                int inscripcionId = Integer.parseInt(inscripcionIdField.getText());
                String sqlGetStudentId = "SELECT student_id FROM inscriptions WHERE id = ?";
                PreparedStatement pstmtGetStudentId = connection.prepareStatement(sqlGetStudentId);
                pstmtGetStudentId.setInt(1, inscripcionId);
                ResultSet rs = pstmtGetStudentId.executeQuery();

                if (rs.next()) {
                    int studentId = rs.getInt("student_id");
                    int courseId = Integer.parseInt(courseField.getText()); // Obtener el ID del curso desde el campo de texto

                    // Insertar la calificación en la tabla grades
                    String sqlInsertGrade = "INSERT INTO grades (student_id, course_id, grade) VALUES (?, ?, ?)";
                    PreparedStatement pstmtInsertGrade = connection.prepareStatement(sqlInsertGrade);
                    pstmtInsertGrade.setInt(1, studentId);
                    pstmtInsertGrade.setInt(2, courseId);  // Usamos el curso ingresado por el usuario
                    pstmtInsertGrade.setDouble(3, Double.parseDouble(calificacionField.getText()));
                    pstmtInsertGrade.executeUpdate();

                    JOptionPane.showMessageDialog(this, "Calificación registrada exitosamente.");

                    // Mostrar las calificaciones después de registrar la nueva calificación
                    mostrarCalificaciones(studentId);

                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró la inscripción con ese ID.");
                }

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void mostrarCalificaciones(int studentId) {
        try {
            // Consulta SQL para obtener las calificaciones del alumno
            String sql = "SELECT distinct c.name AS Curso, g.grade AS Calificación " +
                    "FROM grades g " +
                    "JOIN courses c ON g.course_id = c.id " +
                    "WHERE g.student_id = ?";  // Usamos el ID del estudiante para obtener las calificaciones

            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setInt(1, studentId);

            ResultSet rs = pstmt.executeQuery();

            // Verifica si se obtienen resultados
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "No se encontraron calificaciones para el alumno.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Mostrar resultados en una tabla
            String[] columnNames = {"Curso", "Calificación"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getString("Curso"),
                        rs.getDouble("Calificación")
                });
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            JFrame frame = new JFrame("Calificaciones");
            frame.add(scrollPane);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener las calificaciones.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
