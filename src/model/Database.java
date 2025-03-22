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
 * @author mauricioteranlimari
 */
public class Database {
    private static final String url = "jdbc:mysql://nozomi.proxy.rlwy.net:21344/railway";
    private static final String user = "root";
    private static final String password = "dWblBrkXyDkFdTmPYHRVxIVnxSImvQXO";
    
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url,user,password);
    }
}
