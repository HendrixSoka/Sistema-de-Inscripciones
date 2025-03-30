/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Dao.UserDao;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.fxml.Initializable;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
//import javafx.scene.control.Button;
//import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import model.User;

public class LoginController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {

    }
    @FXML
    private Button BtnClose;

    @FXML
    private Button BtnLogin;

    @FXML
    private PasswordField TextPassword;

    @FXML
    private TextField TextUser;

    @FXML
    void BtnCloseOnAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que deseas salir?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    void BtnLoginOnAction(ActionEvent event) {
        if (TextUser.getText().isEmpty() || TextPassword.getText().isEmpty()) {
            showAlert("Error", "Los campos no pueden estar vacios", Alert.AlertType.WARNING);
            return;
        }
        try {
            UserDao userdao = new UserDao();
            boolean Sucesslogin = userdao.Login(TextUser.getText(), TextPassword.getText());

            if (Sucesslogin) {
                showAlert("Confirmacion", "Inicio de sesion exitoso", Alert.AlertType.CONFIRMATION);
                Parent root = FXMLLoader.load(getClass().getResource("/view/MainMenu.fxml"));
                Stage stage = (Stage) BtnLogin.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setTitle("Sistema de Registro de Estudiantes");
                stage.setScene(scene);

            } else {
                showAlert("Error", "Usuario o Contraseña Incorrectos", Alert.AlertType.ERROR);
            }
        } catch (IOException | ClassNotFoundException e) {
            showAlert("Error", "Problema en la base de datos", Alert.AlertType.ERROR);
            e.printStackTrace();
        }

    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
        
        PauseTransition pause = new PauseTransition(Duration.seconds(2));
        pause.setOnFinished(event -> alert.close()); 
        pause.play();
    }

}
