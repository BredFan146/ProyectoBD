import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MostrarCursos extends JFrame {
    public MostrarCursos(DataFetcher dataFetcher) {
        setTitle("Lista de Cursos");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Obtener datos de cursos
        List<String[]> cursos = dataFetcher.obtenerCursos();

        // Definir columnas y datos
        String[] columnNames = {"ID", "Nombre", "Descripción", "Costo"};
        String[][] data = cursos.toArray(new String[0][]);

        // Crear JTable
        JTable table = new JTable(new DefaultTableModel(data, columnNames));
        JScrollPane scrollPane = new JScrollPane(table);

        // Añadir la tabla a la ventana
        add(scrollPane, BorderLayout.CENTER);
    }
}

