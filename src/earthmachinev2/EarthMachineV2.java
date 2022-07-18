/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package earthmachinev2;

import java.io.File;

/**
 *
 * @author Christopher Brett
 */
public class EarthMachineV2 {

    protected static ApiRequestValidator apiValidator;
    protected static DatabaseConnectionPool databaseConnectionPool;
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        ServerEnvironmentVariables.loadVariablesFromFile(); //Load in environment variables before loading anything else 
        apiValidator = new ApiRequestValidator();
        databaseConnectionPool = new DatabaseConnectionPool();
        new SecureApiRequestListener().start();
    }
    
}
