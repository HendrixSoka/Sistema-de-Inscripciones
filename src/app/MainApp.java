/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package app;

import controller.ManageUsersController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javax.swing.JOptionPane;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar la vista desde el archivo FXML
            Parent root = FXMLLoader.load(getClass().getResource("/view/ManageUsers.fxml"));

            // Crear la escena con la vista cargada
            Scene scene = new Scene(root);

            // Configurar la ventana principal
            primaryStage.setTitle("Sistema de Registro de Estudiantes");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
