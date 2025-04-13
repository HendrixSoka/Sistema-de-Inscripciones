/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Dao.CourseDao;
import Dao.DocumentationDao;
import Dao.Subject_courseDao;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Course;
import model.Documentation;
import model.Subject_course;

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
    private TableView<Course> TableCourse;

    @FXML
    private TableView<Documentation> TableDocumentation;

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
    private Subject_courseDao scoursedao;
    private char parallel;

    public char getParallel() {
        return parallel;
    }
    
    @FXML
    void BtnCancelarOnAction(ActionEvent event) {
        
        if(courseselect != null){
            courseselect = null;
            cleanFields();
            btnCancelar.setDisable(true);
        }else if(documentationselect != null){
            documentationselect = null;
            cleanFields();
            btnCancelar.setDisable(true);
        }
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) throws ClassNotFoundException {

        String selected = CboxSelect.getValue();

        if (selected == null) {
            showAlert("Error", "Debe Seleccionar una opcion", Alert.AlertType.ERROR);
            return;
        } else if (selected.equals("Gestionar Curso")) {

            if (courseselect == null) {

                Course course = new Course();

                if (CboxLevelCourse.getValue().equals("Primaria")) {
                    course.setNivel(0);
                } else {
                    course.setNivel(1);
                }

                switch (CboxGradeCourse.getValue()) {
                    case "Primero":
                        course.setGrado(0);
                        break;
                    case "Segundo":
                        course.setGrado(1);
                        break;
                    case "Tercero":
                        course.setGrado(2);
                        break;
                    case "Cuarto":
                        course.setGrado(3);
                        break;
                    case "Quinto":
                        course.setGrado(4);
                        break;
                    case "Sexto":
                        course.setGrado(5);
                        break;
                    default:
                        break;
                }

                if (CboxLevelCourse.getSelectionModel().getSelectedIndex() == -1
                        || CboxGradeCourse.getSelectionModel().getSelectedIndex() == -1) {
                    showAlert("Error", "Los campos no pueden estar vacios", Alert.AlertType.ERROR);
                    return;
                } else if (CboxLevelCourse.getSelectionModel().getSelectedIndex() != -1 && CboxGradeCourse.getSelectionModel().getSelectedIndex() != -1) {
                    parallel = coursedao.reeturnParallel(CboxLevelCourse.getSelectionModel().getSelectedIndex(), CboxGradeCourse.getSelectionModel().getSelectedIndex());
                    switch (parallel) {
                        case '-':
                            parallel = 'A';
                            course.setParalelo(parallel);
                            break;
                        case 'Z':
                            showAlert("Error", "Maximo de cursos permitido", Alert.AlertType.ERROR);
                            return;
                        default:
                            parallel++;
                            course.setParalelo(parallel);
                            break;
                    }
                }

                boolean resp = this.coursedao.register(course);
                if (resp) {
                    showAlert("Exito", "Se registro correctamente el curso", Alert.AlertType.INFORMATION);
                    int tr = CboxGradeCourse.getSelectionModel().getSelectedIndex();
                    if (tr == 0 || tr == 1 || tr == 2) {
                        addMaterialsForGrade(13);
                    } else if (tr == 3 || tr == 4 || tr == 5) {
                        addMaterialsForGrade(12);
                    }
                    cleanFields();
                    diseble();
                    UploadCourses();
                } else {
                    showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                }
            } else {
                courseselect.setNivel(CboxLevelCourse.getSelectionModel().getSelectedIndex());
                courseselect.setGrado(CboxGradeCourse.getSelectionModel().getSelectedIndex());
                courseselect.setParalelo(TextPalallel.getText().charAt(0));

                boolean rsp = this.coursedao.editCourse(courseselect);
                if (rsp) {
                    showAlert("Exito", "Se guardo correctamente el curso", Alert.AlertType.INFORMATION);
                    cleanFields();
                    UploadCourses();

                    courseselect = null;

                    btnCancelar.setDisable(true);

                } else {
                    showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                }
            }
        } else if (selected.equals("Gestionar Documentacion")) {
            if (documentationselect == null) {
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
                    LoadDocumentation();
                } else {
                    showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                }
            } else {
                documentationselect.setNombre(TextNameDocumentation.getText());
                if (RdYesO.isSelected()) {
                    documentationselect.setObligatorio(true);
                } else if (RdNoO.isSelected()) {
                    documentationselect.setObligatorio(false);
                } else {
                    showAlert("Error", "No puede estar vacio", Alert.AlertType.ERROR);
                }
                if (RdYesCC.isSelected()) {
                    documentationselect.setCartacompromiso(true);
                } else if (RdNoCC.isSelected()) {
                    documentationselect.setCartacompromiso(false);
                } else {
                    showAlert("Error", "No puede estar vacio", Alert.AlertType.ERROR);
                }

                boolean resp = this.documentationdao.editDocumentation(documentationselect);
                if (resp) {
                    showAlert("Exito", "Se guardo correctamente el documento", Alert.AlertType.INFORMATION);
                    cleanFields();

                    documentationselect = null;

                    btnCancelar.setDisable(true);

                    LoadDocumentation();
                } else {
                    showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                }

            }
        }

    }

    private void addMaterialsForGrade(int numberOfSubjects) {
        int idcurso = coursedao.returnIdcurso();
        List<Subject_course> list = new ArrayList<>();

        for (int i = 1; i <= numberOfSubjects; i++) {
            Subject_course addmaterial = new Subject_course();
            addmaterial.setId_materia(i);
            addmaterial.setId_curso(idcurso);
            list.add(addmaterial);
        }

        boolean res = this.scoursedao.addMaterial(list);
        if (res) {
            System.out.println("Se registraron las materias correctamente");
        } else {
            showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
        }
    }

    private void cleanFields() {
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
            enableCourseField();
            disableDocumentationField();
        } else if (seleccion.equals("Gestionar Documentacion")) {
            enableDocumentationField();
            disableCourseField();
        } else {
            diseble();
        }
    }

    public void LoadDocumentation() {

        TableDocumentation.getItems().clear();
        TableDocumentation.getColumns().clear();

        List<Documentation> documentations = this.documentationdao.toList();

        ObservableList<Documentation> data = FXCollections.observableArrayList(documentations);

        TableColumn iddocumenttypeCol = new TableColumn("ID");

        iddocumenttypeCol.setCellValueFactory(new PropertyValueFactory("idtipo_documento"));

        TableColumn nameCol = new TableColumn("NOMBRE");

        nameCol.setCellValueFactory(new PropertyValueFactory("nombre"));

        TableColumn compulsoryCol = new TableColumn("OBLIGATORIO");

        compulsoryCol.setCellValueFactory(new PropertyValueFactory("obligatorio"));

        compulsoryCol.setCellFactory(col -> new TableCell<Course, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"NO", "SI"};
                    setText(level[item ? 1 : 0]);

                }
            }
        });

        TableColumn commitmentletterCol = new TableColumn("CARTA COMPROMISO");

        commitmentletterCol.setCellValueFactory(new PropertyValueFactory("cartacompromiso"));

        commitmentletterCol.setCellFactory(col -> new TableCell<Course, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"NO", "SI"};
                    setText(level[item ? 1 : 0]);

                }
            }
        });

        TableDocumentation.setItems(data);
        TableDocumentation.getColumns().addAll(iddocumenttypeCol, nameCol, compulsoryCol, commitmentletterCol);
    }

    public void UploadCourses() {

        TableCourse.getItems().clear();
        TableCourse.getColumns().clear();

        List<Course> courses = this.coursedao.toList();

        ObservableList<Course> data = FXCollections.observableArrayList(courses);

        TableColumn idcourseCol = new TableColumn("ID");

        idcourseCol.setCellValueFactory(new PropertyValueFactory("idcurso"));

        TableColumn levelCol = new TableColumn("NIVEL");

        levelCol.setCellValueFactory(new PropertyValueFactory("nivel"));
        levelCol.setCellFactory(col -> new TableCell<Course, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"Primaria", "Secundaria"};
                    setText(level[item]);

                }
            }
        });

        TableColumn gradeCol = new TableColumn("GRADO");

        gradeCol.setCellValueFactory(new PropertyValueFactory("grado"));

        gradeCol.setCellFactory(col -> new TableCell<Course, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};
                    setText(level[item]);

                }
            }
        });

        TableColumn parallelCol = new TableColumn("PARALELO");

        parallelCol.setCellValueFactory(new PropertyValueFactory("paralelo"));

        TableColumn quotaCol = new TableColumn("CUPO");

        quotaCol.setCellValueFactory(new PropertyValueFactory("cupo_max"));

        TableColumn admitsnewCol = new TableColumn("ADMITE NUEVOS");

        admitsnewCol.setCellValueFactory(new PropertyValueFactory("admite_nuevos"));

        admitsnewCol.setCellFactory(col -> new TableCell<Course, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] level = {"NO", "SI"};
                    setText(level[item ? 1 : 0]);

                }
            }
        });

        TableCourse.setItems(data);
        TableCourse.getColumns().addAll(idcourseCol, levelCol, gradeCol, parallelCol, quotaCol, admitsnewCol);

    }

    private char updateText() {
        char prueba = 'A';
        if (CboxLevelCourse.getSelectionModel().getSelectedIndex() != -1 && CboxGradeCourse.getSelectionModel().getSelectedIndex() != -1) {
            prueba = coursedao.reeturnParallel(CboxLevelCourse.getSelectionModel().getSelectedIndex(), CboxGradeCourse.getSelectionModel().getSelectedIndex());
            switch (prueba) {
                case '-' -> {
                    prueba = 'A';
                    TextPalallel.setText("A");
                }
                case 'Z' ->
                    showAlert("Error", "Maximo de cursos permitido", Alert.AlertType.ERROR);
                default -> {
                    prueba++;
                    TextPalallel.setText(String.valueOf(prueba));
                }
            }
        }
        return prueba;
    }

    private void diseble() {
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

    private ContextMenu CourseOptions;
    private ContextMenu DocumentationOptions;
    private Course courseselect;
    private Documentation documentationselect;

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

        try {
            this.scoursedao = new Subject_courseDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        CboxLevelCourse.setOnAction(event -> updateText());
        CboxGradeCourse.setOnAction(event -> updateText());

        UploadCourses();
        LoadDocumentation();

        //CURSO
        CourseOptions = new ContextMenu();

        MenuItem EditCourse = new MenuItem("Editar");

        CourseOptions.getItems().addAll(EditCourse);

        EditCourse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                int index = TableCourse.getSelectionModel().getSelectedIndex();

                courseselect = TableCourse.getItems().get(index);

                CboxLevelCourse.getSelectionModel().select(courseselect.getNivel());

                CboxGradeCourse.getSelectionModel().select(courseselect.getGrado());

                TextPalallel.setText(String.valueOf(courseselect.getParalelo()));

                TextQuota.setText(String.valueOf(courseselect.getCupo_max()));

                if (courseselect.getAdmite_nuevos()) {
                    RdYesAN.setSelected(true);
                } else {
                    RdNoAN.setSelected(true);
                }

                TextPalallel.setDisable(false);
                TextPalallel.setEditable(true);
                btnCancelar.setDisable(false);

            }
        });

        TableCourse.setContextMenu(CourseOptions);

        //DOCUMENTOS
        DocumentationOptions = new ContextMenu();

        MenuItem EditDocumentation = new MenuItem("Editar");

        DocumentationOptions.getItems().addAll(EditDocumentation);

        EditDocumentation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                int index = TableDocumentation.getSelectionModel().getSelectedIndex();

                documentationselect = TableDocumentation.getItems().get(index);

                TextNameDocumentation.setText(documentationselect.getNombre());

                if (documentationselect.isObligatorio()) {
                    RdYesO.setSelected(true);
                } else {
                    RdNoO.setSelected(true);
                }

                if (documentationselect.isCartacompromiso()) {
                    RdYesCC.setSelected(true);
                } else {
                    RdNoCC.setSelected(true);
                }

                btnCancelar.setDisable(false);
            }
        });

        TableDocumentation.setContextMenu(DocumentationOptions);

    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void enableCourseField() {
        CboxLevelCourse.setDisable(false);
        CboxGradeCourse.setDisable(false);
        TextPalallel.setDisable(false);
        TextPalallel.setEditable(false);
        TableCourse.setDisable(false);
        btnSave.setDisable(false);
    }

    private void disableCourseField() {
        CboxLevelCourse.setDisable(true);
        CboxGradeCourse.setDisable(true);
        TextPalallel.setDisable(true);
        TextQuota.setDisable(true);
        TableCourse.setDisable(true);
    }

    private void enableDocumentationField() {
        TextNameDocumentation.setDisable(false);
        RdNoO.setDisable(false);
        RdYesO.setDisable(false);
        RdNoCC.setDisable(false);
        RdYesCC.setDisable(false);
        RdNoAN.setDisable(true);
        RdYesAN.setSelected(false);
        RdYesAN.setDisable(true);
        TableDocumentation.setDisable(false);
        btnSave.setDisable(false);
    }

    private void disableDocumentationField() {
        TextNameDocumentation.setDisable(true);
        RdYesO.setDisable(true);
        RdNoO.setDisable(true);
        RdNoCC.setDisable(true);
        RdYesCC.setDisable(true);
        RdNoAN.setDisable(true);
        RdYesAN.setDisable(true);
        RdYesAN.setSelected(true);
    }

}
