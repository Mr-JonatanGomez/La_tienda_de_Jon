package database;

public interface EsquemaDB {
    String DB_NAME = "bazar_jon";

    // Tablas
    String TAB_CLIENTES = "clientes";

    String TAB_PEDIDOS = "pedidos";
    String TAB_PRODUCTOS = "productos";
    String TAB_DETALLES = "detalles_pedido";
    String TAB_CARRITO = "carrito";
    String COL_ID_CLIENTE = "id_cliente";
    String COL_NOMBRE = "nombre";
    String COL_CORREO = "correo";
    String COL_PASSWORD = "password";
    String COL_PRECIO = "precio";

        String COL_CATEGORIA = "categoria";
        String COL_ID_PRODUCTO = "id_producto";

        String COL_STOCK = "stock";
        String COL_DESCRIPCION = "descripcion";
        String COL_ID_DETALLE = "id_detalle";
        String COL_ID_PEDIDO = "id_pedido";
        String COL_CANTIDAD = "cantidad";
        String COL_PRECIO_UNITARIO = "precio_unitario";
        String COL_SUBTOTAL = "subtotal";
        String COL_ID_CARRITO = "id_carrito";

}