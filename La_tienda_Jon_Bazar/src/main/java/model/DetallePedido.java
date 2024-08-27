package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
@Setter
@Getter
@NoArgsConstructor
public class DetallePedido {
    private int idCliente;
    private int idPedido;
    private int id_Detalle;
    private int cantidad;
    private double precioUnidad;
    private double subtotal;

    private ArrayList<Producto>listadoProductos;
}
