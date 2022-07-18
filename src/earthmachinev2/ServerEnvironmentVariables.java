/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachinev2;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Christopher Brett
 */
public class ServerEnvironmentVariables {
     static boolean IN_TEST_MODE;
    static int INCOMING_PORT_TESTING;
    static int INCOMING_PORT_PRODUCTION;
    static boolean DEBUG_ENABLED;
    static String DEBUG_EMAIL;
    static String KEY_STORE_PASSWORD;
    static String DATABASE_DRIVER;
    static String DATABASE_URL;
    static String DATABASE_USERNAME;
    static String DATABASE_PASSWORD;
    static String DATABASE_MAX_POOL;
    static String EMAIL_SMTP_HOST_NAME;
    static int EMAIL_SMTP_HOST_PORT;
    static String EMAIL_SMTP_AUTH_USER;
    static String EMAIL_SMTP_AUTH_PASSWORD;
    
    protected static void loadVariablesFromFile(){
        
        try{
            File serverEnvironmentVariables = new File("RESTRICTED/ServerEnvironmentVariables.json");
            JSONParser jsonParser = new JSONParser();
            FileReader fileReader = new FileReader(serverEnvironmentVariables);
            JSONObject jsonObject = (JSONObject) jsonParser.parse(fileReader);
            IN_TEST_MODE = (Boolean) jsonObject.get("IN_TEST_MODE");
            INCOMING_PORT_TESTING = ((Long) jsonObject.get("INCOMING_PORT_TESTING")).intValue();
            INCOMING_PORT_PRODUCTION = ((Long) jsonObject.get("INCOMING_PORT_PRODUCTION")).intValue();
            DEBUG_ENABLED = (Boolean) jsonObject.get("DEBUG_ENABLED");
            DEBUG_EMAIL = (String) jsonObject.get("DEBUG_EMAIL");
            KEY_STORE_PASSWORD = (String) jsonObject.get("KEY_STORE_PASSWORD");
            DATABASE_DRIVER = (String) jsonObject.get("DATABASE_DRIVER");
            DATABASE_URL = (String) jsonObject.get("DATABASE_URL");
            DATABASE_USERNAME = (String) jsonObject.get("DATABASE_USERNAME");
            DATABASE_PASSWORD = (String) jsonObject.get("DATABASE_PASSWORD");
            DATABASE_MAX_POOL = (String) jsonObject.get("DATABASE_MAX_POOL");
            EMAIL_SMTP_HOST_NAME = (String) jsonObject.get("EMAIL_SMTP_HOST_NAME");
            EMAIL_SMTP_HOST_PORT = ((Long) jsonObject.get("EMAIL_SMTP_HOST_PORT")).intValue();
            EMAIL_SMTP_AUTH_USER = (String) jsonObject.get("EMAIL_SMTP_AUTH_USER");
            EMAIL_SMTP_AUTH_PASSWORD = (String) jsonObject.get("EMAIL_SMTP_AUTH_PASSWORD");
             
        }catch(IOException | ParseException e){
            e.printStackTrace(System.out);
        }
    }
}
