/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Christopher Brett
 */
public class DebugTools {

    protected static void printHttpExchangeRequestInfo(HttpExchange exchange) { //Great for debugging HTTP stuff
        System.out.println("********************************************");
        System.out.println("-- request headers --");
        Headers requestHeaders = exchange.getRequestHeaders();
        requestHeaders.entrySet().forEach(System.out::println);
        
        System.out.println("-- response headers --");
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.entrySet().forEach(System.out::println);

        System.out.println("-- principle --");
        HttpPrincipal principal = exchange.getPrincipal();
        System.out.println(principal);

        System.out.println("-- Context --");
        HttpContext context = exchange.getHttpContext();
        System.out.println(context.getPath());

        System.out.println("-- Path --");
        URI uri = exchange.getRequestURI();
        System.out.println(uri.getPath());

        System.out.println("-- HTTP method --");
        String requestMethod = exchange.getRequestMethod();
        System.out.println(requestMethod);

        System.out.println("-- Query --");
        URI requestURI = exchange.getRequestURI();
        String query = requestURI.getQuery();
        System.out.println(query);

        /*try{
            System.out.println("-- Body --");
            System.out.println(ToolKit.getJSONFromHttpExchangeBody(exchange).toString());
        }catch(IOException | ParseException e){
            e.printStackTrace(System.out);
        }*/
    }
}
