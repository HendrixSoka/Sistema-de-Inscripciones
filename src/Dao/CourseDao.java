/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import model.Course;
import model.Database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 *
 * @author mauricioteranlimari
 */
public class CourseDao {

    private Database CourseConnection;

    public CourseDao() throws ClassNotFoundException {

        this.CourseConnection = new Database();

    }

    public boolean register(Course course) {

        try {

            String SQL = "INSERT INTO curso(nivel, grado, paralelo) VALUES (?, ?, ?)";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, course.getNivel());
            sentence.setInt(2, course.getGrado());
            sentence.setString(3, String.valueOf(course.getParalelo()));

            sentence.executeUpdate();
            sentence.close();

            return true;

        } catch (Exception e) {
            System.err.println("Ocurrio un error al registrar curso");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();

            return false;
        }
    }

    public char reeturnParallel(int level, int grade) {
        char parallel = 'A';
        try {

            String SQL = "SELECT paralelo FROM curso WHERE nivel = ? AND grado = ? ORDER BY paralelo DESC LIMIT 1";

            Connection connection = this.CourseConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, level);
            sentence.setInt(2, grade);

            ResultSet result = sentence.executeQuery();

            if (result.next()) {
                parallel = result.getString("paralelo").charAt(0);
                Course paralelo = new Course();
                paralelo.setParalelo(parallel);
            }

            result.close();
            sentence.close();

        } catch (Exception e) {
            System.err.println("Ocurrio un error al buscar paralelo");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
            
            return '-';
        }

        return parallel;
    }
}
