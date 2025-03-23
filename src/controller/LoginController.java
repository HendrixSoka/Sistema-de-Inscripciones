/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController implements Initializable {

    
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
       
    }    
     @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;
    
    @FXML
    private Button LoginButton;

    //@FXML
    //private Label messageLabel;

    @FXML
    void handleLogin(ActionEvent event) {
        String username = usernameField.getText();
        String password = passwordField.getText();
        System.out.println(username + " "+ password);
        // Verificar si el usuario y la contraseña son "eu"
        if ("eu".equals(username) && "eu".equals(password)) {
            // cambiar vista
            try {
            // Cargar la nueva vista
            Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));

            // Obtener el stage actual desde el botón
            Stage stage = (Stage) LoginButton.getScene().getWindow();

            // Crear una nueva escena y establecerla en el stage
            Scene scene = new Scene(root);
            stage.setTitle("Sistema de Registro de Estudiantes");
            stage.setScene(scene);
        } catch (Exception e) {
            e.printStackTrace();
        }
            
        } else {
            showAlert("Error", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
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
