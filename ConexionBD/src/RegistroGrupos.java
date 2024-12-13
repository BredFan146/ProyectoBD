import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.*;

public class RegistroGrupos extends JFrame {
    private Connection connection;

    public RegistroGrupos(Connection connection) {
        this.connection = connection;
        setTitle("Registro de Grupos");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JTextField cursoIdField = new JTextField();
        JTextField horarioField = new JTextField();
        JTextField salonField = new JTextField();

        Object[] mensaje = {
                "ID del Curso:", cursoIdField,
                "Horario:", horarioField,
                "Salón:", salonField
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Grupo", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String sql = "INSERT INTO groups (course_id, schedule, room) VALUES (?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, Integer.parseInt(cursoIdField.getText()));
                pstmt.setString(2, horarioField.getText());
                pstmt.setString(3, salonField.getText());
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Grupo registrado exitosamente.");

                // Mostrar los grupos después de registrar el nuevo grupo
                mostrarGrupos();

            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error: " + e.getMessage());
            }
        }
    }

    private void mostrarGrupos() {
        try {
            // Consulta SQL para obtener los grupos registrados
            String sql = "SELECT g.id AS Grupo, c.name AS Curso, g.schedule AS Horario, g.room AS Salon " +
                    "FROM groups g " +
                    "JOIN courses c ON g.course_id = c.id";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            // Verificar si se obtienen resultados
            if (!rs.isBeforeFirst()) {
                JOptionPane.showMessageDialog(this, "No se encontraron grupos registrados.", "Información", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            // Mostrar resultados en una tabla
            String[] columnNames = {"Grupo", "Curso", "Horario", "Salón"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            while (rs.next()) {
                model.addRow(new Object[]{
                        rs.getInt("Grupo"),
                        rs.getString("Curso"),
                        rs.getString("Horario"),
                        rs.getString("Salon")
                });
            }

            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            JFrame frame = new JFrame("Grupos Registrados");
            frame.add(scrollPane);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al obtener los grupos.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
