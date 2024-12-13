import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.table.DefaultTableModel;
import com.formdev.flatlaf.FlatLightLaf;

public class ConexionBD extends JFrame {
    private JTextField userField;
    private JTextField emailField;
    private JPasswordField passField;
    private JButton loginButton;
    private JButton registerButton;
    private Connection connection;

    public ConexionBD() {
        // Configuración de la ventana
        setTitle("Sistema de Login");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal con fondo degradado
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(58, 123, 213),
                        0, getHeight(), new Color(58, 96, 213)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // Logotipo o título
        JLabel titleLabel = new JLabel("Bienvenido", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(50, 50, 300, 50);
        mainPanel.add(titleLabel);

        // Campo de usuario
        JLabel userLabel = new JLabel("Usuario");
        userLabel.setForeground(Color.WHITE);
        userLabel.setBounds(50, 150, 300, 30);
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(userLabel);

        userField = new JTextField();
        userField.setBounds(50, 180, 300, 40);
        userField.setFont(new Font("Arial", Font.PLAIN, 14));
        userField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        mainPanel.add(userField);

        // Campo de contraseña
        JLabel passLabel = new JLabel("Contraseña");
        passLabel.setForeground(Color.WHITE);
        passLabel.setBounds(50, 230, 300, 30);
        passLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        mainPanel.add(passLabel);

        passField = new JPasswordField();
        passField.setBounds(50, 260, 300, 40);
        passField.setFont(new Font("Arial", Font.PLAIN, 14));
        passField.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        mainPanel.add(passField);

        // Botón de login
        loginButton = new JButton("Iniciar Sesión");
        loginButton.setBounds(50, 330, 300, 45);
        loginButton.setBackground(new Color(255, 255, 255));
        loginButton.setForeground(new Color(58, 123, 213));
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.addActionListener(e -> login());
        mainPanel.add(loginButton);

        // Botón de registro
        registerButton = new JButton("Registrarse");
        registerButton.setBounds(50, 390, 300, 45);
        registerButton.setBackground(new Color(58, 123, 213));
        registerButton.setForeground(Color.WHITE);
        registerButton.setFont(new Font("Arial", Font.BOLD, 16));
        registerButton.setFocusPainted(false);
        registerButton.addActionListener(e -> abrirRegistro());
        mainPanel.add(registerButton);

        JButton mostrarUsuariosButton = new JButton("Ver Usuarios");
        mostrarUsuariosButton.setBounds(50, 450, 300, 45);
        mostrarUsuariosButton.setBackground(new Color(58, 123, 213));
        mostrarUsuariosButton.setForeground(Color.WHITE);
        mostrarUsuariosButton.setFont(new Font("Arial", Font.BOLD, 16));
        mostrarUsuariosButton.setFocusPainted(false);
        mostrarUsuariosButton.addActionListener(e -> {
            DataFetcher dataFetcher = new DataFetcher(connection);
            new MostrarUsuarios(dataFetcher).setVisible(true);
        });
        mainPanel.add(mostrarUsuariosButton);


        // Conectar a la base de datos
        connection = ConectarBD("");

        // Añadir el panel principal
        add(mainPanel);
    }
    private void loginAsAdmin() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Por favor, complete todos los campos", false);
            return;
        }

        String role = verificarCredenciales(username, password);

        if ("admin".equals(role)) {
            mostrarMensaje("Inicio de sesión exitoso", true);
            DataFetcher dataFetcher = new DataFetcher(connection); // Crear el fetcher con la conexión
            SwingUtilities.invokeLater(() -> new AdminModule(connection, username).setVisible(true));
            this.dispose();
        } else {
            mostrarMensaje("Acceso denegado", false);
        }
    }



    private void login() {
        String username = userField.getText();
        String password = new String(passField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            mostrarMensaje("Por favor, complete todos los campos", false);
            return;
        }

        String role = verificarCredenciales(username, password);

        if ("admin".equals(role)) {
            mostrarMensaje("Inicio de sesión exitoso", true);
            SwingUtilities.invokeLater(() -> new AdminModule(connection, username).setVisible(true));
            this.dispose();
        } else if ("empleado".equals(role) || "maestro".equals(role)) {
            mostrarMensaje("Inicio de sesión exitoso", true);
            SwingUtilities.invokeLater(() -> new EmpleadoModule(connection, username).setVisible(true));
            this.dispose();

        }else if ("Alumno".equals(role)) {
            mostrarMensaje("Inicio de sesión exitoso", true);
            SwingUtilities.invokeLater(() -> new AlumnoModule(connection, username).setVisible(true));
            this.dispose();

        } else {
            mostrarMensaje("Acceso denegado. Rol no autorizado.", false);
        }
    }
    private void iniciarSesion() {
        // Validar credenciales (esto es solo un ejemplo)
        if (validarCredenciales(userField.getText(), new String(passField.getPassword()))) {
            JOptionPane.showMessageDialog(this, "Inicio de sesión exitoso.");

            // Abrir la ventana del administrador
            ModuloAdministrador adminModule = new ModuloAdministrador(connection);

            adminModule.setVisible(true);

            // Cerrar la ventana de login
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Usuario o contraseña incorrectos.");
        }
    }

    private boolean validarCredenciales(String usuario, String password) {
        // Aquí va la lógica para validar usuario y contraseña desde la base de datos
        return usuario.equals("admin") && password.equals("admin123");
    }




    private void registrarInscripcion() {
        // Formulario para capturar user_id y group_id
        JTextField userIdField = new JTextField();
        JTextField groupIdField = new JTextField();

        Object[] mensaje = {
                "ID del Alumno:", userIdField,
                "ID del Grupo:", groupIdField
        };

        int opcion = JOptionPane.showConfirmDialog(this, mensaje, "Registrar Inscripción", JOptionPane.OK_CANCEL_OPTION);

        if (opcion == JOptionPane.OK_OPTION) {
            int student_id = Integer.parseInt(userIdField.getText());
            int groupId = Integer.parseInt(groupIdField.getText());

            try {
                String sql = "INSERT INTO inscriptions (student_id, group_id) VALUES (?, ?)";
                PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setInt(1, student_id);
                pstmt.setInt(2, groupId);
                pstmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Inscripción registrada correctamente.");
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Error al registrar inscripción.");
            }
        }
    }


    private void generarReporteInscripciones() {
        try {
            String sql = "SELECT u.name AS Alumno, c.name AS Curso, g.schedule AS Horario, g.room AS Salon " +
                    "FROM inscriptions e " +
                    "JOIN users u ON u.user_id = u.id " +
                    "JOIN groups g ON e.group_id = g.id " +
                    "JOIN courses c ON g.course_id = c.id";

            PreparedStatement pstmt = connection.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            // Definir las columnas para el reporte
            String[] columnNames = {"Alumno", "Curso", "Horario", "Salón"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0);

            // Procesar los resultados
            while (rs.next()) {
                String alumno = rs.getString("Alumno");
                String curso = rs.getString("Curso");
                String horario = rs.getString("Horario");
                String salon = rs.getString("Salon");

                // Agregar fila con validación para valores nulos
                model.addRow(new Object[]{
                        alumno != null ? alumno : "N/A",   // Validar si el valor es nulo
                        curso != null ? curso : "N/A",     // Validar si el valor es nulo
                        horario != null ? horario : "N/A", // Validar si el valor es nulo
                        salon != null ? salon : "N/A"      // Validar si el valor es nulo
                });

                // Mensajes de depuración para verificar los resultados
                System.out.println("Alumno: " + alumno);
                System.out.println("Curso: " + curso);
                System.out.println("Horario: " + horario);
                System.out.println("Salon: " + salon);
            }

            // Crear la tabla con los datos obtenidos
            JTable table = new JTable(model);
            JScrollPane scrollPane = new JScrollPane(table);

            // Crear un JFrame para mostrar el reporte
            JFrame frame = new JFrame("Reporte de Inscripciones");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);  // Permite cerrar la ventana
            frame.add(scrollPane);
            frame.setSize(600, 400);
            frame.setLocationRelativeTo(null);  // Centra la ventana
            frame.setVisible(true);

        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al generar el reporte: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }




    private void mostrarMensaje(String mensaje, boolean esExitoso) {
        JOptionPane optionPane = new JOptionPane(mensaje,
                esExitoso ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);
        JDialog dialog = optionPane.createDialog("Mensaje");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);
    }

    private void abrirVentanaPrincipal() {
        // Ejemplo de ventana principal simple
        JFrame ventanaPrincipal = new JFrame("Sistema Principal");
        ventanaPrincipal.setSize(600, 400);
        ventanaPrincipal.setLocationRelativeTo(null);

        JLabel bienvenida = new JLabel("Bienvenido al Sistema", SwingConstants.CENTER);
        bienvenida.setFont(new Font("Arial", Font.BOLD, 24));
        ventanaPrincipal.add(bienvenida);

        ventanaPrincipal.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventanaPrincipal.setVisible(true);

        // Cerrar ventana de login
        this.dispose();
    }

    private String verificarCredenciales(String username, String password) {
        try {
            String sql = "SELECT role FROM users WHERE name = ? AND pass = ?";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String rol = rs.getString("role");
                System.out.println("Rol obtenido: " + rol); // Mensaje de depuración
                return rol; // Retorna el rol del usuario
            }
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensaje("Error de conexión", false);
        }
        return null; // Retorna null si no se encuentra al usuario
    }



    private void abrirRegistro() {
        // Crear los campos para los datos de registro
        JTextField nuevoUsuario = new JTextField();
        JTextField emailField = new JTextField();  // Crear el campo de correo electrónico
        JPasswordField nuevaContrasena = new JPasswordField();
        JPasswordField confirmarContrasena = new JPasswordField();
        JComboBox<String> rolComboBox = new JComboBox<>(new String[]{"admin", "empleado","Alumno"}); // Solo permite admin y empleado

        // Definir los campos y el texto que se mostrará en el cuadro de diálogo
        Object[] mensaje = {
                "Nombre de Usuario:", nuevoUsuario,
                "Correo Electrónico:", emailField,  // Añadir el campo de correo electrónico
                "Contraseña:", nuevaContrasena,
                "Confirmar Contraseña:", confirmarContrasena,
                "Rol:", rolComboBox
        };

        // Mostrar el cuadro de diálogo para el registro
        int opcion = JOptionPane.showConfirmDialog(
                this, mensaje, "Registro de Nuevo Usuario", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        // Verificar si se presionó el botón OK
        if (opcion == JOptionPane.OK_OPTION) {
            String username = nuevoUsuario.getText().trim();
            String email = emailField.getText();  // Obtener el correo electrónico del nuevo campo
            String password = new String(nuevaContrasena.getPassword());
            String confirmPassword = new String(confirmarContrasena.getPassword());
            String role = (String) rolComboBox.getSelectedItem();

            // Verificar si los campos obligatorios están vacíos
            if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
                mostrarMensaje("Todos los campos son obligatorios", false);
                return;
            }

            // Verificar si las contraseñas coinciden
            if (!password.equals(confirmPassword)) {
                mostrarMensaje("Las contraseñas no coinciden", false);
                return;
            }

            // Verificar si el rol es válido (solo 'admin' o 'empleado')
            if (role == null || (!role.equals("admin") && !role.equals("empleado")) &&!role.equals("Alumno")) {
                mostrarMensaje("Rol inválido seleccionado", false);
                return;
            }

            // Intentar insertar el usuario en la base de datos
            try {
                insertUser(connection, username, email, password, role);
                mostrarMensaje("Usuario registrado exitosamente", true);
            } catch (Exception e) {
                mostrarMensaje("Error al registrar el usuario: " + e.getMessage(), false);
            }
        }
    }



    public static void updateUser(Connection con, int id, String name, String pass){
        String sql = "Update users set name = ?, pass = ? Where id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, pass);
            pstmt.setInt(3, id);

            int result = pstmt.executeUpdate();

            if(result == 1){
                System.out.println("Se modificaron los datos del usuario : "+name);
            }else{
                System.out.println("El usuario con id: "+id+" no existe");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void updateUser(Connection con, int id, String value, int opc){
        String sql = "";
        if(opc == 1){
            sql = "Update users set name = ? Where id = ?";
        }else{
            sql = "Update users set pass = ? Where id = ?";
        }

        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, value);
            pstmt.setInt(2, id);

            int result = pstmt.executeUpdate();

            if(result == 1){
                System.out.println("Se modificaron los datos del usuario : "+value);
            }else{
                System.out.println("El usuario con id: "+id+" no existe");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void deleteUser(Connection con, int id){
        String sql = "Delete From users Where id = ?";
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            int result = pstmt.executeUpdate();

            if(result == 1){
                System.out.println("Se borro al usuario con id "+id);
            }else{
                System.out.println("El usuario con id "+id+" no existe");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static void insertUser(Connection con, String name, String email, String pass, String role) {
        String sql = "INSERT INTO users(name, email, pass, role) VALUES(?, ?, ?, ?)";

        try (PreparedStatement pstmt = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            // Configuramos los valores de los parámetros en el orden correcto
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, pass);
            pstmt.setString(4, role); // Asegúrate de insertar el rol
            pstmt.executeUpdate();

            // Obtener el ID del usuario recién insertado
            ResultSet rs = pstmt.getGeneratedKeys();
            int userId = -1;
            if (rs.next()) {
                userId = rs.getInt(1); // ID del usuario insertado
            }

            // Insertar en la tabla correspondiente según el rol
            if (userId != -1) {
                if (role.equalsIgnoreCase("alumno")) {
                    // Insertar en la tabla 'students' (alumnos)
                    String studentSql = "INSERT INTO students (user_id) VALUES (?)";
                    try (PreparedStatement studentStmt = con.prepareStatement(studentSql)) {
                        studentStmt.setInt(1, userId);
                        studentStmt.executeUpdate();
                        System.out.println("Alumno insertado con ID de usuario: " + userId);
                    }
                } else if (role.equalsIgnoreCase("empleado")) {
                    // Insertar en la tabla 'employees' (empleados)
                    String employeeSql = "INSERT INTO employees (user_id, position, salary, email) VALUES (?, ?, ?, ?)";
                    // Asignamos un puesto y salario por defecto para empleados generales
                    String position = "Empleado General"; // O el puesto por defecto
                    double salary = 20000.00; // O el salario por defecto

                    try (PreparedStatement employeeStmt = con.prepareStatement(employeeSql)) {
                        employeeStmt.setInt(1, userId);
                        employeeStmt.setString(2, position);
                        employeeStmt.setDouble(3, salary);
                        employeeStmt.setString(4, email); // Email del empleado
                        employeeStmt.executeUpdate();
                        System.out.println("Empleado insertado con ID de usuario: " + userId);
                    }
                } else if (role.equalsIgnoreCase("maestro")) {
                    // Insertar en la tabla 'employees' para maestros
                    String employeeSql = "INSERT INTO employees (user_id, position, salary, email) VALUES (?, ?, ?, ?)";
                    String position = "maestro"; // El puesto específico para los maestros
                    double salary = 25000.00; // Salario por defecto para los profesores

                    try (PreparedStatement employeeStmt = con.prepareStatement(employeeSql)) {
                        employeeStmt.setInt(1, userId);
                        employeeStmt.setString(2, position);
                        employeeStmt.setDouble(3, salary);
                        employeeStmt.setString(4, email); // Email del profesor
                        employeeStmt.executeUpdate();
                        System.out.println("Maestro insertado con ID de usuario: " + userId);
                    }
                }
            }

        } catch (SQLException e) {
            System.out.println("Error al insertar usuario: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void Consulta(Connection con) {
        String sql = "SELECT * FROM users";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()){
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String pass = rs.getString("pass");

                System.out.println("ID: "+id+" , Name: "+name+" , Pass: "+pass);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static Connection ConectarBD(String bd){
        Connection connection = null;
        String host = "jdbc:mysql://localhost:3306/BaseDeDatos";
        String user = "root";
        String pass = "";

        System.out.println("Conectando…");

        try {
            connection = DriverManager.getConnection(host+bd, user, pass);
            System.out.println("Conexion exitosa");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }

        return connection;
    }

    public static void Desconeccion(Connection cb){
        try {
            cb.close();
            System.out.println("Desconectando... !!!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) {
        // Configurar el tema moderno de FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Mostrar la ventana de login (ConexionBD) al iniciar
        SwingUtilities.invokeLater(() -> {
            ConexionBD loginSystem = new ConexionBD();
            loginSystem.setVisible(true); // Mostrar solo la ventana de login al inicio
        });
    }

}


