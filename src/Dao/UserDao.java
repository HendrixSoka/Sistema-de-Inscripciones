/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import controller.ManageUsersController;
import model.Database;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.Alert;

/**
 *
 * @author mauricioteranlimari
 */
public class UserDao {

    private Database UserConnection;
    private ManageUsersController verify;

    public UserDao() throws ClassNotFoundException {
        this.UserConnection = new Database();
        this.verify = new ManageUsersController();
    }

    public boolean register(User usuario) throws Exception {
        try {
            if (isValueExists("cedula_identidad", usuario.getCedula_identidad())) {
                showAlert("Error", "La cédula de identidad ya está registrada.", Alert.AlertType.ERROR);
                return false;
            }

            if (isValueExists("celular", usuario.getCelular())) {
                showAlert("Error", "El número de celular ya está registrado.", Alert.AlertType.ERROR);
                return false;
            }
            if (isValueExists("correo", usuario.getCorreo())) {
                showAlert("Error", "El correo electrónico ya está registrado.", Alert.AlertType.ERROR);
                return false;
            }
            if (isValueExists("usuario", usuario.getUsuario())) {
                showAlert("Error", "El nombre de usuario ya está registrado.", Alert.AlertType.ERROR);
                return false;
            }
            if (isValueExists("contrasena", usuario.getContrasena())) {
                showAlert("Error", "La contrasena ya está registrada.", Alert.AlertType.ERROR);
                return false;
            }
            //Consulta para insertar nuevo usuario
            String SQL = "INSERT INTO usuario(nombre, apellido, cedula_identidad, celular, correo, cargo, usuario, contrasena)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Connection connection = this.UserConnection.getConnection();

            try (PreparedStatement sentence = connection.prepareStatement(SQL)) {
                sentence.setString(1, usuario.getNombre());
                sentence.setString(2, usuario.getApellido());
                sentence.setString(3, usuario.getCedula_identidad());
                sentence.setString(4, usuario.getCelular());
                sentence.setString(5, usuario.getCorreo());
                sentence.setInt(6, usuario.getCargo());
                sentence.setString(7, usuario.getUsuario());
                sentence.setString(8, usuario.getContrasena());

                sentence.executeUpdate();
                sentence.close();
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Ocurrio un error al registrar usuario");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public List<User> tolist() {

        List<User> listUsers = new ArrayList<>();

        try {

            String SQL = "SELECT * FROM usuario";

            Connection connection = this.UserConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            ResultSet data = sentence.executeQuery();

            while (data.next() == true) {

                User user = new User();

                user.setId(data.getInt(1));
                user.setNombre(data.getString(2));
                user.setApellido(data.getString(3));
                user.setCedula_identidad(data.getString(4));
                user.setCelular(data.getString(5));
                user.setCorreo(data.getString(6));
                user.setCargo(data.getInt(7));
                user.setUsuario(data.getString(8));
                user.setContrasena(data.getString(9));

                listUsers.add(user);
            }
            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al listar usuarios");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

        }
        return listUsers;
    }

    public boolean Edit(User user) {

        try {

            String SQL = "UPDATE usuario SET nombre = ?, apellido = ?, celular = ?, correo = ?, cargo = ?, usuario = ?, contrasena = ?"
                    + " WHERE idusuario = ?";

            Connection connection = this.UserConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setString(1, user.getNombre());
            sentence.setString(2, user.getApellido());
            sentence.setString(3, user.getCelular());
            sentence.setString(4, user.getCorreo());
            sentence.setInt(5, user.getCargo());
            sentence.setString(6, user.getUsuario());
            sentence.setString(7, user.getContrasena());

            sentence.setInt(8, user.getId());

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

    public boolean Detele(int id) {
        try {

            String SQL = "DELETE FROM usuario WHERE idusuario = ?";

            Connection connection = this.UserConnection.getConnection();
            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, id);

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

    private boolean isValueExists(String field, String value) {
        try {

            String SQL = "SELECT COUNT(*) FROM usuario WHERE " + field + " = ?";
            Connection connection = this.UserConnection.getConnection();
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

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
