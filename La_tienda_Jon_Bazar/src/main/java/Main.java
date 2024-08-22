import repositories.ProductsRepository;
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
        ProductsRepository productsRepository =new ProductsRepository();


        productsRepository.llevarProductosADatabase();


        menu.menuInicial();//


        //clienteRepository.verificarPasswordParaInicio("Hashing@Password1.com","Abcd1234");

        //     jjgomez@mail.es   jon123gom a
        //Crisp and juicy acid apple, ideal for snacking, baking, or adding a refreshing crunch to your meals.


    }
}
