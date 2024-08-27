package repositories;

import database.DBConnection;
import database.EsquemaDB;

import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Scanner;

public class PedidoRepository {
    private Connection connection;
    private ArrayList<ProductsRepository> carritoCompra;
    ProductsRepository productsRepository=new ProductsRepository();


    public int stockAvailable(int idProducto) {
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
    public void addProductCarrito(){
        System.out.println("Introduce el ID del producto que quieres agregar al carrito");
        Scanner sc = new Scanner(System.in);
        int idProducto= sc.nextInt();
        boolean existeProducto = false;
        int cantidadDisponible;

        if (productsRepository.verificarSiUnIDExisteDatabase(idProducto)){
            existeProducto = true;
        }else{
            System.out.println("El producto elegido no existe");
        }
        cantidadDisponible=productsRepository.consultaYLecturaStockOfProduct(idProducto);

        // TODO: 27/08/2024 solo igualar cantidad Disponible hecha, sin el CRUD de agregar 
        //para que no de cruce de conexion lo realizo por separado, podria meterse dentro del if pero me da fallos de conex.

        if (existeProducto){
            int cantidad;
            System.out.println("Cuanta cantidad de producto quieres");
            cantidad= sc.nextInt();
        }
    }
}
