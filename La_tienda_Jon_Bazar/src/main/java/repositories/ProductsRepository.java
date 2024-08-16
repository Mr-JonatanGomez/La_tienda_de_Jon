package repositories;

import database.DBConnection;
import database.EsquemaDB;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import model.Producto;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Locale;
import java.util.Scanner;

@Getter
@Setter
@NoArgsConstructor
public class ProductsRepository {
    /*
    1- necesitamos URL
    2- un HTTPConnection
    3- Necesitamos leer la contestacion URL en TXT
    4- Pasar TXT a JSON

    */
    private URL url;
    private Connection connection;

    private ArrayList<Producto> listadoProductos;

    //posible para eliminar
    /*
    public ExtraccionProductosJSON(URL url, ArrayList<Producto> listadoProductos) {
        this.url = url;
        this.listadoProductos = new ArrayList<>();
    }
    */

    public void crearProductsYLeerlosENArrayDeJava() {
        /*
        This method copy the products from DummyJson to
        ArrayList with the necesary atributes for my program,
        if dummyJson disapear, i'll have save info in ArrayList
        */
        BufferedReader bufferedReader = null;
        listadoProductos = new ArrayList<>();
        try {

            //1 URL
            url = new URL("https://dummyjson.com/products");
            //2 HTTPConnection casteado (HttpURLConnection)
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            //3Leemos la contestacion desde fuera
            bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String productosJSON = bufferedReader.readLine();

            //4 PAsar a JSON
            JSONObject respuestaJSONProducts = new JSONObject(productosJSON);
            JSONArray productos = respuestaJSONProducts.getJSONArray("products");

            //5 Recorremos array para sacar la info y crear los productos

            for (int i = 0; i < productos.length(); i++) {
                JSONObject producto = productos.getJSONObject(i);//indice i
                int id = producto.getInt("id");
                String nombre = producto.getString("title");
                String categoria = producto.getString("category");
                double precio = producto.getDouble("price");
                String descripcion = producto.getString("description");


                Producto productoParaCrear = new Producto(id, nombre, categoria, precio, descripcion);
                listadoProductos.add(productoParaCrear);
            }


//MOSTRANDO DATOS
            for (Producto item : listadoProductos) {
                item.mostrarDatos();
                System.out.println();
            }

        } catch (MalformedURLException e) {
            System.err.println("Error en la codificacion de la URL");
        } catch (IOException e) {
            System.err.println("Error de conexion Internet");
        } finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.err.println("Error cerrado de bufferedReader");
            }
        }
    }


    public boolean comprobarSiHayProductosEnDatabase() {
        /*
        This method verify the products on database, if exists products on database,
        in afirmative case to be diferent to null, return resultado
        */
        boolean resultado = false;

        connection = DBConnection.getConnection();
        ResultSet resultSet = null;
        Statement statement = null;

        String query = String.format("SELECT %s, COUNT(%s) AS totalProductos FROM %s", EsquemaDB.COL_NOMBRE, EsquemaDB.COL_ID_PRODUCTO, EsquemaDB.TAB_PRODUCTOS);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);


/*
            if (resultSet.next()) {
                numeroProductos = resultSet.getInt("totalProductos");
                if (numeroProductos > 0) {
                    resultado = true;
                }
            }
*/
            if (resultSet.next()) {
                // si hay proximo, resultado = numProductos mayor que 0
                int numeroProductos = resultSet.getInt("totalProductos");
                resultado = numeroProductos > 0;
                //System.out.println(resultSet.getInt("totalProductos"));
            }

        } catch (SQLException e) {
            System.out.println("Error SQL: ");
            System.out.println(e.getMessage());
        } finally {
            try {
                assert statement != null;
                statement.close();
                assert resultSet != null;
                resultSet.close();

            } catch (SQLException e) {
                System.err.println("Error de cerrado statment o resulset");
            }
            DBConnection.closeConnection();
        }


        return resultado;
    }

    public void agregarProductosEnDatabase() {


        //primero se comprueba si hay productos en Database con el metodo anterior

        if (!comprobarSiHayProductosEnDatabase()) {
            crearProductsYLeerlosENArrayDeJava();
            connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = null;

            String query = "INSERT INTO " + EsquemaDB.TAB_PRODUCTOS + " (" +
                    EsquemaDB.COL_NOMBRE + "," +
                    EsquemaDB.COL_CATEGORIA + "," +
                    EsquemaDB.COL_PRECIO + "," +
                    EsquemaDB.COL_DESCRIPCION + ") VALUES (?, ?, ?, ?)";


            try {

                preparedStatement = connection.prepareStatement(query);

                for (Producto item : listadoProductos) {
                    if (item != null) {
                        // se podria hacer con sacando el valor de la clave en json, quizas
                        preparedStatement.setString(1, item.getNombre());
                        preparedStatement.setString(2, item.getCategoria());
                        preparedStatement.setDouble(3, item.getPrecio());
                        preparedStatement.setString(4, item.getDescripcion());

                        preparedStatement.executeUpdate();//confirmacion
                    }
                }
            } catch (SQLException e) {
                System.err.println("Fallo en la sentencia SQL en JSON");
                System.out.println(e.getMessage());
            } finally {
                try {
                    preparedStatement.close();
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error de cerrado de StatmentJSON o Connect");
                }
            }

        }


    }

    public void mostrarProductosTienda() {
        connection = DBConnection.getConnection();

        Statement statement = null;
        ResultSet resultSet = null;

        String query = String.format("SELECT %s, %s, %s,%s FROM %s",
                EsquemaDB.COL_ID_PRODUCTO, EsquemaDB.COL_NOMBRE, EsquemaDB.COL_PRECIO, EsquemaDB.COL_DESCRIPCION, EsquemaDB.TAB_PRODUCTOS);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            System.out.println("\nLISTADO DE PRODUCTOS DEL JON BAZAR 🏢\n");

            while (resultSet.next()) {
                int id = resultSet.getInt("id_producto");
                String nombre = resultSet.getString("nombre");
                double precio = resultSet.getDouble("precio");
                String descripcion = resultSet.getString("descripcion");

                Producto producto = new Producto(id, nombre, precio, descripcion);
                producto.mostrarDatos2();
                System.out.println();

            }
        } catch (SQLException e) {
            System.err.println("Error SQL al leer productos");
            System.out.println(e.getMessage());
        } finally {
            DBConnection.closeConnection();
            connection = null;
        }


    }

    public void agregarNuevoProductoADatabase() {

        System.out.println("Agregando nuevo producto a DataBase");

        connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = null;

        String query = "INSERT INTO " + EsquemaDB.TAB_PRODUCTOS + " (" +
                EsquemaDB.COL_NOMBRE + "," +
                EsquemaDB.COL_CATEGORIA + "," +
                EsquemaDB.COL_PRECIO + "," +
                EsquemaDB.COL_DESCRIPCION + ") VALUES (?, ?, ?, ?)";


        try {

            preparedStatement = connection.prepareStatement(query);

            Scanner sc = new Scanner(System.in);
            //esto es para que acepte el punto como decimal, ya que la coma no vale si trabajas con jdbc
            sc.useLocale(Locale.US);

            System.out.println("Introduce nombre del producto");
            String nombre = sc.nextLine();

            System.out.println("Introduce categoria del producto");
            String categoria = sc.nextLine();

            System.out.println("Introduce precio del producto (los decimales con punto/coma, comprobar");
            double precio = sc.nextDouble();
            sc.nextLine();
            System.out.println("Introduce breve descripcion del producto");
            String descripcion = sc.nextLine();






            // se podria hacer con sacando el valor de la clave en json, quizas
            preparedStatement.setString(1, nombre);
            preparedStatement.setString(2, categoria);
            preparedStatement.setDouble(3, precio);
            preparedStatement.setString(4, descripcion);

            preparedStatement.executeUpdate();//confirmacion


        } catch (InputMismatchException e) {
            System.err.println("al introduccir datos, de un tipo incorrecto");
            System.out.println(e.getMessage());
        }catch (SQLException e) {
            System.err.println("Fallo en la sentencia SQL en JSON");
            System.out.println(e.getMessage());
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error de cerrado de StatmentJSON o Connect");
            }
        }


    }

    public boolean verificarSiUnIDExisteDatabase(int idProducto) {
        connection = DBConnection.getConnection();
        try {
            String queryBusq = String.format("SELECT %s,%s, %s, %s FROM %s WHERE %s =" + idProducto + ";",
                    EsquemaDB.COL_NOMBRE, EsquemaDB.COL_CATEGORIA, EsquemaDB.COL_PRECIO, EsquemaDB.COL_DESCRIPCION,
                    EsquemaDB.TAB_PRODUCTOS,
                    EsquemaDB.COL_ID_PRODUCTO);

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(queryBusq);
            if (resultSet.next()) {
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error al crear Conexion SQL verificando si el producto EXISTE");
        } finally {
            DBConnection.closeConnection();
            connection = null;
        }

        return false;
    }

    public void modificarProductoDatabase() {//quizas quitarle el producto aqui
        Scanner sc = new Scanner(System.in);
        connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;

        System.out.println("💱MODIFICANDO PRODUCTO 💱");
        int idProducto = 0;

        //TERMINAR LA QUERY para el UPDATE
        // TODO: 16/08/2024

        String queryResultSet = String.format("SELECT %s,%s, %s, %s FROM %s WHERE %s =" + idProducto + ";",
                EsquemaDB.COL_NOMBRE, EsquemaDB.COL_CATEGORIA, EsquemaDB.COL_PRECIO, EsquemaDB.COL_DESCRIPCION,
                EsquemaDB.TAB_PRODUCTOS,
                EsquemaDB.COL_ID_PRODUCTO);

        try {
            System.out.println("Introduce el id del producto a modificar");
            idProducto = sc.nextInt();
            // String queryBusqueda por si no funciona
            statement = connection.createStatement();
            resultSet = statement.executeQuery(queryResultSet);

            //if exist, mostrar producto, acto seguido, preguntamos que nombre, precio, descripcion darle
            //podria dar fallo al crear conexion dentro de conexion??
            if (verificarSiUnIDExisteDatabase(idProducto)) {
                String nombre = resultSet.getString("nombre");
                String categoria = resultSet.getString("categoria");
                double precio = resultSet.getDouble("precio");
                String descripcion = resultSet.getString("descripcion");

                System.out.println(
                        "PRODUCTO QUE VAS A MODIFICAR ES: " +
                                "\nNOMBRE: " + nombre + "" +
                                "\nCATEGORIA: " + categoria + "" +
                                "\nPRECIO: " + precio + "" +
                                "\nDESCRIPCION: " + descripcion);


                System.out.println("Introduce el nuevo nombre");
                String nombreN = sc.next();
                System.out.println("Introduce la nueva categoria");
                String categoriaN = sc.next();
                System.out.println("Introduce el nuevo precio");
                double precioN = sc.nextDouble();
                System.out.println("Introduce la nueva descripcion");
                String descripcionN = sc.nextLine();

                String queryMod = String.format("UPDATE %s " +
                                "SET %s = '%s' " +
                                "%s = '%s'," +
                                "%s = %s " +
                                "%s = '%s'," +
                                "WHERE %s = %s ",
                        EsquemaDB.TAB_PRODUCTOS,
                        EsquemaDB.COL_NOMBRE, nombreN,
                        EsquemaDB.COL_CATEGORIA, categoriaN,
                        EsquemaDB.COL_PRECIO, precioN,
                        EsquemaDB.COL_DESCRIPCION, descripcionN);

                int numero = statement.executeUpdate(queryMod);

                if (numero>0){
                    System.out.println("El numero de productos modificados por el update fue: "+numero+"");
                    System.out.println("Mensaje anterior SE PUEDE ELIMINAR");
                    System.out.println(" Los datos fuero cambiado con exito!");
                }
                statement.close();



            } else {
                System.out.println("El id de producto no existe en la DATABASE");
            }


        } catch (SQLException e) {
            System.out.println("Error SQL al modificar producto "+e.getMessage());
        }


    }

    public void modificarProductoDatabase2() {//quizas quitarle el producto aqui
        System.out.println("💱MODIFICANDO PRODUCTO 💱");

        int idProducto;
        System.out.println("Introduce el id del producto a modificar");
        Scanner sc = new Scanner(System.in);
        idProducto = sc.nextInt();

        if(verificarSiUnIDExisteDatabase(idProducto)){
            connection = DBConnection.getConnection();
            Statement statement = null;
            ResultSet resultSet = null;


            String queryResultSet = String.format("SELECT %s,%s, %s, %s FROM %s WHERE %s =" + idProducto + ";",
                    EsquemaDB.COL_NOMBRE, EsquemaDB.COL_CATEGORIA, EsquemaDB.COL_PRECIO, EsquemaDB.COL_DESCRIPCION,
                    EsquemaDB.TAB_PRODUCTOS,
                    EsquemaDB.COL_ID_PRODUCTO);

            try {

                statement = connection.createStatement();
                resultSet = statement.executeQuery(queryResultSet);


                    String nombre = resultSet.getString("nombre");
                    String categoria = resultSet.getString("categoria");
                    double precio = resultSet.getDouble("precio");
                    String descripcion = resultSet.getString("descripcion");

                    System.out.println(
                            "PRODUCTO QUE VAS A MODIFICAR ES: " +
                                    "\nNOMBRE: " + nombre + "" +
                                    "\nCATEGORIA: " + categoria + "" +
                                    "\nPRECIO: " + precio + "" +
                                    "\nDESCRIPCION: " + descripcion);


                    System.out.println("Introduce el nuevo nombre");
                    String nombreN = sc.next();
                    System.out.println("Introduce la nueva categoria");
                    String categoriaN = sc.next();
                    System.out.println("Introduce el nuevo precio");
                    double precioN = sc.nextDouble();
                    sc.nextLine();
                    System.out.println("Introduce la nueva descripcion");
                    String descripcionN = sc.nextLine();

                    String queryMod = String.format("UPDATE %s " +
                                    "SET %s = '%s' " +
                                    "%s = '%s'," +
                                    "%s = %s " +
                                    "%s = '%s'," +
                                    "WHERE %s = %s ",
                            EsquemaDB.TAB_PRODUCTOS,
                            EsquemaDB.COL_NOMBRE, nombreN,
                            EsquemaDB.COL_CATEGORIA, categoriaN,
                            EsquemaDB.COL_PRECIO, precioN,
                            EsquemaDB.COL_DESCRIPCION, descripcionN);

                    int numero = statement.executeUpdate(queryMod);

                    if (numero>0){
                        System.out.println("El numero de productos modificados por el update fue: "+numero+"");
                        System.out.println("Mensaje anterior SE PUEDE ELIMINAR");
                        System.out.println(" Los datos fuero cambiado con exito!");
                    }
                    statement.close();






            } catch (SQLException e) {
                System.out.println("Error SQL al modificar producto "+e.getMessage());
            }
        } else {

        }







    }

    // RESTO CODIGO
}





