/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import interfaces.MainControllerAware;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

/**
 * FXML Controller class
 *
 * @author intel
 */
public class ManageGradesController implements Initializable,MainControllerAware {

    @FXML
    private Button btnEstudiante;
    
    @FXML
    private Button btnCurso;

    @FXML
    private Button btnEstadisticas;
    
    private Map<String, String> pageMap = new HashMap<>();
    
    private MainMenuController mainController;

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        pageMap.put("Notas Estudiantes", "StudentGrade");
        pageMap.put("Notas Materia", "SubjectGrades");
        pageMap.put("Estadisticas", "statistics");

        // Asociar acciones a los botones
        btnEstudiante.setOnAction(e -> navigateTo("Notas Estudiantes"));
        btnCurso.setOnAction(e -> navigateTo("Notas Materia"));
        btnEstadisticas.setOnAction(e -> navigateTo("Estadisticas"));
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
