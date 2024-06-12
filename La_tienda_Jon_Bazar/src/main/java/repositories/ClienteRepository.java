package repositories;

import database.DBConnection;
import database.EsquemaDB;
import exceptions.DigitInNameException;
import exceptions.TipoCorreoIncorrecto;
import exceptions.TipoPasswordIcorrecto;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ClienteRepository {
    //necesitamos la conexion, asique se crea como private, se abre, trabaja y se cierra
    private Connection connection;
    Scanner sc = new Scanner(System.in);

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

                    if (!correo.contains("@") && (!correo.contains(".es") || !correo.contains(".com"))) {
                        correo = null;
                        throw new TipoCorreoIncorrecto("El nombre  debe contener @ y .com o .es");
                    }

                } catch (TipoCorreoIncorrecto e) {
                    System.out.println(e.getMessage());
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
                        }
                    }
                    for (int i = 0; i < password.length(); i++) {
                        if (Character.isLetter(password.charAt(i))) {
                            nLetras++;
                        }
                    }
                    if (nLetras < 1 || nNumeros < 1 || password.length() < 8) {
                        password = null;
                        throw new TipoPasswordIcorrecto("El password no cumple requisitos");
                    }

                } catch (TipoPasswordIcorrecto e) {
                    System.err.println(e.getMessage());
                }
            } while (password == null);


            if (!usuarioExiste(correo)) {// comprobamos si NO existe correo, ejecutamos query
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

    public boolean usuarioExiste(String correo) {
        boolean existe = false;

        connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        String query = String.format("SELECT %s FROM %s", EsquemaDB.COL_CORREO, EsquemaDB.TAB_CLIENTES);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                if (resultSet.getString("correo").equalsIgnoreCase(correo)) {
                    existe = true;
                    System.err.println("El correo, ya existe en la database");
                    break;
                }
            }

        } catch (SQLException e) {
            System.err.println("error SQL");
        }


        return existe;
    }

    public void iniciarSesion() {
    }

}
