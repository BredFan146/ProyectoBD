import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class GestionCursos extends JPanel { // Extiende JPanel en lugar de JFrame
    private Connection connection;

    public GestionCursos(Connection connection) {
        this.connection = connection;
        setLayout(new BorderLayout()); // Usa un layout para la disposición de los componentes

        // Definir columnas
        String[] columnNames = {"ID", "Nombre", "Descripción", "Costo"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0);

        try {
            String sql = "SELECT id, name, description, cost FROM courses";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()) {
                Object[] row = {
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("description"),
                        rs.getDouble("cost")
                };
                model.addRow(row);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }
}
