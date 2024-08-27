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
import java.util.*;

@Getter
@Setter
@NoArgsConstructor
public class ProductsRepository {
    /*
    1- necesitamos URL (SOLO PARA JSON, no para repository)
    2- un HTTPConnection
    3- Necesitamos leer la contestacion URL en TXT
    4- Pasar TXT a JSON

    */
    private URL url;
    private Connection connection;

    private ArrayList<Producto> listadoProductos;
    private int cantidadStockActual;


    public void crearProductosJsonArraylist() {
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

    public void llevarProductosADatabase() {
        //primero se comprueba si hay productos en Database con el metodo anterior,
        // tras esto, si no la tabla está vacia, la llena

        if (!comprobarSiHayProductosEnDatabase()) {
            crearProductosJsonArraylist();
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

        String query = String.format("SELECT %s, %s, %s, %s, %s, %s FROM %s",
                EsquemaDB.COL_ID_PRODUCTO, EsquemaDB.COL_CATEGORIA, EsquemaDB.COL_NOMBRE, EsquemaDB.COL_PRECIO, EsquemaDB.COL_DESCRIPCION, EsquemaDB.COL_STOCK, EsquemaDB.TAB_PRODUCTOS);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            System.out.println("\nLISTADO DE PRODUCTOS DEL JON BAZAR 🏢\n");

            while (resultSet.next()) {
                int id = resultSet.getInt("id_producto");
                String cat = resultSet.getString("categoria");
                String nombre = resultSet.getString("nombre");
                double precio = resultSet.getDouble("precio");
                int stock = resultSet.getInt("stock");
                String descripcion = resultSet.getString("descripcion");

                Producto producto = new Producto(id, nombre, cat, precio, stock, descripcion);
                producto.mostrarDatos3();
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
    public void mostrarProductosTiendaXCat(String categoria) {
        connection = DBConnection.getConnection();

        Statement statement = null;
        ResultSet resultSet = null;

        System.out.println("LOS PRODUCTOS DE LA CATEGORIA"+categoria+" SON:");

        String query = String.format("SELECT %s, %s, %s, %s, %s " +
                        "FROM %s " +
                        "WHERE %s = '%s';",
                EsquemaDB.COL_ID_PRODUCTO, EsquemaDB.COL_NOMBRE, EsquemaDB.COL_PRECIO, EsquemaDB.COL_DESCRIPCION, EsquemaDB.COL_STOCK,
                EsquemaDB.TAB_PRODUCTOS,
                EsquemaDB.COL_CATEGORIA,categoria);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            System.out.println("\nLISTADO DE PRODUCTOS DEL JON BAZAR 🏢\n");

            while (resultSet.next()) {
                int id = resultSet.getInt("id_producto");
                String nombre = resultSet.getString("nombre");
                double precio = resultSet.getDouble("precio");
                int stock = resultSet.getInt("stock");
                String descripcion = resultSet.getString("descripcion");

                Producto producto = new Producto(id, nombre, precio, stock, descripcion);
                producto.mostrarDatos4();
                System.out.println();

            }

            statement.close();
            resultSet.close();
            // TODO: 23/08/2024 NOSUCHELEMENTs 
        } catch (SQLException e) {
            System.err.println("Error SQL al leer productos");
            System.out.println(e.getMessage());
        }catch (InputMismatchException e) {
            System.err.println("Error en el tipo de datos Producto por categoria para menu User");
            System.out.println(e.getMessage());
        }catch (NoSuchElementException e) {
            System.err.println("Error en el tipo de datos Producto por categoria para menu User NO_SUCH");
            System.out.println(e.getMessage());
        } finally {
            DBConnection.closeConnection();
            connection = null;
        }


    }

    public void leerUnProductoDeLaDataBase(int idProducto) {
        connection = DBConnection.getConnection();

        Statement statement = null;
        ResultSet resultSet = null;

        String query = String.format("SELECT %s, %s, %s,%s FROM %s WHERE %s =" + idProducto,
                EsquemaDB.COL_ID_PRODUCTO, EsquemaDB.COL_NOMBRE, EsquemaDB.COL_PRECIO, EsquemaDB.COL_DESCRIPCION, EsquemaDB.TAB_PRODUCTOS, EsquemaDB.COL_ID_PRODUCTO);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

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
            System.err.println("Error SQL al lee producto");
            System.out.println(e.getMessage());
        } finally {
            DBConnection.closeConnection();
            connection = null;
        }
    }

    public int consultaYLecturaStockOfProduct(int idProducto) {
        connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        int stock = -1;

        String query = String.format("SELECT %s, %s, %s " +
                        "FROM %s " +
                        "WHERE %s = %s"

                , EsquemaDB.COL_ID_PRODUCTO, EsquemaDB.COL_NOMBRE, EsquemaDB.COL_STOCK,
                EsquemaDB.TAB_PRODUCTOS,
                EsquemaDB.COL_ID_PRODUCTO, idProducto);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                int id = resultSet.getInt("id_producto");
                String name = resultSet.getString("nombre");
                stock = resultSet.getInt("stock");

                System.out.println("El stockage ACTUAL de:\n" +
                        "ID: " + id + ", " + name + ", es de " + stock + " unidades en TOTAL");
            } else {
                System.out.println(" No se encontró producto con este ID");
            }


        } catch (SQLException e) {
            System.out.println("Error SQL en leer Stockage\n" + e.getMessage());
        } finally {
            DBConnection.closeConnection();
            connection = null;
        }

        return stock;
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

            preparedStatement.close();

        } catch (SQLException e) {
            System.err.println("Fallo en la sentencia SQL en JSON");
            System.out.println(e.getMessage());
        } catch (InputMismatchException e) {
            System.out.println("Error en el tipo de datos introducido\n" + e.getMessage());
        } finally {
            try {
                preparedStatement.close();
                connection.close();
            } catch (SQLException e) {
                System.err.println("Error de cerrado de StatmentJSON o Connect");
            }
        }


    }

    public void modificarProductoDatabase() {
        System.out.println("💱MODIFICANDO PRODUCTO 💱");

        int idProducto;
        Scanner sc = new Scanner(System.in);
        sc.useLocale(Locale.US);
        System.out.println("Introduce el id del producto que quieres modificar");
        idProducto = sc.nextInt();
        sc.nextLine();
        boolean existe = false;

        if (verificarSiUnIDExisteDatabase(idProducto)) {
            //verifica que el id a modificar existe, y si es así existe pasa a true
            existe = true;
        }

        if (existe) {

            //si existe, lee el producto, e
            System.out.println("EL PRODUCTO QUE VAS A MODIFICAR Y SUS DATOS ACTUALES SON:");

            leerUnProductoDeLaDataBase(idProducto);

            ///

            connection = DBConnection.getConnection();
            Statement statement = null;
            try {

                statement = connection.createStatement();


                System.out.println("Introduce el nuevo nombre");
                String nombreN = sc.nextLine();
                System.out.println("Introduce la nueva categoria");
                String categoriaN = sc.next();
                System.out.println("Introduce el nuevo precio");
                double precioN = sc.nextDouble();
                sc.nextLine();
                System.out.println("Introduce la nueva descripcion");
                String descripcionN = sc.nextLine();

                String queryMod = String.format("UPDATE %s " +
                                "SET %s = '%s', " +
                                "%s = '%s', " +
                                "%s = %s , " +
                                "%s = '%s' " +
                                "WHERE %s = %s;",
                        EsquemaDB.TAB_PRODUCTOS,
                        EsquemaDB.COL_NOMBRE, nombreN,
                        EsquemaDB.COL_CATEGORIA, categoriaN,
                        EsquemaDB.COL_PRECIO, precioN,
                        EsquemaDB.COL_DESCRIPCION, descripcionN,
                        EsquemaDB.COL_ID_PRODUCTO, idProducto);

                int numero = statement.executeUpdate(queryMod);

                if (numero > 0) {
                    //System.out.println("El numero de productos modificados por el update fue: "+numero+"");
                    System.out.println(" ✅ Los datos del producto fueron cambiados con exito! ✅");
                }
                statement.close();


            } catch (SQLException e) {
                System.out.println("Error SQL al modificar producto " + e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Error en el tipo de datos introducido\n" + e.getMessage());
            }
        } else {
            System.out.println(" El id de producto, no existe, y por tanto no lo puedes modificar");
        }


    }

    public void addStockage() {
        Scanner sc = new Scanner(System.in);
        int idProd = -1;
        boolean existe = false;
        int cantidadSumada = 0;

        System.out.println("📦Introduce el ID del producto que quieres agregar STOCK 📦");

        idProd = sc.nextInt();

        if (verificarSiUnIDExisteDatabase(idProd)) {
            existe = true;
        }
        if (existe) {
            consultaYLecturaStockOfProduct(idProd);
        } else {
            System.out.println("El ID del producto no existe");
        }

        if (existe) {

            System.out.println("Introduce la cantidad de Stockage a sumar de este producto");
            cantidadSumada = sc.nextInt();

            connection = DBConnection.getConnection();
            Statement statement = null;

            String query = String.format("UPDATE %s " +
                            "SET %s = %s + %s " +
                            "WHERE %s = %s;",
                    EsquemaDB.TAB_PRODUCTOS,
                    EsquemaDB.COL_STOCK, EsquemaDB.COL_STOCK, cantidadSumada,
                    EsquemaDB.COL_ID_PRODUCTO, idProd);


            try {
                statement = connection.createStatement();
                int numero = statement.executeUpdate(query);

                if (numero > 0) {
                    //System.out.println("El numero de productos modificados por el update fue: "+numero+"");
                    System.out.println(" ✅ Las unidades del STOCKAGE del producto fueron sumados con exito! ✅");
                }


                statement.close();

            } catch (SQLException e) {
                System.out.println("Error SQL connection al sumar Stockage\n" + e.getMessage());
            } catch (InputMismatchException e) {
                System.out.println("Error en el tipo de datos introducido\n" + e.getMessage());
            }

            DBConnection.closeConnection();
            connection = null;
        }
    }

    public void restarStockage_addCarritoCliente() {
        // TODO: 22/08/2024 metodo valido para admin al restar,
        //  o para agregar al carrito los user, y que el Stock disminuya
        Scanner sc = new Scanner(System.in);
        int idProd = -1;
        boolean existe = false;
        int cantidadRestada = 0;
        int stockActual = 0;

        System.out.println("📦Introduce el ID del producto que quieres restar STOCK 📦");

        idProd = sc.nextInt();

        if (verificarSiUnIDExisteDatabase(idProd)) {
            existe = true;
        }
        if (existe) {
            //a la vez que leemos y mostramos producto y stock,
            // establecemos la variable stock actual para en caso de querer restar más
            // del stock actual, que no sea posible
            stockActual = consultaYLecturaStockOfProduct(idProd);


        } else {
            System.out.println("El ID del producto no existe");
        }

        if (existe) {

            System.out.println("Introduce la cantidad de Stockage a RESTAR de este producto");
            cantidadRestada = sc.nextInt();

            if (cantidadRestada <= stockActual) {

                connection = DBConnection.getConnection();
                Statement statement = null;

                String query = String.format("UPDATE %s " +
                                "SET %s = %s - %s " +
                                "WHERE %s = %s;",
                        EsquemaDB.TAB_PRODUCTOS,
                        EsquemaDB.COL_STOCK, EsquemaDB.COL_STOCK, cantidadRestada,
                        EsquemaDB.COL_ID_PRODUCTO, idProd);


                try {
                    statement = connection.createStatement();
                    int numero = statement.executeUpdate(query);

                    if (numero > 0) {
                        //System.out.println("El numero de productos modificados por el update fue: "+numero+"");
                        System.out.println(" ✅ Las unidades del STOCKAGE del producto fueron sumados con exito! ✅");
                    }


                    statement.close();

                } catch (SQLException e) {
                    System.out.println("Error SQL connection al sumar Stockage\n" + e.getMessage());
                } catch (InputMismatchException e) {
                    System.out.println("Error en el tipo de datos introducido\n" + e.getMessage());
                } finally {

                    DBConnection.closeConnection();
                    connection = null;
                }

            } else {
                System.out.println("🚫 No puedes restar más Stock del que ya tienes 🚫");
            }


        }
    }

    public void deleteProductDatabase() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Introduce el ID, del producto que quieras eliminar");
        int idDelete = sc.nextInt();
        boolean existe = false;

        if (verificarSiUnIDExisteDatabase(idDelete)) {
            System.out.println("El producto que vas a eliminar es el siguiente: ");
            leerUnProductoDeLaDataBase(idDelete);
            existe = true;
        } else {
            System.out.println("Dicho ID no existe.");
        }

        if (existe) {

            connection = DBConnection.getConnection();

            Statement statement = null;

            try {
                statement = connection.createStatement();
                String query = String.format("DELETE FROM %s WHERE %s = %s;",
                        EsquemaDB.TAB_PRODUCTOS, EsquemaDB.COL_ID_PRODUCTO, idDelete);

                int afectadas = statement.executeUpdate(query);

                if (afectadas > 0) {
                    System.out.println("El producto con ID: " + idDelete + " ha sido 🚮 eliminado 🚮 de la DATABASE correctamente");
                } else {
                    System.out.println("EL producto no ha podido ser eliminado");
                }

            } catch (SQLException e) {
                System.out.println("Error conex SQL en Borrado Producto");
            } catch (InputMismatchException e) {
                System.out.println("El tipo de dato es incorrecto en DeleteProduct");
            } finally {
                DBConnection.closeConnection();
                connection = null;

            }

        }

    }

    public void addCarrito(){
        //
    }


    // RESTO CODIGO
}





