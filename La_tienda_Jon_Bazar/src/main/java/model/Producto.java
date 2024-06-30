package model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class Producto {
    //URL PARA JSON 'https://dummyjson.com/products'
    private int id;
    private String nombre;
    private String categoria;
    private double precio;
    private String descripcion;

    public Producto(int id, String nombre, String categoria, double precio, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public void mostrarDatos(){
        System.out.println("id = " + id);
        System.out.println("nombre = " + nombre);
        System.out.println("categoria = " + categoria);
        System.out.println("precio = " + precio);
        System.out.println("descripcion = " + descripcion);
    }
}
