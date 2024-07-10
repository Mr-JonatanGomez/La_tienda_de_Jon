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

@Getter
@Setter
@NoArgsConstructor
public class ExtraccionProductosJSON {
    /*
    1- necesitamos URL
    2- un HTTPConnection
    3- Necesitamos leer la contestacion URL en TXT
    4- Pasar TXT a JSON

    */
    private URL url;
    private Connection connection;

    private ArrayList<Producto> listadoProductos;


    public ExtraccionProductosJSON(URL url, ArrayList<Producto> listadoProductos) {
        this.url = url;
        this.listadoProductos = new ArrayList<>();
    }

    public void crearProductsYLeerlosENArrayDeJava() {
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

    public void crearProductsYLeerlosENArrayDeJava2() {

        if (!comprobarSiHayProductosEnDatabase()) {

            connection = DBConnection.getConnection();
            Statement statement = null;

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

    }


    public boolean comprobarSiHayProductosEnDatabase() {
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


        //primero se comprueba si hay productos

        if (!comprobarSiHayProductosEnDatabase()) {
            crearProductsYLeerlosENArrayDeJava();
            connection = DBConnection.getConnection();
            PreparedStatement preparedStatement = null;

            String query = "INSERT INTO " + EsquemaDB.TAB_PRODUCTOS + " (" +
                    EsquemaDB.COL_NOMBRE + "," +
                    EsquemaDB.COL_CATEGORIA + "," +
                    EsquemaDB.COL_PRICE + "," +
                    EsquemaDB.COL_DESCRIPCION + ") VALUES (?, ?, ?, ?)";


            try {

                preparedStatement = connection.prepareStatement(query);

                for (Producto item : listadoProductos) {
                    if (item != null) {
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

    public void agregarProductosEnDatabaseSinComprobarSiYaHayProductosORIGIN() {


        //primero se comprueba si hay productos


        crearProductsYLeerlosENArrayDeJava();
        connection = DBConnection.getConnection();
        Statement statement = null;

        try {


            for (Producto item : listadoProductos) {
                if (item != null) {


                    //
                    try {
                        statement = connection.createStatement();
                        String query = String.format("INSERT INTO %s (%s,%s,%s,%s) VALUES ('%s','%s',%s,'%s')",
                                EsquemaDB.TAB_PRODUCTOS,
                                EsquemaDB.COL_NOMBRE, EsquemaDB.COL_CATEGORIA, EsquemaDB.COL_PRICE, EsquemaDB.COL_DESCRIPCION,
                                item.getNombre(), item.getCategoria(), item.getPrecio(), item.getDescripcion());


                        statement.executeUpdate(query);

                        System.out.println(item.getNombre());

                        //
                    } catch (SQLException e) {
                        System.err.println("Fallo en la sentencia SQL en JSON");
                        System.out.println(e.getMessage());
                    } finally {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            System.err.println("Error de cerrado de StatmentJSON");
                        }
                    }
                }
            }
        } finally {
            try {
                statement.close();
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error de cerrado de StatmentJSON");
            }
        }

    }

    // RESTO CODIGO
}





