/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.SQLException;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

/**
 *
 * @author Christopher Brett
 */
public class AccountRequestHandler implements HttpHandler {

    private static final DatabaseControl database = new DatabaseControl();

    @Override
    public void handle(HttpExchange exchange) {
        DebugTools.printHttpExchangeRequestInfo(exchange);
        try (exchange) {
            final Headers requestHeaders = exchange.getRequestHeaders();
            final JSONObject queryJSONObject = ToolKit.getJSONFromHttpExchangeQuery(exchange);
            final JSONObject bodyJSONObject = ToolKit.getJSONFromHttpExchangeBody(exchange);
            final String requestMethod = (queryJSONObject.containsKey("method") && ServerEnvironmentVariables.IN_TEST_MODE) ? ((String) queryJSONObject.get("method")).toUpperCase() : exchange.getRequestMethod().toUpperCase();
            final String apiIdentifier = exchange.getRequestURI().getPath().split("/")[2];
            switch (requestMethod) {
                case HttpConstants.METHOD_GET -> { //Read-Only Access to resources
                    processGetRequest(apiIdentifier, requestHeaders, exchange, queryJSONObject, bodyJSONObject);
                }
                case HttpConstants.METHOD_PUT -> { //Update a resource
                    processPutRequest(apiIdentifier, requestHeaders, exchange, queryJSONObject, bodyJSONObject);
                }
                case HttpConstants.METHOD_POST -> { //Create a new resource
                    processPostRequest(apiIdentifier, requestHeaders, exchange, queryJSONObject, bodyJSONObject);
                }
                case HttpConstants.METHOD_DELETE -> { //Remove a resource
                    processDeleteRequest(apiIdentifier, requestHeaders, exchange, queryJSONObject, bodyJSONObject);
                }
                case HttpConstants.METHOD_OPTIONS -> { //Return what methods are supported
                    ToolKit.sendMethodOptionsResponse(exchange);
                }
                default -> {
                    ToolKit.sendDefaultResponse(exchange);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace(System.out);
        }
    }

    private static void processGetRequest(String apiIdentifier, Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        switch (apiIdentifier) {
            case "VerifyUsernameIsAvailable" -> {
                verifyUsernameIsAvailable(requestHeaders, exchange, queryJSONObject, bodyJSONObject);
            }
            case "VerifyCultureIDIsAvailable" -> {
                verifyCultureIDIsAvailable(requestHeaders, exchange, queryJSONObject, bodyJSONObject);
            }
            default -> {
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.methodNotFoundError(), exchange);
            }
        }
    }

    private static void processPutRequest(String apiIdentifier, Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        switch (apiIdentifier) {
            case "ValidateUsernameVerificationKey" -> {
                validateUsernameVerificationKey(requestHeaders, exchange, queryJSONObject, bodyJSONObject);
            }
            default -> {
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.methodNotFoundError(), exchange);
            }
        }
    }

    private static void processPostRequest(String apiIdentifier, Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        switch (apiIdentifier) {
            case "CreateAccount" -> {
                createAccount(requestHeaders, exchange, queryJSONObject, bodyJSONObject);
            }
            case "RegisterUsernameForVerification" -> {
                registerUsernameForVerification(requestHeaders, exchange, queryJSONObject, bodyJSONObject);
            }
            case "ValidateLogin" -> {
                validateLogin(requestHeaders, exchange, queryJSONObject, bodyJSONObject);
            }
            default -> {
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.methodNotFoundError(), exchange);
            }
        }
    }

    private static void processDeleteRequest(String apiIdentifier, Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        switch (apiIdentifier) {
            default -> {
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.methodNotFoundError(), exchange);
            }
        }
    }

    private static void verifyUsernameIsAvailable(Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.VERIFY_EMAIL_IS_AVAILABLE, queryJSONObject, bodyJSONObject, requestHeaders);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(true);
                ToolKit.sendApiJSONResponse(database.isUsernameAvailable(requestJSONObject, requestHeaders, databaseConnection), exchange);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), exchange);
        }
    }

    private static void verifyCultureIDIsAvailable(Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.VERIFY_CULTURE_ID_IS_AVAILABLE, queryJSONObject, bodyJSONObject, requestHeaders);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(true);
                ToolKit.sendApiJSONResponse(database.isCultureIDAvailable(requestJSONObject, requestHeaders, databaseConnection), exchange);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), exchange);
        }
    }

    private static void validateUsernameVerificationKey(Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.VALIDATE_USERNAME_VERIFICATION_KEY, queryJSONObject, bodyJSONObject, requestHeaders);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(true);
                ToolKit.sendApiJSONResponse(database.validateUsernameVerificationKey(requestJSONObject, requestHeaders, databaseConnection), exchange);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), exchange);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.generalError(), exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), exchange);
        }
    }

    private static void createAccount(Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.CREATE_ACCOUNT, queryJSONObject, bodyJSONObject, requestHeaders);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(false); //all or nothing, prevents partial commits if error occurs 
                ToolKit.sendApiJSONResponse(database.createAccount(requestJSONObject, requestHeaders, databaseConnection), exchange);
                databaseConnection.commit();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                try {
                    databaseConnection.rollback();
                } //undo database entries 
                catch (SQLException ex) {
                } //do nothing
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), exchange);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.generalError(), exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation 
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), exchange);
        }
    }

    private static void registerUsernameForVerification(Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.REGISTER_USERNAME_FOR_VERIFICATION, queryJSONObject, bodyJSONObject, requestHeaders);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(true);
                ToolKit.sendApiJSONResponse(database.sendUsernameVerification(requestJSONObject, requestHeaders, databaseConnection), exchange);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), exchange);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.generalError(), exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation 
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), exchange);
        }
    }

    private static void validateLogin(Headers requestHeaders, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.VALIDATE_LOGIN, queryJSONObject, bodyJSONObject, requestHeaders);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            if (requestJSONObject.containsKey("USERNAME") ^ requestJSONObject.containsKey("CULTURE_ID")) { //XOR
                Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
                try {
                    databaseConnection.setAutoCommit(true);
                    ToolKit.sendApiJSONResponse(database.validateLogin(requestJSONObject, requestHeaders, databaseConnection), exchange);
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), exchange);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace(System.out);
                    ToolKit.sendApiJSONResponse(GenericErrorApiResponse.generalError(), exchange);
                } finally {
                    EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
                }
            } else { //didnt include at least one of USERNAME or CULTURE_ID
                JSONObject reasonJSON = new JSONObject();
                reasonJSON.put("ERROR", "Must Include One Of The Following: USERNAME Or CULTURE_ID");
                ToolKit.sendApiJSONResponse(new ApiResponseData(reasonJSON, HttpConstants.STATUS_BAD_REQUEST), exchange);
            }
        } else {
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), exchange);
        }
    }
}
