package json;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

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

    {
        try {
            url = new URL("https://dummyjson.com/products");
            HttpURLConnection connection = url.openConnection();
            // TODO: 28/06/2024 MINUTO DE LA CLASE 41:19 
        } catch (MalformedURLException e) {
            System.err.println("Error en la codificacion de la URL");
        }
    }
}
