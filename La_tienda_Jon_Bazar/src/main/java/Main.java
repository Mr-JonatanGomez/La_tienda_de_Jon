import database.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        Connection connection = DBConnection.getConnection();
        try {
            System.out.println(connection.getCatalog());// solo por ver la conex, que ya existe la database
            // TODO: 09/06/2024 continuar creando la DBCONECT proximo dia seguimos
        } catch (SQLException e) {
            System.err.println("Error ejecucion conexion en Main");
        }
    }
}
