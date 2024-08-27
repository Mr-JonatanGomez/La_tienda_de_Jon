package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
@NoArgsConstructor
public class Pedido {
    private int id_Pedido;
    private int id_Cliente;
    private ArrayList<Producto> Carrito;
}
