/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import interfaces.MainControllerAware;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

public class ManageStudentsController implements Initializable, MainControllerAware {

    @FXML
    private Button btnEstudianteNuevo;
    
    private MainMenuController mainController;

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnEstudianteNuevo.setOnAction(e -> navigateTo("Estudiante Nuevo","ExistingStudent"));
    }    
    
    public void navigateTo(String pageName, String fxmlName){
        System.out.println("Presionado " + pageName);
        if (mainController != null) {
            mainController.loadView(fxmlName);
            mainController.addPage(pageName,fxmlName);
        } else {
            System.out.println("Error: MainMenuController no está disponible.");
        }
    }
}
