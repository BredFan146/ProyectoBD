import javax.swing.*;
import java.awt.*;
import java.sql.Connection;

public class MenuPrincipal extends JFrame {
    private Connection connection;
    private String usuarioActual;
    private String rolActual; // Nuevo campo para el rol del usuario

    // Constructor actualizado que incluye el rol
    public MenuPrincipal(Connection connection, String usuario, String rol) {
        this.connection = connection;
        this.usuarioActual = usuario;
        this.rolActual = rol;

        // Configuración de la ventana
        setTitle("Menú Principal - Sistema de Gestión");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(5, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Etiqueta de bienvenida
        JLabel bienvenidaLabel = new JLabel("Bienvenido, " + usuarioActual + " (" + rolActual + ")", SwingConstants.CENTER);
        bienvenidaLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Botones de opciones
        Font buttonFont = new Font("Arial", Font.BOLD, 16);

        // Botones específicos según el rol
        if (rol.equals("admin")) {
            JButton gestionUsuariosButton = new JButton("Gestión de Usuarios");
            gestionUsuariosButton.setFont(buttonFont);
            gestionUsuariosButton.addActionListener(e -> abrirGestionUsuarios());
            mainPanel.add(gestionUsuariosButton);
        }

        if (rol.equals("admin") || rol.equals("maestro")) {
            JButton gestionProductosButton = new JButton("Gestión de Productos");
            gestionProductosButton.setFont(buttonFont);
            gestionProductosButton.addActionListener(e -> abrirGestionProductos());
            mainPanel.add(gestionProductosButton);
        }

        if (rol.equals("alumno")) {
            JButton registroActividadesButton = new JButton("Registro de Actividades");
            registroActividadesButton.setFont(buttonFont);
            registroActividadesButton.addActionListener(e -> abrirRegistroActividades());
            mainPanel.add(registroActividadesButton);
        }

        // Botones comunes para todos los roles
        JButton reportesButton = new JButton("Reportes");
        reportesButton.setFont(buttonFont);
        reportesButton.addActionListener(e -> abrirReportes());
        mainPanel.add(reportesButton);

        JButton salirButton = new JButton("Cerrar Sesión");
        salirButton.setFont(buttonFont);
        salirButton.addActionListener(e -> cerrarSesion());
        mainPanel.add(salirButton);

        // Añadir componentes al panel
        mainPanel.add(bienvenidaLabel);

        // Añadir panel a la ventana
        add(mainPanel);
    }

    private void abrirGestionUsuarios() {
        DataFetcher dataFetcher = new DataFetcher(connection); // Crear DataFetcher con la conexión
        new MostrarUsuarios(dataFetcher).setVisible(true); // Abre la ventana MostrarUsuarios
    }


    private void abrirGestionProductos() {
        JOptionPane.showMessageDialog(this, "Módulo de Gestión de Productos");
        // Aquí podrías abrir una nueva ventana para gestionar productos
    }

    private void abrirRegistroActividades() {
        JOptionPane.showMessageDialog(this, "Módulo de Registro de Actividades");
        // Aquí podrías abrir una nueva ventana para registrar actividades
    }

    private void abrirReportes() {
        JOptionPane.showMessageDialog(this, "Módulo de Reportes");
        // Aquí podrías abrir una nueva ventana para generar reportes
    }

    private void cerrarSesion() {
        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Está seguro que desea cerrar sesión?",
                "Cerrar Sesión",
                JOptionPane.YES_NO_OPTION
        );

        if (respuesta == JOptionPane.YES_OPTION) {
            // Cerrar esta ventana
            this.dispose();

            // Volver a abrir la ventana de login
            SwingUtilities.invokeLater(() -> {
                ConexionBD loginSystem = new ConexionBD();
                loginSystem.setVisible(true);
            });
        }
    }

    // Método para iniciar el menú principal
    public static void mostrar(Connection connection, String usuario, String rol) {
        SwingUtilities.invokeLater(() -> {
            MenuPrincipal menu = new MenuPrincipal(connection, usuario, rol);
            menu.setVisible(true);
        });
    }
}

