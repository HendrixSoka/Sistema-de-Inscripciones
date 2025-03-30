/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import model.Database;
import model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import javafx.scene.control.Alert;

/**
 *
 * @author mauricioteranlimari
 */
public class UserDao {

    private Database UserConnection;

    public UserDao() throws ClassNotFoundException {
        this.UserConnection = new Database();
    }

    public boolean register(User usuario) {
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
                showAlert("Error", "La contrasena ya esta registrdo.", Alert.AlertType.ERROR);
                return false;
            }
            //Consulta para insertar nuevo usuario
            String SQL = "INSERT INTO usuario(nombre, apellido, cedula_identidad, celular, correo, cargo, usuario, contrasena)"
                    + " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

            Connection connection = this.UserConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

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

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al registrar usuario");
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
            resultSet.next();
            int count = resultSet.getInt(1);
            resultSet.close();
            sentence.close();

            return count > 0;
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
