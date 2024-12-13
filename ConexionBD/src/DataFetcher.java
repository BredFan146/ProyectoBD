import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DataFetcher {
    private Connection connection;

    public DataFetcher(Connection connection) {
        this.connection = connection;
    }

    public List<String[]> obtenerUsuarios() {
        List<String[]> usuarios = new ArrayList<>();
        String sql = "SELECT id, name, email, role FROM users";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String[] usuario = {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("role")
                };
                usuarios.add(usuario);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }


    public List<String[]> obtenerCursos() {
        List<String[]> cursos = new ArrayList<>();
        String sql = "SELECT id, name, description, cost FROM courses";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String[] curso = {
                        String.valueOf(rs.getInt("id")),
                        rs.getString("name"),
                        rs.getString("description"),
                        String.valueOf(rs.getDouble("cost"))
                };
                cursos.add(curso);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cursos;
    }
}

