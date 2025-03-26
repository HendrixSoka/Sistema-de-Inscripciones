/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import com.sun.jfx.incubator.scene.control.richtext.FxPathBuilder;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import model.Database;
import model.User;

/**
 * FXML Controller class
 *
 * @author intel
 */
public class ManageUsersController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private TableView<User> tblUser;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colNombre;
    @FXML
    private TableColumn<User, String> colApellidos;
    @FXML
    private TableColumn<User, String> colCI;
    @FXML
    private TableColumn<User, String> colCelular;
    @FXML
    private TableColumn<User, Integer> colCargo;
    @FXML
    private TableColumn<User, String> colUsuario;
    @FXML
    private TableColumn<User, String> colCorreo;
    @FXML
    private TableColumn<User, String> colContrasena;

    private ObservableList<User> users;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        users = FXCollections.observableArrayList();
        this.colId.setCellValueFactory(new PropertyValueFactory("id"));
        this.colNombre.setCellValueFactory(new PropertyValueFactory("nombre"));
        this.colApellidos.setCellValueFactory(new PropertyValueFactory("apellido"));
        this.colCI.setCellValueFactory(new PropertyValueFactory("cedula_identidad"));
        this.colCelular.setCellValueFactory(new PropertyValueFactory("celular"));
        this.colCorreo.setCellValueFactory(new PropertyValueFactory("correo"));
        this.colCargo.setCellValueFactory(new PropertyValueFactory("cargo"));
        this.colUsuario.setCellValueFactory(new PropertyValueFactory("usuario"));
        this.colContrasena.setCellValueFactory(new PropertyValueFactory("contrasena"));

        LoadUsers();
        tblUser.setItems(users);
    }

    private void LoadUsers() {
        users.clear();
        String query = "SELECT idusuario, nombre, apellido, cedula_identidad, celular, correo, cargo, usuario, contrasena FROM usuario";
        try (Connection connection = Database.getConnection(); PreparedStatement pst = connection.prepareStatement(query); ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                users.add(new User(
                        rs.getInt("idusuario"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("cedula_identidad"),
                        rs.getString("celular"),
                        rs.getString("correo"),
                        rs.getInt("cargo"),
                        rs.getString("usuario"),
                        rs.getString("contrasena")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
