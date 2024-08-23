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
    private int stock;
    private String descripcion;

    public Producto(int id, String nombre, String categoria, double precio, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public Producto(int id, String nombre, double precio, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.descripcion = descripcion;
    }

    public Producto(int id, String nombre, double precio, int stock, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
    }

    public Producto(int id, String nombre, String categoria, double precio, int stock, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.categoria = categoria;
        this.precio = precio;
        this.stock = stock;
        this.descripcion = descripcion;
    }

    public void mostrarDatos() {
        System.out.println("id = " + id);
        System.out.println("nombre = " + nombre);
        System.out.println("categoria = " + categoria);
        System.out.println("precio = " + precio);
        System.out.println("descripcion = " + descripcion);
    }

    public void mostrarDatos2() {
        System.out.println("id = " + id);
        System.out.println("nombre = " + nombre);
        System.out.println("precio = " + precio);
        System.out.println("descripcion = " + descripcion);
    }

    public void mostrarDatos3() {
        System.out.println("id = " + id);
        System.out.println("nombre = " + nombre);
        System.out.println("categoria = " + categoria);
        System.out.println("precio = " + precio);
        System.out.println("stock = " + stock);
        System.out.println("descripcion = " + descripcion);
    }
    public void mostrarDatos4() {
        System.out.println("id = " + id);
        System.out.println("nombre = " + nombre);
        System.out.println("precio = " + precio);
        System.out.println("stock = " + stock);
        System.out.println("descripcion = " + descripcion);
    }
}