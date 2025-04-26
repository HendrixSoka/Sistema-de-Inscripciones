/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import interfaces.DataReceiver;
import interfaces.MainControllerAware;
import javafx.fxml.Initializable;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.scene.Parent;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.control.Hyperlink;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MainMenuController implements Initializable {

    @FXML
    private AnchorPane contentPane;
    @FXML
    private HBox breadcrumbContainer;

    private Map<String, String> pageMap;

    @FXML
    private ImageView ilogo;

    @FXML
    public Label textuser;
    
    @FXML
    void btnexitAction(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar salida");
        alert.setHeaderText(null);
        alert.setContentText("¿Seguro que deseas salir?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Platform.exit();
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Cargar la vista inicial al arrancar la aplicación
        Image logo = new Image(getClass().getResourceAsStream("/resources/icons/logo.png"));
        ilogo.setImage(logo);

        pageMap = new LinkedHashMap<>();
        pageMap.put("Menu Principal", "MenuOptions");
        loadView("MenuOptions");
        updateBreadcrumb();
    }

    public void setSubController(Object subController) {
        if (subController instanceof MainControllerAware) {
            ((MainControllerAware) subController).setMainController(this);
        }
    }

    public void loadView(String pageName) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + pageName + ".fxml"));
            Parent root = loader.load();

            Object subController = loader.getController();

            if (subController instanceof MainControllerAware) {
                ((MainControllerAware) subController).setMainController(this);
            }

            contentPane.getChildren().clear();

            // Añadir la nueva vista cargada
            contentPane.getChildren().add(root);

            // Ajustar la vista cargada al tamaño del contenedor
            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);
            System.out.println("Vista cargada: " + pageName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addPage(String pageName, String fxmlFile) {
        pageMap.put(pageName, fxmlFile);
        updateBreadcrumb();
    }

    private void updateBreadcrumb() {

        breadcrumbContainer.getChildren().clear();

        List<String> pageNames = new ArrayList<>(pageMap.keySet());
        for (int i = 0; i < pageNames.size() - 1; i++) {
            String page = pageNames.get(i);
            Hyperlink link = new Hyperlink(page);
            link.setOnAction(e -> {

                navigateToPage(page);
            });
            breadcrumbContainer.getChildren().add(link);

            Label separator = new Label(">");
            breadcrumbContainer.getChildren().add(separator);
        }

        if (!pageNames.isEmpty()) {
            Label label = new Label(pageNames.get(pageNames.size() - 1));
            breadcrumbContainer.getChildren().add(label);
        }
    }

    private void navigateToPage(String pageName) {
        if (pageMap.containsKey(pageName)) {

            loadView(pageMap.get(pageName));
            Iterator<Map.Entry<String, String>> iterator = pageMap.entrySet().iterator();
            boolean foundCurrentPage = false;

            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                if (entry.getKey().equals(pageName)) {
                    foundCurrentPage = true;
                } else if (foundCurrentPage) {
                    // Eliminar las páginas que están después de la página actual
                    iterator.remove();
                }
            }
            updateBreadcrumb();
        } else {
            System.out.println("Error: La página '" + pageName + "' no está registrada.");
        }
    }

    public void loadSceneWithData(String pageName, Object data) {
        try {
            System.out.println("Intentando cargar: " + pageName);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/" + pageName + ".fxml"));
            Parent root = loader.load();
            System.out.println("FXML cargado correctamente");

            Object controller = loader.getController();
            System.out.println("Controlador cargado: " + controller);

            if (controller instanceof DataReceiver) {
                System.out.println("Enviando datos al controlador");
                ((DataReceiver) controller).onDataReceived(data);
            } else {
                System.out.println("El controlador no implementa DataReceiver");
            }

            contentPane.getChildren().clear();
            contentPane.getChildren().add(root);

            AnchorPane.setTopAnchor(root, 0.0);
            AnchorPane.setBottomAnchor(root, 0.0);
            AnchorPane.setLeftAnchor(root, 0.0);
            AnchorPane.setRightAnchor(root, 0.0);

            System.out.println("Vista cargada: " + pageName);
        } catch (IOException e) {
            System.out.println("Error al cargar la vista: " + pageName);
            e.printStackTrace();
        }
    }

    public void pop() {
        if (pageMap.size() <= 1) {
            System.out.println("No hay páginas anteriores para retroceder.");
            return;
        }

        // Obtener las claves en orden
        List<String> keys = new ArrayList<>(pageMap.keySet());

        // Eliminar la última página
        String lastKey = keys.get(keys.size() - 1);
        pageMap.remove(lastKey);
        updateBreadcrumb();

    }

}
