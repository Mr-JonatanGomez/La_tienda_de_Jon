package menu;

import repositories.ClienteRepository;

import java.util.Scanner;

public class Menu_Inicio_App {
    ClienteRepository clienteRepository = new ClienteRepository();
    Scanner sc = new Scanner(System.in);

    public void menuInicial() {


        int opcion = -1;
        //PARTE INICIO SESION NO ACTIVA HASTA QUE HAGA EL RESSULT SET
        do {
            System.out.println("""
                    MENU DE INICIO
                                    
                    1- INICIAR SESIÓN
                    2- REGISTRARSE
                                    
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
                case 0:
                    System.out.println("Saliendo de la APP");
                    break;

            }

        } while (opcion != 0);
    }
}
