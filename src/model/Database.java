/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 *  
 *
 * @author mauricioteranlimari
 */
public class Database {

    private Connection connection;
    private static final String url = "jdbc:mysql://nozomi.proxy.rlwy.net:21344/railway";
    private static final String user = "root";
    private static final String password = "dWblBrkXyDkFdTmPYHRVxIVnxSImvQXO";
    private static final String driver = "com.mysql.cj.jdbc.Driver";

    public Database() throws ClassNotFoundException {

        try {
            Class.forName(driver);
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("¡Conexión exitosa!");
            } else {
                System.out.println("Error: No se pudo establecer la conexión.");
            }
        } catch (SQLException e) {
            System.err.println("Ocurrio un error en la conexion");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
    }

    public Connection getConnection() {
        return connection;
    }

}
