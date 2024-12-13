import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MostrarUsuarios extends JFrame {
    public MostrarUsuarios(DataFetcher dataFetcher) {
        setTitle("Lista de Usuarios");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Obtener datos de usuarios
        List<String[]> usuarios = dataFetcher.obtenerUsuarios();

        // Definir columnas
        String[] columnNames = {"ID", "Nombre", "Email", "Rol"};

        // Verificar si hay usuarios
        if (usuarios.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay usuarios registrados en la base de datos.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Convertir datos para la tabla
        String[][] data = usuarios.toArray(new String[0][]);

        // Crear un modelo de tabla no editable
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Hace que todas las celdas sean no editables
            }
        };

        // Crear JTable con el modelo
        JTable table = new JTable(model);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 12));
        table.setRowHeight(20);

        // Añadir la tabla a un JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);

        // Añadir el JScrollPane al JFrame
        add(scrollPane, BorderLayout.CENTER);
    }
}


