package menu;

import database.DBConnection;
import database.EsquemaDB;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import repositories.PedidoRepository;
import repositories.ProductsRepository;
import repositories.ClienteRepository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

@Getter
@Setter
@NoArgsConstructor
public class Menu_Inicio_App {
    private Connection connection;

    ClienteRepository clienteRepository = new ClienteRepository();
    ProductsRepository productsRepository = new ProductsRepository();
    PedidoRepository pedidoRepository = new PedidoRepository();

    Scanner sc = new Scanner(System.in);
    String clienteActual = null;
    int idClienteActual = 0;
    boolean inicioCorrecto = false;


    public boolean inicioSesion() {

        String correoInicio = null;
        String passwordInicio = null;
        int contadorDeVecesCorreo = 0;
        int contadorDeVecesPass = 3;

        do {
            System.out.println("Introduce tu correo electronico");
            correoInicio = sc.next();
            //clienteRepository.correoExisteDB(correoInicio);
            if (!clienteRepository.correoExisteDB(correoInicio)) {
                System.out.println("El correo introducido contiene errores, o no está registrado");
                contadorDeVecesCorreo++;
                if (contadorDeVecesCorreo == 3) {
                    System.out.println("Ya has intentado 3 veces el correo, y no existe o esta mal escrito, por favor REGISTRESE:");
                    clienteRepository.registrarClienteNuevo();
                    return false;
                }
            } else {
                System.out.println("El correo es correcto");
                break;
            }

        } while (contadorDeVecesCorreo < 3);


        if (clienteRepository.correoExisteDB(correoInicio)) {

            do {

                System.out.println("Introduce tu password");
                passwordInicio = sc.next();
                if (clienteRepository.verificarPasswordParaInicio(correoInicio, passwordInicio)) {
                    clienteActual = correoInicio;
                    System.out.println("🏪Correo y contraseña correctos, Inicio de Sesión Exitoso🏪");
                    inicioCorrecto = true;
                    establecerIdActual();

                    return true;
                } else {
                    contadorDeVecesPass--;
                    System.err.println("El password no coincide con la base de datos");
                    System.out.println("Te quedan " + contadorDeVecesPass + " intentos");
                }
            } while (contadorDeVecesPass > 0 || !clienteRepository.verificarPasswordParaInicio(correoInicio, passwordInicio));


        }
        return false;

    }

    public void establecerIdActual() {
        //quizas poner este metodo con devolucion de INT
        connection = DBConnection.getConnection();
        if (inicioCorrecto) {
            Statement statement = null;
            ResultSet resultSet = null;

            String query = String.format("SELECT * FROM %s WHERE %s = '%s'",
                    EsquemaDB.TAB_CLIENTES, EsquemaDB.COL_CORREO, clienteActual);

            try {
                statement = connection.createStatement();
                resultSet = statement.executeQuery(query);

                if (resultSet.next()){
                    idClienteActual = resultSet.getInt("id_cliente");


                }else{
                    System.out.println("NO Imprimir ID"+idClienteActual);
                }


            } catch (SQLException e) {
                System.out.println("Error SQL en el Menu_Inicio_APP en metodo establece_IdCliente"+e.getMessage());
            } finally {

                DBConnection.closeConnection();
                connection = null;
            }
        }

    }

    public boolean comprobarAdmin() {
        if ("jjgomez@mail.es".equals(clienteActual)) {
            return true;
        }
        return false;
        // TODO: 14/08/2024 incorporar a menuInicial la comprobacion y derivar a un menú u otro (admin/user)
    }

    public void menuInicial() {

        /*
        Al cerrar sesion, se debe poner a null y cero el cliente actual e IdClienteActual
        */

        int opcion = -1;

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
                    // así inicia sesion y si es correcto...
                    if (inicioSesion()) {
                        if ("jjgomez@mail.es".equals(clienteActual)) {
                            menuAdmin();

                        } else {
                            menuUser();
                        }

                    }

//ahora mismo tras inicio sesion vuelve al menu por no haber nada
                    break;
                case 2:

                    clienteRepository.registrarClienteNuevo();
                    break;


                case 0:
                    System.out.println("Saliendo de la APP");
                    clienteActual = null;
                    idClienteActual = 0;
                    break;
                default:
                    System.out.println("Opcion no contemplada");
                    break;

            }

        } while (opcion != 0);
    }


    public void menuAdmin() {
        int opcion = -1;
        Scanner sc = new Scanner(System.in);

        do {


            System.out.println("""
                    MENU DE ADMINISTRADOR
                    1-AGREGAR PRODUCTO
                    2-MODIFICAR PRODUCTO
                    3-SUMAR STOCK
                    4-RESTAR STOCK
                    5-ELIMINAR PRODUCTO
                    0-CERRAR SESION
                    """);
            opcion = sc.nextInt();
            switch (opcion) {
                case 1:
                    productsRepository.agregarNuevoProductoADatabase();
                    break;
                case 2:
                    productsRepository.modificarProductoDatabase();
                    break;
                case 3:
                    productsRepository.addStockage();
                    break;
                case 4:
                    productsRepository.restarStockage_addCarritoCliente();
                    break;
                case 5:
                    productsRepository.deleteProductDatabase();
                    break;
                case 0:
                    System.out.println("CERRANDO SESION");
                    clienteActual = null;
                    idClienteActual = 0;

                    break;
                default:
                    System.out.println("opcion no contemplada");
            }
        } while (opcion != 0);
    }

    public void menuUser() {
        int opcion = -1;
        Scanner scop = new Scanner(System.in);

        do {

            System.out.println("""
                    MENU DE COMPRA en construccion TODAVIA; NO PONERSE NERVIOSOS
                    1-VER TODOS LOS PRODUCTOS
                    2-VER TODOS LOS PRODUCTOS DE UNA CATEGORIA (beauty, fragances, furniture, groceries, sports)
                    3-AÑADIR PRODUCTOS AL CARRITO
                    4-ELIMINAR PRODUCTOS DEL CARRITO
                    5-VER CARRITO ACTUAL
                    6-REALIZAR COMPRA y por supuesto, PAGAR
                    7-HISTORIAL DE COMPRAS
                    0-GUARDAR CARRITO ACTUAL, CERRAR SESION y SALIR AL MENU INICIAL
                    """);

            opcion = scop.nextInt();
            switch (opcion) {
                case 1:
                    productsRepository.mostrarProductosTienda();
                    break;
                case 2:
                    String cat;
                    Scanner sc = new Scanner(System.in);
                    System.out.println("ESCRIBE DE QUE CATEGORIA QUIERES VER LOS PRODUCTOS?");
                    cat = sc.nextLine();

                    productsRepository.mostrarProductosTiendaXCat(cat);
                    // TODO: 23/08/2024 añadir si cat no existe...
                    break;
                case 3:
                    pedidoRepository.addProductCarrito();
                    break;
                case 0:
                    System.out.println("GURDANDO CARRITO, CERRANDO SESION Y SALIENDO AL MENÚ PRINCIPAL ");
                    clienteActual = null;
                    idClienteActual = 0;

                    break;
                default:
                    System.out.println("opcion no contemplada");
                    break;
            }
        } while (opcion != 0);
    }
}

/*
    METODOS cambiados de luegar que antes estaban aquí
    1- metido ahora en clienteRepo

*/