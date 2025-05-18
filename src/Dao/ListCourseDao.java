/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Dao;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import model.Database;
import model.ListCourse;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.ResultSet;
import java.time.LocalDate;

/**
 *
 * @author mauricioteranlimari
 */
public class ListCourseDao {

    private final Database ListConnection;

    public ListCourseDao() throws ClassNotFoundException, SQLException {
        this.ListConnection = new Database();
    }

    public List<ListCourse> listnotes(int idcurso, int gestion) throws SQLException {

        List<ListCourse> listaalumnos = new ArrayList<>();

        try {
            String SQL = "SELECT e.nombre,e.apellido,e.cedula_identidad,m.nombre,n.nota "
                    + "FROM estudiante e "
                    + "JOIN inscripcion i ON e.idestudiante = i.id_estudiante "
                    + "JOIN curso c ON i.id_curso = c.idcurso "
                    + "JOIN materia_curso cm ON cm.id_curso = c.idcurso "
                    + "JOIN materia m ON m.idmateria = cm.id_materia "
                    + "LEFT JOIN nota n ON n.id_inscripcion = i.idinscripcion AND n.id_materia = m.idmateria "
                    + "WHERE c.idcurso = ? "
                    + "AND i.gestion = ? "
                    + "ORDER BY e.apellido, e.nombre, m.nombre";

            Connection connection = this.ListConnection.getConnection();

            PreparedStatement sentence = connection.prepareStatement(SQL);

            sentence.setInt(1, idcurso);
            sentence.setInt(2, gestion);

            ResultSet data = sentence.executeQuery();

            while (data.next()) {

                ListCourse list = new ListCourse();

                list.setNameStudent(data.getString(1) + " " + data.getString(2));
                list.setCedula_identidad(data.getString(3));
                list.setNameMateria(data.getString(4));
                list.setNota(data.getBigDecimal(5));

                listaalumnos.add(list);

            }

            data.close();
            sentence.close();

        } catch (SQLException e) {
            System.err.println("Ocurrio un error al leer lista de estudiantes");
            System.err.println("Mensaje del error: " + e.getMessage());
            System.err.println("Detalle del error: ");

            e.printStackTrace();
        }
        return listaalumnos;
    }

}
