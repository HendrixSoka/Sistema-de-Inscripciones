/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import interfaces.MainControllerAware;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.Initializable;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
public class MenuOptionsController implements Initializable, MainControllerAware {

        
    @FXML
    private Button btnGestionarEstudiantes;

    @FXML
    private Button btnConfiguracion;

    @FXML
    private Button btnReportes;

    @FXML
    private Button btnGestionarNotas;

    @FXML
    private Button btnAyuda;

    @FXML
    private Button btnGestionarUsuarios;

    private Map<String, String> pageMap = new HashMap<>();

    private MainMenuController mainController;

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pageMap.put("Gestionar Estudiantes", "ManageStudents");
        pageMap.put("Configuracion", "SchoolSettings");
        pageMap.put("Reportes", "Report");
        pageMap.put("Gestionar Notas", "ManageGrades");
        pageMap.put("Ayuda", "Help");
        pageMap.put("Gestionar Usuarios", "ManageUsers");

        btnGestionarEstudiantes.setOnAction(e -> navigateTo("Gestionar Estudiantes"));
        btnConfiguracion.setOnAction(e -> navigateTo("Configuracion"));
        btnReportes.setOnAction(e -> navigateTo("Reportes"));
        btnGestionarNotas.setOnAction(e -> navigateTo("Gestionar Notas"));
        btnAyuda.setOnAction(e -> navigateTo("Ayuda"));
        btnGestionarUsuarios.setOnAction(e -> navigateTo("Gestionar Usuarios"));
    }    
    


    public void navigateTo(String pageName) {
        System.out.println("Presionado " + pageName);
        if (mainController != null) {
            mainController.loadView(pageMap.get(pageName));
            mainController.addPage(pageName, pageMap.get(pageName));
        } else {
            System.out.println("Error: MainMenuController no está disponible.");
        }
    }
}
