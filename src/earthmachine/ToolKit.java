/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Christopher Brett
 */
public class ToolKit {

    protected static Map<String, String> queryToMap(String query) { //Perhaps make more resilient to malformed queries in the future?
        if (query == null) {
            return null;
        }
        Map<String, String> result = new HashMap<>();
        for (String param : query.split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                result.put(entry[0], entry[1]);
            } else {
                result.put(entry[0], "");
            }
        }
        return result;
    }
    
    protected static void sendApiJSONResponse(ApiResponseData response, Headers headers, HttpExchange exchange) throws IOException {
        String responseBody = response.getResponse().toJSONString();
        headers.set(HttpConstants.HEADER_CONTENT_TYPE, String.format(HttpConstants.FORMAT_JSON, HttpConstants.CHARSET_UTF8));
        if(response.getExtraHeaders() != null){
            for (Map.Entry<String,String> entry : response.getExtraHeaders().entrySet()){
                headers.set(entry.getKey(), entry.getValue());
            }      
        }
        final byte[] rawResponseBody = responseBody.getBytes(HttpConstants.CHARSET_UTF8);
        exchange.sendResponseHeaders(response.getHttpStatus(), rawResponseBody.length);
        exchange.getResponseBody().write(rawResponseBody);
    }
}
