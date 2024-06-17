import database.DBConnection;
import menu.Menu_Inicio_App;
import model.Cliente;
import repositories.ClienteRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
       /*
        Connection connection = DBConnection.getConnection();

        try {
            System.out.println(connection.getCatalog());// solo por ver la conex, que ya existe la database
            // TODO: 09/06/2024 continuar creando la DBCONECT proximo dia seguimos
            // que estoy mas que hartao con el GIT del huevo y sus santos huevos
        } catch (SQLException e) {
            System.err.println("Error ejecucion conexion en Main");
        }*/

        Menu_Inicio_App menu = new Menu_Inicio_App();
        ClienteRepository clienteRepository = new ClienteRepository();
        menu.menuInicial();//
        //clienteRepository.registrarClienteNuevo();

        //clienteRepository.cifradoPassword("jonatan25gomez");

        //clienteRepository.verificarPasswordParaInicio("Hashing@Password1.com","Abcd1234");
        /*
        ProbandoHash

        Hashing@Password.com

        Abcd1234

        */
        

    }
}
