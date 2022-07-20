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
            final Headers headers = exchange.getResponseHeaders();
            final JSONObject queryJSONObject = ToolKit.getJSONFromHttpExchangeQuery(exchange);//new JSONObject(ToolKit.queryToMap(exchange.getRequestURI().getQuery()));
            final JSONObject bodyJSONObject = ToolKit.getJSONFromHttpExchangeBody(exchange);
            final String requestMethod = (queryJSONObject.containsKey("method") && ServerEnvironmentVariables.IN_TEST_MODE) ? ((String) queryJSONObject.get("method")).toUpperCase() : exchange.getRequestMethod().toUpperCase();
            final String apiIdentifier = exchange.getRequestURI().getPath().split("/")[2];
            switch (requestMethod) {
                case HttpConstants.METHOD_GET -> { //Read-Only Access to resources
                    processGetRequest(apiIdentifier, headers, exchange, queryJSONObject, bodyJSONObject);
                }
                case HttpConstants.METHOD_PUT -> { //Update a resource
                    processPutRequest(apiIdentifier, headers, exchange, queryJSONObject, bodyJSONObject);
                }
                case HttpConstants.METHOD_POST -> { //Create a new resource
                    processPostRequest(apiIdentifier, headers, exchange, queryJSONObject, bodyJSONObject);
                }
                case HttpConstants.METHOD_DELETE -> { //Remove a resource
                    processDeleteRequest(apiIdentifier, headers, exchange, queryJSONObject, bodyJSONObject);
                }
                case HttpConstants.METHOD_OPTIONS -> { //Return what methods are supported
                    headers.set(HttpConstants.HEADER_ALLOW, HttpConstants.ALLOWED_METHODS);
                    exchange.sendResponseHeaders(HttpConstants.STATUS_OK, HttpConstants.NO_RESPONSE_LENGTH);
                }
                default -> {
                    headers.set(HttpConstants.HEADER_ALLOW, HttpConstants.ALLOWED_METHODS);
                    exchange.sendResponseHeaders(HttpConstants.STATUS_METHOD_NOT_ALLOWED, HttpConstants.NO_RESPONSE_LENGTH);
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace(System.out);
        }
    }

    private static void processGetRequest(String apiIdentifier, Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        switch (apiIdentifier) {
            case "VerifyUsernameIsAvailable" -> {
                verifyUsernameIsAvailable(headers, exchange, queryJSONObject, bodyJSONObject);
            }
            case "VerifyCultureIDIsAvailable" -> {
                verifyCultureIDIsAvailable(headers, exchange, queryJSONObject, bodyJSONObject);
            }
            case "ValidateLogin" -> {
                validateLogin(headers, exchange, queryJSONObject, bodyJSONObject);
            }
            default -> {
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.methodNotFoundError(), headers, exchange);
            }
        }
    }

    private static void processPutRequest(String apiIdentifier, Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        switch (apiIdentifier) {
            case "ValidateUsernameVerificationKey" -> {
                validateUsernameVerificationKey(headers, exchange, queryJSONObject, bodyJSONObject);
            }
            default -> {
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.methodNotFoundError(), headers, exchange);
            }
        }
    }

    private static void processPostRequest(String apiIdentifier, Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        switch (apiIdentifier) {
            case "CreateAccount" -> {
                createAccount(headers, exchange, queryJSONObject, bodyJSONObject);
            }
            case "RegisterUsernameForVerification" -> {
                registerUsernameForVerification(headers, exchange, queryJSONObject, bodyJSONObject);
            }
            default -> {
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.methodNotFoundError(), headers, exchange);
            }
        }
    }

    private static void processDeleteRequest(String apiIdentifier, Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        switch (apiIdentifier) {
            default -> {
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.methodNotFoundError(), headers, exchange);
            }
        }
    }

    private static void verifyUsernameIsAvailable(Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.VERIFY_EMAIL_IS_AVAILABLE, queryJSONObject, bodyJSONObject, headers);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(true);
                ToolKit.sendApiJSONResponse(database.isUsernameAvailable(requestJSONObject, headers, databaseConnection), headers, exchange);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), headers, exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), headers, exchange);
        }
    }

    private static void verifyCultureIDIsAvailable(Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.VERIFY_CULTURE_ID_IS_AVAILABLE, queryJSONObject, bodyJSONObject, headers);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(true);
                ToolKit.sendApiJSONResponse(database.isCultureIDAvailable(requestJSONObject, headers, databaseConnection), headers, exchange);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), headers, exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), headers, exchange);
        }
    }

    private static void validateLogin(Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.VALIDATE_LOGIN, queryJSONObject, bodyJSONObject, headers);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            if (requestJSONObject.containsKey("USERNAME") ^ requestJSONObject.containsKey("CULTURE_ID")) { //XOR
                Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
                try {
                    databaseConnection.setAutoCommit(true);
                    ToolKit.sendApiJSONResponse(database.validateLogin(requestJSONObject, headers, databaseConnection), headers, exchange);
                } catch (SQLException e) {
                    e.printStackTrace(System.out);
                    ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), headers, exchange);
                } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                    e.printStackTrace(System.out);
                    ToolKit.sendApiJSONResponse(GenericErrorApiResponse.generalError(), headers, exchange);
                } finally {
                    EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
                }
            } else { //didnt include at least one of USERNAME or CULTURE_ID
                JSONObject reasonJSON = new JSONObject();
                reasonJSON.put("ERROR", "Must Include One Of The Following: USERNAME Or CULTURE_ID");
            }
        } else {
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), headers, exchange);
        }
    }

    private static void validateUsernameVerificationKey(Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.VALIDATE_USERNAME_VERIFICATION_KEY, queryJSONObject, bodyJSONObject, headers);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(true);
                ToolKit.sendApiJSONResponse(database.validateUsernameVerificationKey(requestJSONObject, headers, databaseConnection), headers, exchange);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), headers, exchange);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.generalError(), headers, exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), headers, exchange);
        }
    }

    private static void createAccount(Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.CREATE_ACCOUNT, queryJSONObject, bodyJSONObject, headers);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(false); //all or nothing, prevents partial commits if error occurs 
                ToolKit.sendApiJSONResponse(database.createAccount(requestJSONObject, headers, databaseConnection), headers, exchange);
                databaseConnection.commit();
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                try {
                    databaseConnection.rollback();
                } //undo database entries 
                catch (SQLException ex) {
                } //do nothing
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), headers, exchange);
            } catch (InvalidKeySpecException | NoSuchAlgorithmException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.generalError(), headers, exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation 
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), headers, exchange);
        }
    }

    private static void registerUsernameForVerification(Headers headers, HttpExchange exchange, JSONObject queryJSONObject, JSONObject bodyJSONObject) throws IOException {
        ApiRequestValidator apiRequestValidator = new ApiRequestValidator();
        ApiRequestValidationResult apiRequestValidationResult = apiRequestValidator.validateApiRequest(SupportedAPIs.REGISTER_USERNAME_FOR_VERIFICATION, queryJSONObject, bodyJSONObject, headers);
        if (apiRequestValidationResult.passedApiValidation()) {
            JSONObject requestJSONObject = ToolKit.mergeJSONObjects(queryJSONObject, bodyJSONObject);
            Connection databaseConnection = EarthMachine.databaseConnectionPool.checkOut();
            try {
                databaseConnection.setAutoCommit(true);
                ToolKit.sendApiJSONResponse(database.sendUsernameVerification(requestJSONObject, headers, databaseConnection), headers, exchange);
            } catch (SQLException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.databaseError(), headers, exchange);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace(System.out);
                ToolKit.sendApiJSONResponse(GenericErrorApiResponse.generalError(), headers, exchange);
            } finally {
                EarthMachine.databaseConnectionPool.checkIn(databaseConnection);
            }
        } else { //failed validation 
            ToolKit.sendApiJSONResponse(new ApiResponseData(apiRequestValidationResult.getReason(), HttpConstants.STATUS_BAD_REQUEST), headers, exchange);
        }
    }
}
