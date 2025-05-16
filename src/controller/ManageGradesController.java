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
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Course;
import model.ListCourse;
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
    private TableView<ListCourse> TableNotes;

    @FXML
    private Label TextAdvisor;

    private Map<String, String> pageMap = new HashMap<>();

    private MainMenuController mainController;

    private User advisor;

    private AdvisorDao advisordao;

    private ListCourseDao listcoursedao;

    private CourseDao coursedao;

    private int idcurso;

    private SchoolSettingsController st;

    public String[] optionsGrade = {"Primero", "Segundo", "Tercero", "Cuarto", "Quinto", "Sexto"};

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

        ObservableList<ListCourse> data = FXCollections.observableArrayList(lista);

        TableColumn<ListCourse, Number> idCol = new TableColumn<>("NUMERO LISTA");

        idCol.setCellValueFactory(cellData -> {
            return new ReadOnlyObjectWrapper<>(TableNotes.getItems().indexOf(cellData.getValue()) + 1);
        });

        TableColumn name = new TableColumn("ESTUDIANTE");

        name.setCellValueFactory(new PropertyValueFactory("NameStudent"));

        TableNotes.setItems(data);

        TableNotes.getColumns().addAll(idCol, name);

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
            System.out.println("Curso seleccionado: " + seleccionado);

            this.idcurso = coursedao.idcourse(st.verifycourse(seleccionado));

            System.out.println("El id del curso es: " + idcurso);
            
            
            if(!CbxCourses.getSelectionModel().isEmpty()){
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
