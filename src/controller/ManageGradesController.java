/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Dao.AdvisorDao;
import Dao.CourseDao;
import Dao.ListCourseDao;
import interfaces.MainControllerAware;
import interfaces.DataReceiver;
import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Course;
import model.Extras;
import model.ListCourse;
import model.StudentNotes;
import model.User;

/**
 * FXML Controller class
 *
 * @author intel
 */
public class ManageGradesController implements Initializable, MainControllerAware, DataReceiver {

    @FXML
    private ComboBox<String> CbxCourses;

    @FXML
    private TableView<StudentNotes> TableNotes;

    @FXML
    private Label TextAdvisor;

    @FXML
    private Button BtnUploadFile;

    @FXML
    void OpenFile(ActionEvent event) {

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccione el Archivo Excel");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Archivos Excel", "*.xls", "*.xlsx"));
        Stage stage = (Stage) BtnUploadFile.getScene().getWindow();

        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            Extras.showAlert("Exito", "Archivo Subido Correctamente", Alert.AlertType.INFORMATION);
        }
    }

    private Map<String, String> pageMap = new HashMap<>();

    private MainMenuController mainController;

    private User advisor;

    private AdvisorDao advisordao;

    private ListCourseDao listcoursedao;

    private CourseDao coursedao;

    private int idcurso;

    private SchoolSettingsController st;

    public String[] optionsGrade = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

    private final String[] departments = {"LP", "SCZ", "CBBA", "OR", "PT", "CH", "TJA", "BE", "PD"};

    public void setAdvisor(User user) {
        this.advisor = user;
    }

    public User getAdvisor() {
        return advisor;
    }

    public void init() {
        if (advisor != null && advisor.getCargo() == 2) {
            TextAdvisor.setText("Asesor: " + advisor.getNombre() + " " + advisor.getApellido());
            List<Course> cursos = advisordao.search(advisor.getNombre() + " " + advisor.getApellido());

            List<String> listaCursosFormateados = new ArrayList<>();

            for (Course curso : cursos) {
                int grado = curso.getGrado();
                String gradoTexto = (grado >= 0 && grado < optionsGrade.length) ? optionsGrade[grado] : "Desconocido";
                listaCursosFormateados.add(gradoTexto + " " + curso.getParalelo());
            }

            ObservableList<String> observableCursos = FXCollections.observableArrayList(listaCursosFormateados);
            CbxCourses.setItems(observableCursos);
            CbxCourses.setValue("Seleccione");

        }
    }

    @Override
    public void onDataReceived(Object data) {
        if (data instanceof User user) {
            this.advisor = user;
            System.out.println("Usuario recibido: " + advisor.getNombre() + " " + advisor.getApellido());
            init();
        }
    }

    @Override
    public void setMainController(MainMenuController mainController) {
        this.mainController = mainController;
    }

    public void LoadList() {

        TableNotes.getItems().clear();
        TableNotes.getColumns().clear();

        List<ListCourse> lista = null;

        try {
            lista = listcoursedao.listnotes(idcurso, LocalDate.now().getYear());
        } catch (SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        Map<String, StudentNotes> studentmap = new LinkedHashMap<>();
        Set<String> materiasSet = new LinkedHashSet<>();

        for (ListCourse course : lista) {
            String key = course.getNameStudent() + "|" + course.getCedula_identidad();
            materiasSet.add(course.getNameMateria());

            StudentNotes estudiante = studentmap.get(key);
            if (estudiante == null) {
                estudiante = new StudentNotes(course.getNameStudent(), course.getCedula_identidad());
                studentmap.put(key, estudiante);
            }
            // Asignar nota (convertir BigDecimal a String)
            estudiante.setNota(course.getNameMateria(),
                    course.getNota() != null ? course.getNota().toString() : "");
        }

        List<StudentNotes> estudiantesOrdenados = new ArrayList<>(studentmap.values());
        estudiantesOrdenados.sort(Comparator.comparing(s -> {
            String[] partes = s.getNombreCompleto().split(" ");
            int len = partes.length;
            String primerApellido = len > 1 ? partes[len - 2] : "";
            String segundoApellido = partes[len - 1];
            return (primerApellido + " " + segundoApellido).toLowerCase();
        }));

        ObservableList<StudentNotes> data = FXCollections.observableArrayList(estudiantesOrdenados);

        // Columna número
        TableColumn<StudentNotes, Number> idCol = new TableColumn<>("N°");
        idCol.setCellValueFactory(cd
                -> new ReadOnlyObjectWrapper<>(TableNotes.getItems().indexOf(cd.getValue()) + 1)
        );

        // Columna nombre completo
        TableColumn<StudentNotes, String> nameCol = new TableColumn<>("Estudiante");
        nameCol.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getNombreCompleto()));

        // Columna CI
        TableColumn<StudentNotes, String> ciCol = new TableColumn<>("CI");
        ciCol.setCellValueFactory(cd -> new ReadOnlyStringWrapper(cd.getValue().getCi()));

        ciCol.setCellFactory(col -> new TableCell<StudentNotes, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || !item.contains("-")) {
                    setText(null);
                } else {
                    try {
                        String[] parts = item.split("-");

                        switch (parts.length) {
                            case 2 -> {
                                String base = parts[0];
                                int index = Integer.parseInt(parts[1]);
                                if (index >= 0 && index < departments.length) {
                                    setText(base + "-" + departments[index]);
                                } else {
                                    setText(item);
                                }
                            }
                            case 3 -> {
                                String base = parts[0];
                                String letra = parts[1];
                                int index = Integer.parseInt(parts[2]);
                                if (index >= 0 && index < departments.length) {
                                    setText(base + "-" + letra + "-" + departments[index]);
                                } else {
                                    setText(item);
                                }
                            }
                            default ->
                                setText(item);
                        }

                    } catch (NumberFormatException e) {
                        setText(item);
                    }
                }
            }
        });
        TableNotes.getColumns().addAll(idCol, nameCol, ciCol);

        // Columnas dinámicas para materias
        for (String materia : materiasSet) {
            TableColumn<StudentNotes, String> colMateria = new TableColumn<>(materia);
            colMateria.setCellValueFactory(cd -> cd.getValue().notaProperty(materia));
            TableNotes.getColumns().add(colMateria);
        }

        TableNotes.setItems(data);

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            advisordao = new AdvisorDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        try {
            listcoursedao = new ListCourseDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        st = new SchoolSettingsController();

        try {
            coursedao = new CourseDao();
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(ManageGradesController.class.getName()).log(Level.SEVERE, null, ex);
        }

        CbxCourses.setOnAction(event -> {
            String seleccionado = CbxCourses.getSelectionModel().getSelectedItem();
            this.idcurso = coursedao.idcourse(st.verifycourse(seleccionado));

            if (!CbxCourses.getSelectionModel().isEmpty()) {
                LoadList();
            }

        });

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
