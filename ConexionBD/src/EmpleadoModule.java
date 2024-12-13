import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class EmpleadoModule extends JFrame {
    private Connection connection;
    private String empleadoUser;

    public EmpleadoModule(Connection connection, String empleadoUser) {
        this.connection = connection;
        this.empleadoUser = empleadoUser;

        // Configuración de la ventana
        setTitle("Módulo de Empleados");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel bienvenidaLabel = new JLabel("Bienvenido, " + empleadoUser, SwingConstants.CENTER);
        bienvenidaLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Botones
        JButton registrarInscripcionesButton = new JButton("Registrar Inscripciones");
        registrarInscripcionesButton.addActionListener(e -> abrirRegistroInscripciones());

        JButton registrarCalificacionesButton = new JButton("Registrar Calificaciones");
        registrarCalificacionesButton.addActionListener(e -> abrirRegistroCalificaciones());

        JButton registrarGruposButton = new JButton("Registrar Grupos");
        registrarGruposButton.addActionListener(e -> abrirRegistroGrupos());

        JButton cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(e -> cerrarSesion());

        // Agregar botones al panel
        mainPanel.add(bienvenidaLabel);
        mainPanel.add(registrarInscripcionesButton);
        mainPanel.add(registrarCalificacionesButton);
        mainPanel.add(registrarGruposButton);
        mainPanel.add(cerrarSesionButton);

        add(mainPanel);
    }

    private void abrirRegistroInscripciones() {
        new RegistroInscripciones(connection).setVisible(true);
    }

    private void abrirRegistroCalificaciones() {
        new RegistroCalificaciones(connection).setVisible(true);
    }

    private void abrirRegistroGrupos() {
        new RegistroGrupos(connection).setVisible(true);
    }

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
