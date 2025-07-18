/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import controller.ManageStudentsController;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;
import model.Database;
import model.Student;

public class StudentDao {
    private Database StudentConnection;
    private ManageStudentsController verify;
    
    private Statement statement;

    public StudentDao() throws ClassNotFoundException, SQLException {
        this.StudentConnection = new Database();
        this.verify = new ManageStudentsController();
    }
    public int register(Student estudiante) throws Exception {
        try {
            if (isValueExists("cedula_identidad", estudiante.getCedula_identidad())) {
                showAlert("Error", "La cédula de identidad ya está registrada.", Alert.AlertType.ERROR);
                return -1;
            }
            if (isValueExists("correo", estudiante.getCorreo())) {
                showAlert("Error", "El correo electrónico ya está registrado.", Alert.AlertType.ERROR);
                return -1;
            }
         
            //Consulta para insertar nuevo usuario
            String SQL = "INSERT INTO estudiante (nombre, apellido,fecha_nacimiento,cedula_identidad, genero,direccion, correo)"
                    + " VALUES ( ?, ?, ?, ?, ?, ?, ?)";

            Connection connection = this.StudentConnection.getConnection();

            try (PreparedStatement sentence = connection.prepareStatement(SQL,statement.RETURN_GENERATED_KEYS)) {
                sentence.setString(1, estudiante.getNombre());
                sentence.setString(2, estudiante.getApellido());
                sentence.setDate(3,estudiante.getFecha_nacimiento());
                sentence.setString(4, estudiante.getCedula_identidad());
                sentence.setInt(5, estudiante.getGenero());
                sentence.setString(6, estudiante.getDireccion());
                sentence.setString(7, estudiante.getCorreo());

                int affectedRows = sentence.executeUpdate();

                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = sentence.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            return generatedKeys.getInt(1); // Retorna el ID generado
                        }
                    }
                }

                return -1;
            }
        } catch (SQLException e) {
            System.err.println("Ocurrio un error al registrar Estudiante");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return -1;
        }
    }
    public Student SearchbyId(int idBuscado) {
        Student student = null;

        try {
            String SQL = "SELECT * FROM estudiante WHERE idestudiante = ?";
            Connection connection = this.StudentConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);
            sentence.setInt(1, idBuscado);

            ResultSet data = sentence.executeQuery();

            if (data.next()) {
                student = new Student();
                student.setId(data.getInt(1));
                student.setNombre(data.getString(2));
                student.setApellido(data.getString(3));
                student.setFecha_nacimiento(data.getDate(4));
                student.setCedula_identidad(data.getString(5));
                student.setGenero(data.getInt(6));
                student.setDireccion(data.getString(7));
                student.setCorreo(data.getString(8));
            }

            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Ocurrió un error al buscar el estudiante por ID");
            System.err.println("Mensaje del error: " + e.getMessage());
            e.printStackTrace();
        }

        return student;
    }


    public List<Student> tolist() {

        List<Student> listStudents = new ArrayList<>();

        try {

            String SQL = "SELECT * FROM estudiante";

            Connection connection = this.StudentConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next() == true) {

                Student student = new Student();

                student.setId(data.getInt(1));
                student.setNombre(data.getString(2));
                student.setApellido(data.getString(3));
                student.setFecha_nacimiento(data.getDate(4));
                student.setCedula_identidad(data.getString(5));
                student.setGenero(data.getInt(6));
                student.setDireccion(data.getString(7));
                student.setCorreo(data.getString(8));
                listStudents.add(student);
            }
            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al listar estudiantes");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return listStudents;
    }

    public boolean Edit(Student estudiante) {

        try {

            String SQL = "UPDATE estudiante SET  nombre = ?, apellido = ?, fecha_nacimiento = ?, cedula_identidad = ?, genero = ?, direccion = ?, correo = ?"
                    + " WHERE idestudiante = ?";

            Connection connection = this.StudentConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

                sentence.setString(1, estudiante.getNombre());
                sentence.setString(2, estudiante.getApellido());
                sentence.setDate(3,estudiante.getFecha_nacimiento());
                sentence.setString(4, estudiante.getCedula_identidad());
                sentence.setInt(5, estudiante.getGenero());
                sentence.setString(6, estudiante.getDireccion());
                sentence.setString(7, estudiante.getCorreo());

            sentence.setInt(8, estudiante.getId());

            sentence.executeUpdate();

            sentence.close();

            return true;

        } catch (Exception e) {
            System.err.println("Ocurrio un error al editar usuario");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public boolean Delete(int id) {
        try {

            String SQL = "DELETE FROM estudiante WHERE idestudiante = ?";

            Connection connection = this.StudentConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, id);

            sentence.executeUpdate();

            sentence.close();

            return true;

        } catch (Exception e) {
            System.err.println("Ocurrio un error al editar estudiante");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }
    

    
    private boolean isValueExists(String field, String value) {
        try {

            String SQL = "SELECT COUNT(*) FROM estudiante WHERE " + field + " = ?";
            Connection connection = this.StudentConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);
            sentence.setString(1, value);

            ResultSet resultSet = sentence.executeQuery();
            return resultSet.next() && resultSet.getInt(1) > 0;

        } catch (SQLException e) {
            System.err.println("Error al verificar la existencia del valor");
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Student> getEstudiantesPorGradoParaleloYNivel(int grado, String paralelo, int nivel) {
    List<Student> lista = new ArrayList<>();
    String sql = "SELECT e.* FROM estudiante e " +
                 "JOIN inscripcion i ON e.idestudiante = i.id_estudiante " +
                 "JOIN curso c ON i.id_curso = c.idcurso " +
                 "WHERE c.grado = ? AND c.paralelo = ? AND c.nivel = ?";

        try (Connection conn = this.StudentConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, grado);
            stmt.setString(2, paralelo);
            stmt.setInt(3, nivel);

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Student e = new Student();
                e.setId(rs.getInt("idestudiante"));
                e.setNombre(rs.getString("nombre"));
                e.setApellido(rs.getString("apellido"));
                e.setCedula_identidad(rs.getString("cedula_identidad"));
                e.setFecha_nacimiento(rs.getDate("fecha_nacimiento"));
                e.setGenero(rs.getInt("genero"));
                e.setDireccion(rs.getString("direccion"));
                e.setCorreo(rs.getString("correo"));
                lista.add(e);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }


    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
