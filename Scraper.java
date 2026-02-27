import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;

public class Scraper {
    public static void main(String[] args) {
        // Definimos la URL real y el archivo de salida
        String url = "http://books.toscrape.com/";
        String csvFile = "productos_limpios.csv";

        try {
            System.out.println("🌐 Conectando a " + url + "...");
            
            // 1. Conexión y Carga del HTML desde Internet
            // Usamos userAgent para evitar que el sitio nos bloquee por ser un bot básico
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .get();

            // 2. Selección de Elementos (cada libro)
            Elements productos = doc.select(".product_pod");

            // Preparamos la escritura del archivo
            FileWriter writer = new FileWriter(csvFile);
            writer.append("Titulo;Precio\n"); // Cabeceras

            int contador = 0;

            // 3. Extracción y Limpieza de Datos
            for (Element producto : productos) {
                // Extraemos el título desde el atributo 'title' para tener el nombre completo
                String titulo = producto.select("h3 a").attr("title");
                
                // Extraemos el texto del precio (ej. "£51.77")
                String precioCrudo = producto.select(".price_color").text();

                // Limpiamos el dato: quitamos el símbolo '£' (y a veces un carácter 'Â' oculto que se cuela en la codificación)
                String precioLimpio = precioCrudo.replace("£", "").replace("Â", "").trim();

                // 4. Guardado en el CSV (separado por punto y coma)
                writer.append(titulo).append(";").append(precioLimpio).append("\n");
                contador++;
            }

            writer.flush();
            writer.close();
            System.out.println("✅ ¡Éxito! Se han extraído " + contador + " libros y se guardaron en " + csvFile);

        } catch (IOException e) {
            System.err.println("❌ Error al conectar o escribir el archivo: " + e.getMessage());
        }
    }
}