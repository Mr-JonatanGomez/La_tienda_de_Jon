package menu;

import repositories.ClienteRepository;

import java.util.Scanner;

public class Menu_Inicio_App {
    public void menuInicial(){
        Scanner sc = new Scanner(System.in);
        int opcion=-1;
        //PARTE INICIO SESION NO ACTIVA HASTA QUE HAGA EL RESSULT SET
        do {
        System.out.println("""
                MENU DE INICIO
                
                1- INICIAR SESIÓN
                2- REGISTRARSE
                
                0- SALIR
                """);
        opcion=sc.nextInt();

        switch (opcion){
            case 1:
                //INHABILITADA TODAVIA
                break;
            case 2:
                ClienteRepository clienteRepository = new ClienteRepository();
                clienteRepository.registrarClienteNuevo();
                break;
            case 0:
                System.out.println("Saliendo de la APP");
                break;

        }

        }while(opcion != 0);
    }
}
