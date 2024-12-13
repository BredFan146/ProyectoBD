import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AdminModule extends JFrame {
    private Connection connection;
    private String adminUser;

    public AdminModule(Connection connection, String adminUser) {
        this.connection = connection;
        this.adminUser = adminUser;

        // Configuración de la ventana
        setTitle("Módulo del Administrador");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel mainPanel = new JPanel(new GridLayout(4, 1, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel bienvenidaLabel = new JLabel("Bienvenido Administrador: " + adminUser, SwingConstants.CENTER);
        bienvenidaLabel.setFont(new Font("Arial", Font.BOLD, 20));

        // Botones de gestión
        JButton gestionEmpleadosButton = new JButton("Gestión de Empleados");
        gestionEmpleadosButton.addActionListener(e -> abrirGestionEmpleados());

        JButton gestionCursosButton = new JButton("Gestión de Cursos");
        gestionCursosButton.addActionListener(e -> abrirGestionCursos());

        JButton gestionAlumnosButton = new JButton("Gestión de Alumnos");
        gestionAlumnosButton.addActionListener(e -> abrirGestionAlumnos());

        JButton reportesButton = new JButton("Generar Reportes");
        reportesButton.addActionListener(e -> generarReportes());

        JButton generarReportesButton = new JButton("Generar Reportes");
        generarReportesButton.addActionListener(e -> new ReportesAdmin(connection).setVisible(true));
        mainPanel.add(generarReportesButton);

        JButton registrarUsuarioButton = new JButton("Registrar Profesores/Empleados");
        registrarUsuarioButton.addActionListener(e -> registrarUsuario());
        mainPanel.add(registrarUsuarioButton);

        JButton actualizarCursoButton = new JButton("Actualizar Cursos");
        actualizarCursoButton.addActionListener(e -> actualizarCurso());
        mainPanel.add(actualizarCursoButton);

        JButton agregarCursoButton = new JButton("Agregar Nuevo Curso");
        agregarCursoButton.addActionListener(e -> agregarCurso());
        mainPanel.add(agregarCursoButton);

        JButton cerrarSesionButton = new JButton("Cerrar Sesión");
        cerrarSesionButton.addActionListener(e -> cerrarSesion());




        // Añadir componentes al panel
        mainPanel.add(bienvenidaLabel);
        mainPanel.add(gestionEmpleadosButton);
        mainPanel.add(gestionCursosButton);
        mainPanel.add(gestionAlumnosButton);
        mainPanel.add(reportesButton);
        mainPanel.add(cerrarSesionButton);

        add(mainPanel);
    }

    private void abrirGestionEmpleados() {
        new GestionEmpleados(connection).setVisible(true);
    }

    private void abrirGestionCursos() {
        JFrame frame = new JFrame("Gestión de Cursos");
        frame.setSize(600, 400);
        frame.add(new GestionCursos(connection)); // Agrega el JPanel GestionCursos
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }


    private void abrirGestionAlumnos() {
        new GestionAlumnos(connection).setVisible(true);
    }

    private void generarReportes() {
        new Reportes(connection).setVisible(true);
    }
    private void registrarUsuario() {
        JTextField nombreField = new JTextField();
        JTextField emailField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<String> rolComboBox = new JComboBox<>(new String[]{"empleado", "maestro"});

        Object[] mensaje = {
                "Nombre:", nombreField,
                "Email:", emailField,
                "Contraseña:", passField,
                "Rol:", rolComboBox
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Usuario", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            String nombre = nombreField.getText().trim();
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String role = (String) rolComboBox.getSelectedItem();

            // Validaciones
            if (nombre.isEmpty() || email.isEmpty() || password.isEmpty() || role.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Todos los campos son obligatorios.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                JOptionPane.showMessageDialog(this, "Por favor, ingrese un email válido.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                String sql = "INSERT INTO users (name, email, pass, role) VALUES (?, ?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nombre);
                pstmt.setString(2, email);
                pstmt.setString(3, password);
                pstmt.setString(4, role);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Usuario registrado exitosamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al registrar el usuario: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void actualizarCurso() {
        JTextField idField = new JTextField();
        JTextField nombreField = new JTextField();
        JTextField descripcionField = new JTextField();
        JTextField costoField = new JTextField();

        Object[] mensaje = {
                "ID del Curso:", idField,
                "Nuevo Nombre:", nombreField,
                "Nueva Descripción:", descripcionField,
                "Nuevo Costo:", costoField
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Actualizar Curso", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String sql = "UPDATE courses SET name = ?, description = ?, cost = ? WHERE id = ?";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nombreField.getText());
                pstmt.setString(2, descripcionField.getText());
                pstmt.setDouble(3, Double.parseDouble(costoField.getText()));
                pstmt.setInt(4, Integer.parseInt(idField.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Curso actualizado exitosamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al actualizar el curso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    private void agregarCurso() {
        JTextField nombreField = new JTextField();
        JTextField descripcionField = new JTextField();
        JTextField costoField = new JTextField();

        Object[] mensaje = {
                "Nombre del Curso:", nombreField,
                "Descripción:", descripcionField,
                "Costo:", costoField
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Agregar Curso", JOptionPane.OK_CANCEL_OPTION);
        if (opcion == JOptionPane.OK_OPTION) {
            try {
                String sql = "INSERT INTO courses (name, description, cost) VALUES (?, ?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, nombreField.getText());
                pstmt.setString(2, descripcionField.getText());
                pstmt.setDouble(3, Double.parseDouble(costoField.getText()));
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Curso agregado exitosamente.");
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Error al agregar el curso: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
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
