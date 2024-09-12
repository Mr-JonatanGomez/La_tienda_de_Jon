package repositories;

import database.DBConnection;
import database.EsquemaDB;
import menu.Menu_Inicio_App;

import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class PedidoRepository {
    private Connection connection;

   private double precio;

    ProductsRepository productsRepository=new ProductsRepository();



    public int stockAvailable(int idProducto) {
        connection = DBConnection.getConnection();
        Statement statement = null;
        ResultSet resultSet = null;
        int stock = -1;

        String query = String.format("SELECT %s, %s, %s,%s " +
                        "FROM %s " +
                        "WHERE %s = %s"

                , EsquemaDB.COL_ID_PRODUCTO, EsquemaDB.COL_NOMBRE, EsquemaDB.COL_STOCK,EsquemaDB.COL_PRECIO,
                EsquemaDB.TAB_PRODUCTOS,
                EsquemaDB.COL_ID_PRODUCTO, idProducto);

        try {
            statement = connection.createStatement();
            resultSet = statement.executeQuery(query);

            if (resultSet.next()) {
                int id = resultSet.getInt("id_producto");
                String name = resultSet.getString("nombre");
                stock = resultSet.getInt("stock");
                precio = resultSet.getDouble("precio");


                System.out.println("El stockage ACTUAL de:\n" +
                        "ID: " + id + ", " + name + ", es de " + stock + " unidades en TOTAL, con un precio unitario de: "+precio);
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
    public void addProductCarrito(int idClienteActual){

        System.out.println("Introduce el ID del producto que quieres agregar al carrito");
        Scanner sc = new Scanner(System.in);
        int idProducto= sc.nextInt();
        // AQUI NECESITO SACAR EL idClienteActual establecido en Menu_Inicio_App
        boolean existeProducto = false;
        int cantidadDisponible;


        if (productsRepository.verificarSiUnIDExisteDatabase(idProducto)){
            existeProducto = true;
        }else{
            System.out.println("El producto elegido no existe");
        }

        cantidadDisponible=stockAvailable(idProducto);
        // TODO: 27/08/2024 solo igualar cantidad Disponible hecha, sin el CRUD de agregar
        //para que no de cruce de conexion lo realizo por separado, podria meterse dentro del if pero me da fallos de conex.

        if (existeProducto){
            int cantidad;
            do {
            System.out.println("Cuanta cantidad de producto quieres, si introduces 0, volverás atrás");
            cantidad= sc.nextInt();

            if (cantidad<=cantidadDisponible && cantidad>0){
                double subtotal = cantidad*precio;

                //insertToCarrito(idClienteActual,idProducto,cantidad,precio,subtotal);
                insertToCarrito(idClienteActual,idProducto,cantidad,precio);
                System.out.println("La cantidad se agregó PROBANDO, en ningun lugar");
//luego poner precio a 0 para el siguiente por si acaso

            } else if (cantidad==0) {
                System.out.println("Te has arrepentido y no quieres el producto");

            } else{
                System.out.println("Lo siento, el stock máximo disponible es de "+cantidadDisponible);
            }


            }while(cantidad > cantidadDisponible);
        }
    }
    public void insertToCarrito(int idCliente, int idProd, int cantidad, double precio) {
        connection = DBConnection.getConnection();
        PreparedStatement preparedStatement = null;

        // Definir la consulta sin el id_carrito, ya que es AUTO_INCREMENT
        String query = "INSERT INTO " + EsquemaDB.TAB_CARRITO + " (" +
                EsquemaDB.COL_ID_CLIENTE + ", " +
                EsquemaDB.COL_ID_PRODUCTO + ", " +
                EsquemaDB.COL_CANTIDAD + ", " +
                EsquemaDB.COL_PRECIO_UNITARIO + ") " +
                "VALUES (?, ?, ?, ?);";

        try {
            // Preparar la consulta con valores dinámicos
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, idCliente);
            preparedStatement.setInt(2, idProd);
            preparedStatement.setInt(3, cantidad);
            preparedStatement.setDouble(4, precio);



            preparedStatement.executeUpdate();
            System.out.println("Producto agregado correctamente al carrito.");
        } catch (SQLException e) {
            System.err.println("Error al agregar producto al carrito: " + e.getMessage());
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) DBConnection.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    public void readCarrito(int idCliente){
        connection=DBConnection.getConnection();

        Statement statement = null;
        ResultSet resultSet = null;
//hacer 2 querys, una para leer y la otra que calcule el precio, para que cliente lo sepa
        String query = String.format("SELECT %s,%s, FROM %s WHERE %s = %s",EsquemaDB.TAB);

        DBConnection.closeConnection();
        connection=null;
    }
    public void confirmarPedido(int idClienteActual){
        //primero leemos su carrito, para cerciorarnos que es correcto.
        /*
         Problemas, que pueden aparecer y hay que solucionar:
            1- cuando vas a confirmar compra, que el producto no tenga stock porque otro cliente lo compró primero
            1S- Para ello comrpobamos el carrito, si algo no tiene stock suficiente:
                    a) otorgarle el maximo disponible
                    b) cancelar el producto definitivamente y seguir
                    c) cancelar carrito completo
                IF (A||B) proseguir compra - ELSE delete carrito where idCliente...
         */
    }
}
