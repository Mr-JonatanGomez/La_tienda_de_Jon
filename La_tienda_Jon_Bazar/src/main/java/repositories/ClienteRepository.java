package repositories;

import database.DBConnection;
import database.EsquemaDB;
import exceptions.DigitInNameException;
import exceptions.TipoCorreoIncorrecto;
import exceptions.TipoPasswordIcorrecto;
import menu.Menu_Inicio_App;
import org.mindrot.jbcrypt.BCrypt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ClienteRepository {
    Scanner sc = new Scanner(System.in);

    //necesitamos la conexion, asique se crea como private, se abre, trabaja y se cierra
    private Connection connection;

    //METODO PARA CIFRADO CON BCRYPT
    private String hashPassword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    // Método para verificar la contraseña usando BCrypt
    private boolean checkPassword(String normalPassword, String passwordCifrada) {
        return BCrypt.checkpw(normalPassword, passwordCifrada);
    }


    //aqui meter los CRUD de cliente
    public void registrarClienteNuevo() {
        //abrimos conex y creamos Statment a null
        connection = DBConnection.getConnection();
        Statement statement = null;

        //we working with this, whit connection.createStatment
        // tenemos el :
        // execute (devuelve boolean si hay o no cambios/fallos)
        // executeUpdate (devuelve el numero de filas afectadas)

        try {

            String nombre = null;
            String correo = null;
            String password = null;


            //ESTABLECER NOMBRE
            do {
                try {


                    System.out.println("Introduce el nombre");
                    nombre = sc.next();
                    for (int i = 0; i < nombre.length(); i++) {
                        if (Character.isDigit(nombre.charAt(i))) {
                            nombre = null;
                            throw new DigitInNameException("El nombre no debe contener digitos");
                        }
                    }
                } catch (DigitInNameException e) {
                    System.out.println(e.getMessage());
                }
            } while (nombre == null);
            //ESTABLECER CORREO
            do {
                try {


                    System.out.println("Introduce el correo");
                    correo = sc.next();
                    if (correoExisteDB(correo)){
                        correo=null;
                        System.err.println("El correo introducido ya existe en la database");

                    }

                    if (!correo.contains("@") && (!correo.contains(".es") || !correo.contains(".com"))) {
                        correo = null;
                        throw new TipoCorreoIncorrecto("El nombre  debe contener @ y .com o .es");
                    }

                } catch (TipoCorreoIncorrecto e) {
                    System.out.println(e.getMessage());
                } catch (NullPointerException e){

                }
            } while (correo == null);


            //ESTABLECER PASSWORD
            do {
                try {


                    System.out.println("Introduce el password, con letras y numeros, al menos 8 caracteres");
                    password = sc.next();
                    int nLetras = 0;
                    int nNumeros = 0;

                    for (int i = 0; i < password.length(); i++) {
                        if (Character.isDigit(password.charAt(i))) {
                            nNumeros++;
                        }else if(Character.isLetter(password.charAt(i))){
                            nLetras++;
                        }
                    }

                    if (nLetras < 1 || nNumeros < 1 || password.length() < 8) {
                        password = null;
                        throw new TipoPasswordIcorrecto("El password no cumple requisitos");
                    } else{
                        password=hashPassword(password);
                    }

                    //cifradoPassword(passwordSinCifrar);
                    /*
                        CIFRADO CHANO
                           for (int i = 0; i < passwordSinCifrar.length(); i++) {
                           password += passwordSinCifrar.charAt(i)*claveCifrado;
                        }
                    */


                } catch (TipoPasswordIcorrecto e) {
                    System.err.println(e.getMessage());
                }
            } while (password == null);


            if (!correoExisteDB(correo)) {// comprobamos si NO existe correo, ejecutamos query
                statement = connection.createStatement();

                String query = String.format("INSERT INTO %s " +
                                "(%s,%s,%s) " +
                                "VALUES ('%s','%s','%s');",
                        EsquemaDB.TAB_CLIENTES,
                        EsquemaDB.COL_NOMBRE, EsquemaDB.COL_CORREO, EsquemaDB.COL_PASSWORD,
                        nombre, correo, password);
                //IMPORTANTE meterle comillas a las banderas

                // para sacar filas afectadas se crea la variable, la QUERY se ejecuta al mismo tiempo que se guarda
                int filasAfectadas = statement.executeUpdate(query);
                //statement.executeUpdate(query);
                statement.close();
                if (filasAfectadas > 0) {
                    System.out.println("Cliente registrado con exito");
                } else {
                    System.err.println("Cliente no se ha registrado");
                }
            }
/*
            String query = "INSERT INTO clientes (nombre, correo, password) VALUES ('" + nombre + "', '" + correo + "' ,'" + password + "');";
            statement.executeUpdate(query);
            statement.close();
*/


        } catch (SQLException e) {
            System.err.println("Fallo en la sentencia SQL");
            System.out.println(e.getMessage());
        } finally {
            //cerramos
            DBConnection.closeConnection();
        }


    }
    public boolean correoExisteDB(String correo) {
        boolean existeCorreo = false;

        connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        String query = String.format("SELECT %s FROM %s", EsquemaDB.COL_CORREO, EsquemaDB.TAB_CLIENTES);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                if (resultSet.getString("correo").equalsIgnoreCase(correo)) {
                    existeCorreo = true;

                    break;
                }
            }

        } catch (SQLException e) {
            System.err.println("error SQL Cliente");
            System.out.println(e.getMessage());
        }


        return existeCorreo;

    }
    public boolean verificarPasswordParaInicio(String correo, String plainPassword) {
        boolean acceso = false;


        //descifrar el password cogiendolo de la base de datos,

        connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        String query = String.format("SELECT %s FROM %s WHERE %s = '%s'", EsquemaDB.COL_PASSWORD, EsquemaDB.TAB_CLIENTES, EsquemaDB.COL_CORREO, correo);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if (resultSet.next()){
                String passwordCifrada = resultSet.getString(EsquemaDB.COL_PASSWORD);
                return checkPassword(plainPassword, passwordCifrada);
                /*
                YA COMPARA EL RESULTADO Y DEVUELVE TRUE SI COINCIDE

                System.out.println("passNormal"+plainPassword);
                System.out.println("Cifrada"+passwordCifrada);

                 */
            }

        } catch (SQLException e) {
            System.err.println("error al verificar password");
        }finally {
            DBConnection.closeConnection();
        }

        return false;
    }


    // TODO: 30/08/2024 intentar meter aqui el inicio, el establecimiento de id etc. 
    /*

    public boolean inicioSesion() {

        String correoInicio = null;
        String passwordInicio = null;
        int contadorDeVecesCorreo = 0;
        int contadorDeVecesPass = 3;
        String clienteActual = null;

        do {
            System.out.println("Introduce tu correo electronico");
            correoInicio = sc.next();
            //clienteRepository.correoExisteDB(correoInicio);
            if (!correoExisteDB(correoInicio)) {
                System.out.println("El correo introducido contiene errores, o no está registrado");
                contadorDeVecesCorreo++;
                if (contadorDeVecesCorreo == 3) {
                    System.out.println("Ya has intentado 3 veces el correo, y no existe o esta mal escrito, por favor REGISTRESE:");
                    registrarClienteNuevo();
                    return false;
                }
            } else {
                System.out.println("El correo es correcto");
                break;
            }

        } while (contadorDeVecesCorreo < 3);


        if (correoExisteDB(correoInicio)) {

            do {

                System.out.println("Introduce tu password");
                passwordInicio = sc.next();
                if (verificarPasswordParaInicio(correoInicio, passwordInicio)) {
                    clienteActual = correoInicio;
                    System.out.println("🏪Correo y contraseña correctos, Inicio de Sesión Exitoso para 🏪"+clienteActual);

                    return true;
                } else {
                    contadorDeVecesPass--;
                    System.err.println("El password no coincide con la base de datos");
                    System.out.println("Te quedan " + contadorDeVecesPass + " intentos");
                }
            } while (contadorDeVecesPass > 0 || !verificarPasswordParaInicio(correoInicio, passwordInicio));


        }
        return false;
    }*/

    public int idClienteActual(String clienteActual){
        int idClienteActual=0;
        connection=DBConnection.getConnection();

        Statement statement = null;
        ResultSet resultSet = null;
        String query =String.format("SELECT %s FROM %s WHERE %s = %s;",
                EsquemaDB.COL_ID_CLIENTE, EsquemaDB.TAB_CLIENTES, EsquemaDB.COL_CORREO, clienteActual) ;

        DBConnection.closeConnection();
        connection=null;

        return idClienteActual;

    }




}
