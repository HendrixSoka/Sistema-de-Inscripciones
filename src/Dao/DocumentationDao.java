/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import model.Database;
import model.Documentation;
import java.sql.Connection;
import java.sql.PreparedStatement;

/**
 *
 * @author mauricioteranlimari
 */
public class DocumentationDao {
    
     private Database DocumentationConnection;

    public DocumentationDao() throws ClassNotFoundException {
        
        this.DocumentationConnection = new Database();
        
    }
     
    public boolean register(Documentation documentation) {

        try {

            String SQL =  "INSERT INTO tipo_documento(nombre, obligatorio, cartacompromiso) VALUES (?, ?, ?)";
            
            Connection connection = this.DocumentationConnection.getConnection();
            
            PreparedStatement sentence = connection.prepareStatement(SQL);
            
            sentence.setString(1, documentation.getNombre());
            sentence.setBoolean(2, documentation.isObligatorio());
            sentence.setBoolean(3, documentation.isCartacompromiso());
            
            sentence.executeUpdate();
            sentence.close();
            
            return true;
            
        } catch (Exception e) {
            System.err.println("Ocurrio un error al registrar el documento");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }
     
}
