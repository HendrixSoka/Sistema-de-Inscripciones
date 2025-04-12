/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Dao.CourseDao;
import Dao.DocumentationDao;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import model.Course;
import model.Documentation;

/**
 * FXML Controller class
 *
 * @author intel
 */
public class SchoolSettingsController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private ComboBox<String> CboxGradeCourse;

    @FXML
    private ComboBox<String> CboxLevelCourse;

    @FXML
    private RadioButton RdNoO;

    @FXML
    private RadioButton RdYesO;

    @FXML
    private RadioButton RdNoCC;

    @FXML
    private RadioButton RdYesCC;
    
    @FXML
    private RadioButton RdNoAN;

    @FXML
    private RadioButton RdYesAN;

    @FXML
    private ComboBox<String> CboxSelect;

    @FXML
    private TableView<?> TableCourse;

    @FXML
    private TableView<?> TableDocumentation;

    @FXML
    private TextField TextNameDocumentation;

    @FXML
    private TextField TextPalallel;

    @FXML
    private TextField TextQuota;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnSave;

    private CourseDao coursedao;
    private DocumentationDao documentationdao;

    @FXML
    void btnSaveOnAction(ActionEvent event) {

        String selected = CboxSelect.getValue();

        if (selected == null) {
            showAlert("Error", "Debe Seleccionar una opcion", Alert.AlertType.ERROR);
            return;
        } else if (selected.equals("Gestionar Curso")) {

            Course course = new Course();

            if (CboxLevelCourse.getValue().equals("Primaria")) {
                course.setNivel(0);
            } else {
                course.setNivel(1);
            }

            if (CboxGradeCourse.getValue().equals("Primero")) {
                course.setGrado(0);
            } else if (CboxGradeCourse.getValue().equals("Segundo")) {
                course.setGrado(1);
            } else if (CboxGradeCourse.getValue().equals("Tercero")) {
                course.setGrado(2);
            } else if (CboxGradeCourse.getValue().equals("Cuarto")) {
                course.setGrado(3);
            } else if (CboxGradeCourse.getValue().equals("Quinto")) {
                course.setGrado(4);
            } else if (CboxGradeCourse.getValue().equals("Sexto")) {
                course.setGrado(5);
            }

            String parallel = TextPalallel.getText();

            if (parallel.isEmpty()) {
                showAlert("Error", "El campo no puede estar vacío", Alert.AlertType.ERROR);
                TextPalallel.clear();
                return;
            } else if (parallel.matches("^[A-Z]$")) {
                course.setParalelo(parallel.charAt(0));
            } else {
                showAlert("Error", "Paralelo invalido, solo letras", Alert.AlertType.ERROR);
                TextPalallel.clear();
                return;
            }
            boolean resp = this.coursedao.register(course);
            if (resp) {
                showAlert("Exito", "Se registro correctamente el curso", Alert.AlertType.INFORMATION);
                cleanFields();
                diseble();
            } else {
                showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
            }
        } else if (selected.equals("Gestionar Documentacion")) {
            Documentation documentation = new Documentation();

            documentation.setNombre(TextNameDocumentation.getText());
            if (RdYesO.isSelected()) {
                documentation.setObligatorio(true);
            } else if (RdNoO.isSelected()) {
                documentation.setObligatorio(false);
            } else {
                showAlert("Error", "No puede estar vacio", Alert.AlertType.ERROR);
            }
            if (RdYesCC.isSelected()) {
                documentation.setCartacompromiso(true);
            } else if (RdNoCC.isSelected()) {
                documentation.setCartacompromiso(false);
            } else {
                showAlert("Error", "No puede estar vacio", Alert.AlertType.ERROR);
            }
            boolean resp = this.documentationdao.register(documentation);
            if (resp) {
                showAlert("Exito", "Se registro correcto el documento", Alert.AlertType.INFORMATION);
                cleanFields();
                diseble();
            } else {
                showAlert("Error","Hubo un error", Alert.AlertType.ERROR);
            }
        }
        
    }
    
    private void cleanFields(){
        CboxSelect.getSelectionModel().select("Seleccione");
        CboxLevelCourse.getSelectionModel().select("Seleccione");
        CboxGradeCourse.getSelectionModel().select("Seleccione");
        TextPalallel.clear();
        TextQuota.clear();
        RdYesAN.setSelected(false);
        RdNoAN.setSelected(false);
        TextNameDocumentation.clear();
        RdYesO.setSelected(false);
        RdNoO.setSelected(false);
        RdYesCC.setSelected(false);
        RdNoCC.setSelected(false);
    }

    @FXML
    void setOnAction(ActionEvent event) {

        String seleccion = CboxSelect.getValue();

        if (seleccion.equals("Gestionar Curso")) {
            CboxLevelCourse.setDisable(false);
            CboxGradeCourse.setDisable(false);
            TextPalallel.setDisable(false);
            TableCourse.setDisable(false);
            btnSave.setDisable(false);
            TextNameDocumentation.setDisable(true);
            RdNoO.setDisable(true);
            RdYesO.setDisable(true);
            RdNoCC.setDisable(true);
            RdYesCC.setDisable(true);
            RdNoAN.setDisable(true);
            RdYesAN.setSelected(true);
            RdYesAN.setDisable(true);
            TableDocumentation.setDisable(true);
        } else if (seleccion.equals("Gestionar Documentacion")) {
            TextNameDocumentation.setDisable(false);
            RdYesO.setDisable(false);
            RdNoO.setDisable(false);
            btnSave.setDisable(false);
            RdNoCC.setDisable(false);
            RdYesCC.setDisable(false);
            RdNoAN.setDisable(true);
            RdYesAN.setDisable(true);
            RdYesAN.setSelected(false);
            CboxLevelCourse.setDisable(true);
            CboxGradeCourse.setDisable(true);
            TextPalallel.setDisable(true);
            TextQuota.setDisable(true);
            TableCourse.setDisable(true);
        } else {
            CboxLevelCourse.setDisable(true);
            CboxGradeCourse.setDisable(true);
            TextPalallel.setDisable(true);
            TextQuota.setDisable(true);
            TableCourse.setDisable(true);

            TextNameDocumentation.setDisable(true);
            RdNoO.setDisable(true);
            RdYesO.setDisable(true);
            TableDocumentation.setDisable(true);

            btnSave.setDisable(true);
            btnCancelar.setDisable(true);
            RdYesO.setDisable(true);
            RdNoO.setDisable(true);
            RdNoCC.setDisable(true);
            RdYesCC.setDisable(true);
            RdNoAN.setDisable(true);
            RdYesAN.setDisable(true);
        }
    }
    
    private void diseble(){
        CboxLevelCourse.setDisable(true);
        CboxGradeCourse.setDisable(true);
        TextPalallel.setDisable(true);
        TextQuota.setDisable(true);
        TableCourse.setDisable(true);

        TextNameDocumentation.setDisable(true);
        RdNoO.setDisable(true);
        RdYesO.setDisable(true);
        RdNoCC.setDisable(true);
        RdYesCC.setDisable(true);
        RdNoAN.setDisable(true);
        RdYesAN.setDisable(true);
        TableDocumentation.setDisable(true);

        btnSave.setDisable(true);
        btnCancelar.setDisable(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        diseble();

        String[] options = {"Gestionar Curso", "Gestionar Documentacion"};

        ObservableList<String> items = FXCollections.observableArrayList(options);

        CboxSelect.setItems(items);
        CboxSelect.setValue("Seleccione");

        String[] optionsLevel = {"Primaria", "Secundaria"};

        ObservableList<String> itemsLevel = FXCollections.observableArrayList(optionsLevel);

        CboxLevelCourse.setItems(itemsLevel);
        CboxLevelCourse.setValue("Seleccione");

        String[] optionsGrade = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

        ObservableList<String> itemsGrade = FXCollections.observableArrayList(optionsGrade);

        CboxGradeCourse.setItems(itemsGrade);
        CboxGradeCourse.setValue("Seleccione");

        ToggleGroup group0 = new ToggleGroup();

        RdYesO.setToggleGroup(group0);
        RdNoO.setToggleGroup(group0);
        
        ToggleGroup group1 = new ToggleGroup();
        RdYesCC.setToggleGroup(group1);
        RdNoCC.setToggleGroup(group1);

        ToggleGroup group2 = new ToggleGroup();
        RdYesAN.setToggleGroup(group2);
        RdNoAN.setToggleGroup(group2);

        try {
            this.coursedao = new CourseDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            this.documentationdao = new DocumentationDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
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
