/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachinev2;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Christopher Brett
 */
public class HttpRootRequestHandler implements HttpHandler{

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
            try {
                BufferedReader in = new BufferedReader(new FileReader("DATA/culture_earth_v1.html"));
                String str;
                while ((str = in.readLine()) != null) {
                    contentBuilder.append(str);
                }
                in.close();
            } catch (IOException e) {
            }
            String content = contentBuilder.toString();
            exchange.sendResponseHeaders(200, content.length());
            OutputStream os = exchange.getResponseBody();
            os.write(content.getBytes());
            os.close();
    }
    
}
