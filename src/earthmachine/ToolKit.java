/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Christopher Brett
 */
public class ToolKit {

    protected static JSONObject getJSONFromHttpExchangeQuery(HttpExchange exchange) {
        Map<String, String> jsonMap = new HashMap<>();
        for (String param : exchange.getRequestURI().getQuery().split("&")) {
            String[] entry = param.split("=");
            if (entry.length > 1) {
                jsonMap.put(entry[0], entry[1]);
            } else {
                jsonMap.put(entry[0], "");
            }
        }
        return new JSONObject(jsonMap);
    }

    protected static JSONObject getJSONFromHttpExchangeBody(HttpExchange exchange) throws ParseException, UnsupportedEncodingException, IOException {
        StringBuilder buffer;
        try ( InputStreamReader inputStreamReader = new InputStreamReader(exchange.getRequestBody(), "utf-8");  BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
            int character;
            buffer = new StringBuilder();
            while ((character = bufferedReader.read()) != -1) {
                buffer.append((char) character);
            }
        }
        JSONParser jSONParser = new JSONParser();
        return buffer.isEmpty() ? new JSONObject() : (JSONObject) jSONParser.parse(buffer.toString());
    }
    
    protected static JSONObject mergeJSONObjects(JSONObject json1, JSONObject json2){
        json1.putAll(json2);
        return json1;
    }

    protected static void sendApiJSONResponse(ApiResponseData response, Headers headers, HttpExchange exchange) throws IOException {
        String responseBody = response.getResponse().toJSONString();
        headers.set(HttpConstants.HEADER_CONTENT_TYPE, String.format(HttpConstants.FORMAT_JSON, HttpConstants.CHARSET_UTF8));
        if (response.getExtraHeaders() != null) {
            for (Map.Entry<String, String> entry : response.getExtraHeaders().entrySet()) {
                headers.set(entry.getKey(), entry.getValue());
            }
        }
        final byte[] rawResponseBody = responseBody.getBytes(HttpConstants.CHARSET_UTF8);
        exchange.sendResponseHeaders(response.getHttpStatus(), rawResponseBody.length);
        exchange.getResponseBody().write(rawResponseBody);
    }
}
