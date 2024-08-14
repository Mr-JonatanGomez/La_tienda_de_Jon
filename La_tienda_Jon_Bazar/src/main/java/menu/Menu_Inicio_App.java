package menu;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.Cliente;
import repositories.ProductsRepository;
import repositories.ClienteRepository;

import java.util.Scanner;
@Getter
@Setter
@NoArgsConstructor
public class Menu_Inicio_App {
    ClienteRepository clienteRepository = new ClienteRepository();
    ProductsRepository productsRepository = new ProductsRepository();
    Scanner sc = new Scanner(System.in);
    String clienteActual = null;



    public boolean comprobarAdmin(){
        if("jjgomez@mail.es".equals(clienteActual)){
            return true;
        }
        return false;
        // TODO: 14/08/2024 incorporar a menuInicial la comprobacion y derivar a un menú u otro (admin/user)
    }
    public void menuInicial() {


        int opcion = -1;
        //PARTE INICIO SESION NO ACTIVA HASTA QUE HAGA EL RESSULT SET
        do {
            System.out.println("""
                    MENU DE INICIO
                                    
                    1- INICIAR SESIÓN
                    2- REGISTRARSE
                    3- Mostrar PRODUCTOS EN CRUD
                                    
                    0- SALIR
                    """);
            opcion = sc.nextInt();

            switch (opcion) {
                case 1:
                    String correoInicio = null;
                    String passwordInicio = null;
                    int contadorDeVecesCorreo = 0;
                    int contadorDeVecesPass = 3;

                    do {
                        System.out.println("Introduce tu correo electronico");
                        correoInicio = sc.next();
                        clienteRepository.correoExisteDB(correoInicio);
                        if (!clienteRepository.correoExisteDB(correoInicio)) {
                            System.out.println("El correo introducido contiene errores, o no está registrado");
                            contadorDeVecesCorreo++;
                            if (contadorDeVecesCorreo > 3) {
                                System.out.println("Ya has intentado 3 veces el correo, y no existe o esta mal escrito, por favor REGISTRESE:");
                                clienteRepository.registrarClienteNuevo();
                            }
                        } else {
                            System.out.println("El correo es correcto");
                            break;
                        }

                    } while (contadorDeVecesCorreo < 4 || clienteRepository.correoExisteDB(correoInicio));


                    if (clienteRepository.correoExisteDB(correoInicio)) {

                        do {


                            System.out.println("Introduce tu password");
                            passwordInicio = sc.next();
                            if (clienteRepository.verificarPasswordParaInicio(correoInicio, passwordInicio)) {
                                clienteActual=correoInicio;
                                System.out.println("🏪ESTAS DENTRO DEL MENÚ, AHORA CREA EL MENÚ DE LA TIENDA Y LA TIENDA HUEVON🏪");
                                // TODO: 17/06/2024 AQUI LLEVAR AL MENÚ DE TIENDA...comprar y demas
                                break;
                            } else {
                                contadorDeVecesPass--;
                                System.err.println("El password no coincide con la base de datos");
                                System.out.println("Te quedan " + contadorDeVecesPass + " intentos");
                            }
                        } while (contadorDeVecesPass > 0 || !clienteRepository.verificarPasswordParaInicio(correoInicio, passwordInicio));


                    }
                    break;
                case 2:

                    clienteRepository.registrarClienteNuevo();
                    break;

                case 3:

                    productsRepository.mostrarProductosTienda();

                    break;
                case 0:
                    System.out.println("Saliendo de la APP");
                    break;

            }

        } while (opcion != 0);
    }


    public void menuAdmin(){
        int opcion = -1;


        System.out.println("""
                MENU DE ADMINISTRADOR
                1-AGREGAR PRODUCTO
                2-MODIFICAR PRODUCTO
                3-ELIMINAR PRODUCTO
                """);

        switch (opcion){
            case 1:
                productsRepository.agregarNuevoProductoADatabase();
                break;
            case 2:

                break;
            case 3:

                break;
            case 0:

                break;
            default:
                System.out.println("opcion no contemplada");
        }
    }
}
