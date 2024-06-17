package repositories;

import database.DBConnection;
import database.EsquemaDB;
import exceptions.DigitInNameException;
import exceptions.TipoCorreoIncorrecto;
import exceptions.TipoPasswordIcorrecto;
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
    //necesitamos la conexion, asique se crea como private, se abre, trabaja y se cierra
    private Connection connection;
    Scanner sc = new Scanner(System.in);

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
            System.err.println("error SQL");
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

    public void leerUseryDescifrarPass(){//NO FUNCIONA DEJAR AL FINAL EL PASS
        connection=DBConnection.getConnection();
String query = String.format("SELECT %s,%s FROM %s",EsquemaDB.COL_CORREO,EsquemaDB.COL_PASSWORD,
        EsquemaDB.TAB_CLIENTES);
        try {
            Statement statement= connection.createStatement();
            ResultSet resultSet= statement.executeQuery(query);

            while(resultSet.next()){
                String correoLeido = resultSet.getString(EsquemaDB.COL_CORREO);
                String passLeida = resultSet.getString(EsquemaDB.COL_CORREO);

                System.out.println("correo: "+correoLeido);
                System.out.println("passCod: "+passLeida);
                String passNormal=null;
                for (int i = 0; i < passLeida.length(); i++) {
                    passNormal+= passLeida.charAt(i)/87;
                }
                System.out.println("La pas normal es: "+passNormal);
            }

        } catch (SQLException e) {
            System.err.println("Error LECTURA SQL");
        }
    }
    public void cifradoPassword(String password){
        /*multiplicando por 87
        String cifrado= "";
        for (int i = 0; i < password.length(); i++) {
            cifrado += i * 87;
        }
        System.out.println("password "+password);
        System.out.println("cifrado "+cifrado);
        */
        File file = new File("src/main/resources/cifrado.txt");
        FileWriter fileWriter=null;

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Fallo al crear el file");
            }
        } else {
            try {
                file.delete();
                file.createNewFile();
            } catch (IOException e) {
                System.err.println("Fallo al crear el file");
            }
        }


        try {
            fileWriter= new FileWriter(file);
            for (int i = 0; i < password.length(); i++) {
                fileWriter.write(password.charAt(i)*2);
            }

        } catch (IOException e) {
            System.err.println("Error al escribir cifrado");
        } finally {
            try {
                assert fileWriter != null;
                fileWriter.close();
            } catch (IOException e) {
                System.err.println("Error de cerrado");
            }
        }


    }
    public void descifradoPassword(String password){
//PRIMERO HACER EL READ
        /*File file = new File("src/main/resources/descifrado.txt");
        FileReader fileReader=null;
*/





    }
}
