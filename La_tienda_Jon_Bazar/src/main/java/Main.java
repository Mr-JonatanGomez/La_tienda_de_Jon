import repositories.ExtraccionProductosJSON;
import menu.Menu_Inicio_App;
import repositories.ClienteRepository;

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
        ExtraccionProductosJSON extraccionProductosJSON=new ExtraccionProductosJSON();

        // esto agrega los productos del JSON si no hay productos en la database
        extraccionProductosJSON.agregarProductosEnDatabase();


        menu.menuInicial();//
        //clienteRepository.registrarClienteNuevo();

        //clienteRepository.cifradoPassword("jonatan25gomez");

        //clienteRepository.verificarPasswordParaInicio("Hashing@Password1.com","Abcd1234");

        

    }
}
