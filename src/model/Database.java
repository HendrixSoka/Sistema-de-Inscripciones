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

    private static final String url = "jdbc:mysql://nozomi.proxy.rlwy.net:21344/railway";
    private static final String user = "root";
    private static final String password = "dWblBrkXyDkFdTmPYHRVxIVnxSImvQXO";
    private static final String driver = "com.mysql.cj.jdbc.Driver";

    public Database() throws ClassNotFoundException {
        try {
            Class.forName(driver);
            System.out.println("¡Driver cargado correctamente!");
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el driver de MySQL");
            throw e;
        }
    }

    public Connection getConnection() {
        try {
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            System.err.println("Error al obtener conexión a la base de datos");
            e.printStackTrace();
            return null;
        }
    }
}
