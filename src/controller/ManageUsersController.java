/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Dao.UserDao;
import com.sun.jfx.incubator.scene.control.richtext.FxPathBuilder;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.StageStyle;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import model.Database;
import model.User;

/**
 * FXML Controller class
 *
 * @author intel
 */
public class ManageUsersController implements Initializable {

    /**
     * Initializes the controller class.
     */
    @FXML
    private Button BtnAdd;
    @FXML
    private Button BtnBuscar;
    @FXML
    private TextField TextBuscarfCi;
    @FXML
    private TextField TextCiUser;
    @FXML
    private TextField TextEmailUser;
    @FXML
    private TextField TextFnameUser;
    @FXML
    private TextField TextLnameUser;
    @FXML
    private TextField TextPasswordUser;
    @FXML
    private TextField TextPhoneUser;
    @FXML
    private TableView<User> tblUser;
    @FXML
    private TableColumn<User, Integer> colId;
    @FXML
    private TableColumn<User, String> colNombre;
    @FXML
    private TableColumn<User, String> colApellidos;
    @FXML
    private TableColumn<User, String> colCI;
    @FXML
    private TableColumn<User, String> colCelular;
    @FXML
    private TableColumn<User, Integer> colCargo;
    @FXML
    private TableColumn<User, String> colUsuario;
    @FXML
    private TableColumn<User, String> colCorreo;
    @FXML
    private TableColumn<User, String> colContrasena;
    @FXML
    private ComboBox<String> CboCharge;

    @FXML
    void BtnAddOnAction(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        //Verificar si se quiere guardar algun NULL
        if (TextFnameUser.getText().isEmpty() || TextLnameUser.getText().isEmpty()
                || TextCiUser.getText().isEmpty() || TextPhoneUser.getText().isEmpty()
                || TextEmailUser.getText().isEmpty() || TextPasswordUser.getText().isEmpty()) {

            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Todos los campos deben ser llenados.");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            return;
        }

        User usuario = new User();

        //Nombre
        usuario.setNombre(TextFnameUser.getText());
        //Apellido
        usuario.setApellido(TextLnameUser.getText());
        //Cedula_Identidad
        usuario.setCedula_identidad(TextCiUser.getText());
        //Celular, verifica si es valido
        if (VerifyNumberUser(TextPhoneUser.getText())) {
            usuario.setCelular(TextPhoneUser.getText());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Numero de celular invalido");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            TextPhoneUser.clear();
        }
        //Correo, verifica si es valido
        if (VerifyEmailUser(TextEmailUser.getText())) {
            usuario.setCorreo(TextEmailUser.getText());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Formato de correo invalido");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
            TextEmailUser.clear();
        }
        //Agrega cargo
        String selectedCargo = CboCharge.getValue();
        ObservableList<String> items = CboCharge.getItems();
        int selectedIndex = items.indexOf(selectedCargo);
        usuario.setCargo(selectedIndex);
        usuario.setUsuario(GenerateUser(TextFnameUser.getText()));
        //Encripta Constrasenas
        String password = TextPasswordUser.getText();
        byte[] salt = getSalt();
        String hashedPassword = hashPassword(password, salt);
        usuario.setContrasena(hashedPassword);

        //Para verificar si se puede guardar en la base de datos
        boolean rsp = this.userdao.register(usuario);

        if (rsp) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Exito");
            alert.setHeaderText(null);
            alert.setContentText("Se registro correctamente el usuario");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();

            TextFnameUser.clear();
            TextLnameUser.clear();
            TextCiUser.clear();
            TextPhoneUser.clear();
            TextEmailUser.clear();
            TextPasswordUser.clear();
            CboCharge.setValue(null);

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Hubo un error");
            alert.initStyle(StageStyle.UTILITY);
            alert.showAndWait();
        }
    }

    private ObservableList<User> users;

    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

    public static boolean VerifyNumberUser(String number) {
        return number != null && number.matches("\\d{8}");
    }

    public static boolean VerifyEmailUser(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String GenerateUser(String name) {
        String[] nameParts = name.split(" ");
        String firstName = nameParts[0];
        String secondLetter = nameParts.length > 1 ? nameParts[1].substring(0, 1) : "";
        Random random = new Random();
        int randomNum = random.nextInt(1000);
        String randomNumber = String.format("%03d", randomNum);
        return firstName + secondLetter + randomNumber;
    }

    public static byte[] getSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] salt = new byte[16];
        secureRandom.nextBytes(salt);
        return salt;
    }

    public static String hashPassword(String password, byte[] salt) throws Exception {
        PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 256);
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hashedPassword = factory.generateSecret(spec).getEncoded();

        return bytesToHex(hashedPassword);
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public static boolean verifyPassword(String password, byte[] salt, String storedHash) throws Exception {
        String hashedPassword = hashPassword(password, salt);

        return hashedPassword.equals(storedHash);
    }

    private UserDao userdao;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String[] cargos = {"Director/a", "Secretario/a", "Asesor/a", "Regente/Regenta"};
        ObservableList<String> items = FXCollections.observableArrayList(cargos);
        CboCharge.setItems(items);
        CboCharge.setValue("Seleccione");

        try {
            this.userdao = new UserDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
