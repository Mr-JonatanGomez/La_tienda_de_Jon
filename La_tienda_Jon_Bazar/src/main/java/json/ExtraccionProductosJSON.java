package json;

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
    private ArrayList<Producto> listadoProductos;

    public ExtraccionProductosJSON(URL url, ArrayList<Producto> listadoProductos) {
        this.url = url;
        this.listadoProductos = new ArrayList<>();
    }

    public void crearProductsYLeerlosENArrayDeJava() {
        BufferedReader bufferedReader = null;
        listadoProductos= new ArrayList<>();
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
        }finally {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                System.err.println("Error cerrado de buffered");
            }
        }
    }

    {

    }
}
