/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpPrincipal;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author Christopher Brett
 */
public class SecureApiRequestListener extends Thread {
    
    @Override
    public void run() {
        
        //Temporary until we add HTTPS
        try{
            int port = 9000;
            HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
            server.createContext("/", new RootRequestHandler());
            server.createContext("/Account", new AccountRequestHandler());
            server.setExecutor(new ThreadPoolExecutor(6, 24, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100)));
            server.start();
        }catch(IOException e){
            e.printStackTrace(System.out);
        }
    }
}
