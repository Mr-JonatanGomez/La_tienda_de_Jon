package database;

import com.mysql.cj.jdbc.Driver;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    //configuration for connection to database

    static Connection connection = null;// conexion a databse


    private static void createConennection(){
        //1 cargar driver en memoria (classforName),
        //2 abrir la conexion (CON una URI(jdbc:mysql://127.0.0.1/basedatos))
        //3 la conex = a la carga del DRIVER
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String uri = "jdbc:mysql://127.0.0.1:3306/tienda_jon";
            connection = DriverManager.getConnection(uri,"root","");
        } catch (ClassNotFoundException e) {
            System.err.println("No has descargado el driver");
        } catch (SQLException e) {
            System.err.println("Error en la ejecucion o carga del DRIVER SQL");
        }
    }
    public static Connection getConnection() {
        if (connection==null){
            createConennection();
        }
        return connection;
    }

    public static void closeConnection(){
        try {
            connection.close();
            connection=null;
            /* asi cuando se cierra se iguala a nulo para cuando se tenga
            que abrir de nueva sea nulo y la crea de nuevo*/
        } catch (SQLException e) {
            System.err.println("Error al cerrar la conexion de database");
        }
    }

}
