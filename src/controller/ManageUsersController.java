/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package controller;

import Dao.UserDao;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.StageStyle;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
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
    private Button BtnCancelar;
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
    private ComboBox<String> CboCharge;
    @FXML
    private TextField TextUserUser;

    @FXML
    void BtnAddOnAction(ActionEvent event) throws NoSuchAlgorithmException, Exception {
        if (selectUser == null) {
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
            //Crear Usuario en cuanto se ingrese el nombre
            usuario.setUsuario(TextUserUser.getText());
            //TextUserUser.setEditable(false);
            //encriptar contraseña
            String pass = Encrypt(TextPasswordUser.getText());
            usuario.setContrasena(pass);

            //Para verificar si se puede guardar en la base de datos
            boolean rsp = this.userdao.register(usuario);

            if (rsp) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Se registro correctamente el usuario");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();

                ClearFiels();
                LoadUsers();

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error al guardar");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
        } else {

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

            selectUser.setNombre(TextFnameUser.getText());
            selectUser.setApellido(TextLnameUser.getText());
            selectUser.setCelular(TextPhoneUser.getText());
            selectUser.setCorreo(TextEmailUser.getText());
            String selectedCargo = CboCharge.getValue();
            ObservableList<String> items = CboCharge.getItems();
            int selectedIndex = items.indexOf(selectedCargo);
            selectUser.setCargo(selectedIndex);
            selectUser.setUsuario(TextUserUser.getText());
            selectUser.setContrasena(Encrypt(TextPasswordUser.getText()));

            boolean rsp = this.userdao.Edit(selectUser);

            if (rsp) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Se guardo correctamente el usuario");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();

                ClearFiels();
                LoadUsers();

                selectUser = null;

                BtnCancelar.setDisable(true);

            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error al modificar");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
        }
    }

    @FXML
    void BtnCancelarOnAction(ActionEvent event) {

        selectUser = null;

        ClearFiels();

        BtnCancelar.setDisable(true);

    }

    private void ClearFiels() {
        TextFnameUser.clear();
        TextLnameUser.clear();
        TextCiUser.clear();
        TextPhoneUser.clear();
        TextEmailUser.clear();
        TextUserUser.clear();
        TextPasswordUser.clear();
        CboCharge.getSelectionModel().select("Seleccione");
    }
    

    public void LoadUsers() {

        tblUser.getItems().clear();
        tblUser.getColumns().clear();

        List<User> users = this.userdao.tolist();

        ObservableList<User> data = FXCollections.observableArrayList(users);

        TableColumn Idcol = new TableColumn("ID");
        Idcol.setCellValueFactory(new PropertyValueFactory("id"));

        TableColumn Namecol = new TableColumn("NOMBRE(S)");
        Namecol.setCellValueFactory(new PropertyValueFactory("nombre"));

        TableColumn Surnamecol = new TableColumn("APELLIDO(S)");
        Surnamecol.setCellValueFactory(new PropertyValueFactory("apellido"));

        TableColumn CIcol = new TableColumn("CI");
        CIcol.setCellValueFactory(new PropertyValueFactory("cedula_identidad"));

        TableColumn Phonecol = new TableColumn("CELULAR");
        Phonecol.setCellValueFactory(new PropertyValueFactory("celular"));

        TableColumn Emailcol = new TableColumn("CORREO");
        Emailcol.setCellValueFactory(new PropertyValueFactory("correo"));

        TableColumn Chargecol = new TableColumn("CARGO");
        Chargecol.setCellValueFactory(new PropertyValueFactory("cargo"));
        Chargecol.setCellFactory(col -> new TableCell<User, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    String[] cargos = {"Director/a", "Secretario/a", "Asesor/a", "Regente/a"};
                    setText(cargos[item]);

                }
            }
        });

        TableColumn Usercol = new TableColumn("USUARIO");
        Usercol.setCellValueFactory(new PropertyValueFactory("usuario"));

        TableColumn Passwordcol = new TableColumn("CONTRASEÑA");
        Passwordcol.setCellFactory(col -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {

                    setText(Decrypt(item));
                }
            }
        });
        Passwordcol.setCellValueFactory(new PropertyValueFactory("contrasena"));

        tblUser.setItems(data);
        tblUser.getColumns().addAll(Idcol, Namecol, Surnamecol, CIcol, Phonecol, Emailcol, Chargecol, Usercol, Passwordcol);

    }

    public boolean EditUsers() {
        try {

        } catch (Exception e) {
        }
        return true;
    }

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

    private String key = "PumasAndinos";

    public SecretKeySpec CreateKey(String password) {
        try {
            byte[] chain = password.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            chain = md.digest(chain);
            chain = Arrays.copyOf(chain, 16);
            SecretKeySpec secretKeySpec = new SecretKeySpec(chain, "AES");
            return secretKeySpec;

        } catch (Exception e) {
            return null;
        }
    }

    //Encriptacion
    public String Encrypt(String encrypt) {
        try {
            SecretKeySpec secretKeySpec = CreateKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);

            byte[] chain = encrypt.getBytes(StandardCharsets.UTF_8);
            byte[] encrypted = cipher.doFinal(chain);
            String chain_encrypted = Base64.getEncoder().encodeToString(encrypted);
            return chain_encrypted;

        } catch (Exception e) {
            return " ";
        }
    }

    //Desencriptar
    public String Decrypt(String decrypt) {
        try {
            SecretKeySpec secretKeySpec = CreateKey(key);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);

            byte[] chain = Base64.getDecoder().decode(decrypt);
            byte[] decrypted = cipher.doFinal(chain);
            String chain_decrypted = new String(decrypted);
            return chain_decrypted;

        } catch (Exception e) {
            return " ";
        }
    }

    private UserDao userdao;

    private ContextMenu OptionsUsers;

    private User selectUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        String[] cargos = {"Director/a", "Secretario/a", "Asesor/a", "Regente/Regenta"};
        ObservableList<String> items = FXCollections.observableArrayList(cargos);
        CboCharge.setItems(items);
        CboCharge.setValue("Seleccione");
        TextUserUser.setEditable(false);

        try {
            this.userdao = new UserDao();
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ManageUsersController.class.getName()).log(Level.SEVERE, null, ex);
        }

        LoadUsers();
        TextFnameUser.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.trim().isEmpty()) {
                String generatedUsername = GenerateUser(newValue);
                TextUserUser.setText(generatedUsername);
            } else {
                TextUserUser.setText("");
            }
        });
        OptionsUsers = new ContextMenu();

        MenuItem edit = new MenuItem("Editar");
        MenuItem delete = new MenuItem("Eliminar");

        OptionsUsers.getItems().addAll(edit, delete);

        edit.setOnAction((ActionEvent t) -> {
            int index = tblUser.getSelectionModel().getSelectedIndex();
            
            selectUser = tblUser.getItems().get(index);
            
            TextFnameUser.setText(selectUser.getNombre());
            TextLnameUser.setText(selectUser.getApellido());
            TextCiUser.setText(selectUser.getCedula_identidad());
            TextCiUser.setEditable(false);
            TextPhoneUser.setText(selectUser.getCelular());
            TextEmailUser.setText(selectUser.getCorreo());
            CboCharge.getSelectionModel().select(selectUser.getCargo());
            TextUserUser.setText(selectUser.getUsuario());
            TextUserUser.setEditable(true);
            TextPasswordUser.setText(Decrypt(selectUser.getContrasena()));
            
            BtnCancelar.setDisable(false);
        });

        delete.setOnAction((ActionEvent t) -> {
            int index = tblUser.getSelectionModel().getFocusedIndex();
            
            User deleteUser = tblUser.getItems().get(index);
            
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            
            alert.setTitle("Confirmacion");
            alert.setHeaderText(null);
            alert.setContentText("¿Desea eliminar el usuario: "
                    + deleteUser.getNombre() +" "+ deleteUser.getApellido()
                    + "?");
            alert.initStyle(StageStyle.UTILITY);
            
            Optional<ButtonType> result = alert.showAndWait();
            
            if (result.get() == ButtonType.OK) {
                
                boolean rsp = userdao.Detele(deleteUser.getId());
                
                if (rsp) {
                    
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                    alert2.setTitle("Exito");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Se elimino correctamente el usuario");
                    alert2.initStyle(StageStyle.UTILITY);
                    alert2.showAndWait();
                    
                    LoadUsers();
                    
                } else {
                    
                    Alert alert2 = new Alert(Alert.AlertType.ERROR);
                    alert2.setTitle("Error");
                    alert2.setHeaderText(null);
                    alert2.setContentText("Hubo un error al eliminar");
                    alert2.initStyle(StageStyle.UTILITY);
                    alert2.showAndWait();
                }
            }
        });

        tblUser.setContextMenu(OptionsUsers);

    }

}
