/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Dao.AdvisorDao;
import Dao.CourseDao;
import Dao.DocumentationDao;
import Dao.Subject_courseDao;
import Dao.UserDao;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;
import model.Advisor;
import model.Course;
import model.Documentation;
import model.Subject_course;
import model.User;

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

    @FXML
    private ComboBox<String> cbxA;

    @FXML
    private ComboBox<String> cbxC;

    @FXML
    private DatePicker dpF;

    @FXML
    private DatePicker dpI;

    @FXML
    private Button btnAsesor;

    private CourseDao coursedao;
    private UserDao userdao;
    private DocumentationDao documentationdao;
    private Subject_courseDao scoursedao;
    private AdvisorDao advisordao;
    private char parallel;
    private ContextMenu CourseOptions;
    private ContextMenu DocumentationOptions;
    private Course courseselect;
    private Documentation documentationselect;

    public String[] options = {"Gestionar Curso", "Gestionar Documentacion"};

    public String[] optionsLevel = {"Primaria", "Secundaria"};

    public String[] optionsGrade = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

    public char getParallel() {
        return parallel;
    }

    @FXML
    void BtnCancelarOnAction(ActionEvent event) {

        if (courseselect != null) {
            courseselect = null;
            cleanFieldsCourse();
            btnCancelar.setDisable(true);
        } else if (documentationselect != null) {
            documentationselect = null;
            cleanFieldsDocumentation();
            btnCancelar.setDisable(true);
        }else if(cbxA.getSelectionModel().getSelectedIndex() != -1){
            cleanFieldsDocumentation();
            cleanFieldsCourse();
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

                    disableDocumentationField();
                    enableCourseField();

                    cleanFieldsCourse();

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

                    cleanFieldsCourse();

                    disableDocumentationField();
                    enableCourseField();

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

                    disableCourseField();
                    enableDocumentationField();

                    cleanFieldsDocumentation();

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

                    disableCourseField();
                    enableDocumentationField();

                    cleanFieldsDocumentation();

                    documentationselect = null;

                    btnCancelar.setDisable(true);

                    LoadDocumentation();
                } else {
                    showAlert("Error", "Hubo un error", Alert.AlertType.ERROR);
                }

            }
        }

    }

    @FXML
    void btnAddAsesor(ActionEvent event) {

        if (cbxA.getSelectionModel().getSelectedIndex() != -1 && cbxC.getSelectionModel().getSelectedIndex() != -1) {

            Advisor advisor = new Advisor();

            //id del asesor
            advisor.setIdusuario(userdao.idasesor(cbxA.getSelectionModel().getSelectedItem()));

            //id del curso
            advisor.setIdcurso(coursedao.idcourse(verifycourse(cbxC.getSelectionModel().getSelectedItem())));

            //fecha inicio
            if (dpI.getValue().isBefore(LocalDate.now())) {
                showAlert("Error", "Fecha invalida, no debe ser anterior a la fecha actual", Alert.AlertType.ERROR);
                return;
            } else {
                advisor.setFechainicio(dpI.getValue());
            }

            //fecha fin
            if (dpF.getValue().isBefore(LocalDate.now())) {
                showAlert("Error", "Fecha invalida, no debe ser anterior a la fecha actual", Alert.AlertType.ERROR);
                return;
            } else {
                advisor.setFechafin(dpF.getValue());
            }
            
            boolean rsp = this.advisordao.register(advisor);
            if (rsp) {
                
                showAlert("Exito", "Se registro correctamente el asesor", Alert.AlertType.INFORMATION);

                cleanFieldsCourse();
                UploadCourses();
           
            } else {
                showAlert("Error", "Hugo un error al guardar", Alert.AlertType.ERROR);
               
            }

        }

    }

    private String verifycourse(String full) {
        for (int i = 0; i < optionsGrade.length; i++) {
            if (full.contains(optionsGrade[i])) {
                return full.replace(optionsGrade[i], String.valueOf(i));
            }
        }
        return null;
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

    private void cleanFieldsCourse() {

        TextPalallel.clear();
        TextQuota.clear();
        RdYesAN.setSelected(false);
        RdNoAN.setSelected(false);
        CboxSelect.getSelectionModel().select("Seleccione");
        CboxLevelCourse.getSelectionModel().select("Seleccione");
        CboxGradeCourse.getSelectionModel().select("Seleccione");
        cbxA.getSelectionModel().select("Seleccione");
        cbxC.getSelectionModel().select("Seleccione");
        dpI.setValue(null);
        dpF.setValue(null);
        btnAsesor.setDisable(true);
    }

    private void cleanFieldsDocumentation() {

        TextNameDocumentation.clear();
        RdYesO.setSelected(false);
        RdNoO.setSelected(false);
        RdYesCC.setSelected(false);
        RdNoCC.setSelected(false);
        CboxSelect.getSelectionModel().clearSelection();
        CboxSelect.setPromptText("Seleccione");
    }

    @FXML
    void setOnAction(ActionEvent event) {

        String seleccion = CboxSelect.getValue();
        if (seleccion != null) {
            if (seleccion.equals("Gestionar Curso")) {
                enableCourseField();
                disableDocumentationField();
            } else if (seleccion.equals("Gestionar Documentacion")) {
                enableDocumentationField();
                disableCourseField();
            } else {
                diseble();
            }
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

        TableColumn advisorsCol = new TableColumn("ASESOR");

        advisorsCol.setCellValueFactory(new PropertyValueFactory("asesor"));

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
        TableCourse.getColumns().addAll(idcourseCol, levelCol, gradeCol, parallelCol,advisorsCol, quotaCol, admitsnewCol);

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

        cbxA.setDisable(true);
        cbxC.setDisable(true);
        dpI.setDisable(true);
        dpF.setDisable(true);
        btnAsesor.setDisable(true);

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

        CboxSelect.getSelectionModel().select("Seleccione");
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        diseble();

        ObservableList<String> items = FXCollections.observableArrayList(this.options);

        CboxSelect.setItems(items);
        CboxSelect.setValue("Seleccione");

        ObservableList<String> itemsLevel = FXCollections.observableArrayList(this.optionsLevel);

        CboxLevelCourse.setItems(itemsLevel);
        CboxLevelCourse.setValue("Seleccione");

        ObservableList<String> itemsGrade = FXCollections.observableArrayList(this.optionsGrade);

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

        dpI.valueProperty().addListener((obs, oldDate, newDate) -> {
            if (newDate != null) {
                dpF.setValue(newDate.plusYears(1));
            }
        });

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

        try {
            this.userdao = new UserDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            this.advisordao = new AdvisorDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(SchoolSettingsController.class.getName()).log(Level.SEVERE, null, ex);
        }

        //lista de asesores
        ObservableList<String> observableListA = FXCollections.observableArrayList(userdao.Advisors());
        cbxA.setItems(observableListA);
        cbxA.setValue("Seleccione");

        //lista de los cursos
        ObservableList<String> observableListC = FXCollections.observableArrayList(coursedao.CoursesAdvisors());
        cbxC.setItems(observableListC);
        cbxC.setValue("Seleccione");

        cbxA.setOnAction(event -> {
            btnAsesor.setDisable(false);
            CboxLevelCourse.setDisable(true);
            CboxGradeCourse.setDisable(true);
            TextPalallel.setDisable(true);
            TextQuota.setDisable(true);
            RdNoAN.setDisable(true);
            RdYesAN.setDisable(true);
            btnSave.setDisable(true);
            btnCancelar.setDisable(false);
            
        });
        CboxLevelCourse.setOnAction(event ->{
            cbxA.setDisable(true);
            cbxC.setDisable(true);
            dpI.setDisable(true);
            dpF.setDisable(true);
            btnAsesor.setDisable(true);
        });

        CboxLevelCourse.setOnAction(event -> updateText());
        CboxGradeCourse.setOnAction(event -> updateText());

        UploadCourses();
        LoadDocumentation();

        //CURSO
        CourseOptions = new ContextMenu();

        MenuItem EditCourse = new MenuItem("Editar");
        MenuItem DeleteCourse = new MenuItem("Eliminar");

        CourseOptions.getItems().addAll(EditCourse, DeleteCourse);

        DeleteCourse.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                int index = TableCourse.getSelectionModel().getSelectedIndex();

                Course deleteCourse = TableCourse.getItems().get(index);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setTitle("Confirmacion");
                alert.setHeaderText(null);

                String[] grados = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

                alert.setContentText("¿Desea eliminar el curso: "
                        + grados[deleteCourse.getGrado()] + " " + deleteCourse.getParalelo() + "?");

                alert.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {

                    boolean rsp = coursedao.deleteCourse(deleteCourse.getIdcurso());

                    if (rsp) {

                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Exito");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Se elimino correctamente el curso");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();

                        UploadCourses();

                        disableDocumentationField();
                        enableCourseField();

                    } else {

                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Error");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Hubo un error al eliminar");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();
                    }
                }

            }

        });

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
        MenuItem DeleteDocumentation = new MenuItem("Eliminar");

        DocumentationOptions.getItems().addAll(EditDocumentation, DeleteDocumentation);

        DeleteDocumentation.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent t) {

                int index = TableDocumentation.getSelectionModel().getSelectedIndex();

                Documentation deleteDocumentation = TableDocumentation.getItems().get(index);

                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);

                alert.setTitle("Confirmacion");
                alert.setHeaderText(null);
                alert.setContentText("¿Desea eliminar el usuario: "
                        + deleteDocumentation.getNombre() + "?");

                alert.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> result = alert.showAndWait();

                if (result.get() == ButtonType.OK) {

                    boolean rsp = documentationdao.deleteDocumentation(deleteDocumentation.getIdtipo_documento());

                    if (rsp) {

                        Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                        alert2.setTitle("Exito");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Se elimino correctamente el documento");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();

                        LoadDocumentation();

                        disableCourseField();
                        enableDocumentationField();

                    } else {

                        Alert alert2 = new Alert(Alert.AlertType.ERROR);
                        alert2.setTitle("Error");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Hubo un error al eliminar");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();
                    }
                }

            }

        });

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
        //Combo box de Nivel y Curso 
        CboxLevelCourse.setDisable(false);
        CboxGradeCourse.setDisable(false);
        //Texto Paralelo y Text CupoMax
        TextPalallel.setDisable(false);
        TextPalallel.setEditable(false);
        TextQuota.setDisable(false);
        TextQuota.setEditable(false);
        //Radio buton Admite nuevos
        RdNoAN.setDisable(true);
        RdYesAN.setDisable(true);
        RdYesAN.setSelected(true);
        //Tabla curso
        TableCourse.setDisable(false);
        btnSave.setDisable(false);
        //Asesor
        cbxA.setDisable(false);
        cbxC.setDisable(false);
        dpI.setDisable(false);
        dpI.setEditable(false);
        dpI.setValue(LocalDate.now());
        dpF.setDisable(false);
        dpF.setValue(LocalDate.now().plusYears(1));
        btnAsesor.setDisable(true);
    }

    private void disableCourseField() {
        //Combo box de Nivel y Curso 
        CboxLevelCourse.setDisable(true);
        CboxGradeCourse.setDisable(true);
        //Texto Paralelo y Text CupoMax
        TextPalallel.setDisable(true);
        TextQuota.setDisable(true);
        //Tabla curso
        TableCourse.setDisable(true);
        //Radio buton
        RdNoAN.setDisable(true);
        RdYesAN.setDisable(true);
        //Asesor
        cbxA.setDisable(true);
        cbxC.setDisable(true);
        dpI.setDisable(true);
        dpF.setDisable(true);
        btnAsesor.setDisable(true);

    }

    private void enableDocumentationField() {

        //Text nombre documento
        TextNameDocumentation.setEditable(true);
        TextNameDocumentation.setDisable(false);
        //Radio buton para ver si el documento es obligatorio
        RdNoO.setDisable(false);
        RdYesO.setDisable(false);
        //Radio buton para ver si el documento tiene carta compromiso
        RdNoCC.setDisable(false);
        RdYesCC.setDisable(false);
        TableDocumentation.setDisable(false);
        btnSave.setDisable(false);
    }

    private void disableDocumentationField() {
        //Text nombre documento
        TextNameDocumentation.setDisable(true);
        //Radio buton para ver si el documento es obligatorio
        RdYesO.setDisable(true);
        RdNoO.setDisable(true);
        //Radio buton para ver si el documento tiene carta compromiso
        RdNoCC.setDisable(true);
        RdYesCC.setDisable(true);
        //Tabla documentacio
        TableDocumentation.setDisable(true);
    }

}
