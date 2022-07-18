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
public class RootRequestHandler implements HttpHandler {

    //Send the main webpage to the user 
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        DebugTools.printHttpExchangeRequestInfo(exchange);
        StringBuilder contentBuilder = new StringBuilder();
        try ( BufferedReader in = new BufferedReader(new FileReader("DATA/culture_earth_v1.html"))) {
            String str;
            while ((str = in.readLine()) != null) {
                contentBuilder.append(str);
            }
        } catch (IOException e) {
            e.printStackTrace(System.out);
        }
        String content = contentBuilder.toString();
        exchange.sendResponseHeaders(HttpConstants.STATUS_OK, content.length());
        try ( OutputStream os = exchange.getResponseBody()) {
            os.write(content.getBytes());
        }

        //Send Image Example
        /*
        File file = new File("DATA/image.jpg");
        exchange.sendResponseHeaders(200, file.length());
        OutputStream outputStream = exchange.getResponseBody();
        Files.copy(file.toPath(), outputStream);
        outputStream.close();*/
    }
}
