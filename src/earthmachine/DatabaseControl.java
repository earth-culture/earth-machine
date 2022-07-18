/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Brett
 */
public class DatabaseControl {

    //GET
    protected ApiResponseData isUsernameAvailable(JSONObject request, Connection databaseConnection) throws SQLException {
        JSONObject response = new JSONObject();
        final String sql = "select ID from logins where username = ?";
        final PreparedStatement statement = databaseConnection.prepareStatement(sql);
        statement.setString(1, (String) request.get("USERNAME"));
        final ResultSet results = statement.executeQuery();
        boolean foundInLogins = results.next(), foundInPendingEmails = false, isVerified = false;
        final String sql2 = "select verified from emailverificationrequests where email = ?";
        final PreparedStatement statement2 = databaseConnection.prepareStatement(sql2);
        statement2.setString(1, (String) request.get("USERNAME"));
        final ResultSet results2 = statement2.executeQuery();
        if (results2.next()) {
            foundInPendingEmails = true;
            isVerified = results2.getString("verified").equals("T");
        }
        response.put("USERNAME_AVAILABLE", (!foundInLogins) ? "true" : "false");
        response.put("VERIFICATION_STATUS", foundInPendingEmails ? (isVerified ? "VERIFIED" : "PENDING") : "NOT STARTED");
        return new ApiResponseData(response, HttpConstants.STATUS_OK);
    }

    protected ApiResponseData isCultureIDAvailable(JSONObject request, Connection databaseConnection) throws SQLException {
        JSONObject response = new JSONObject();
        final String sql = "select ID from logins where cultureid = ?";
        final PreparedStatement statement = databaseConnection.prepareStatement(sql);
        statement.setString(1, (String) request.get("CULTURE_ID"));
        final ResultSet results = statement.executeQuery();
        response.put("CULTURE_ID_AVAILABLE", results.next() ? "false" : "true");
        return new ApiResponseData(response, HttpConstants.STATUS_OK);
    }

    //PUT
    protected ApiResponseData validateUsernameVerificationKey(JSONObject request, Connection databaseConnection) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        JSONObject response = new JSONObject();
        final EncryptionManager encryptionManager = new EncryptionManager();
        final String sql = "select salt, hash, verified from emailverificationrequests where email = ?";
        final PreparedStatement statement = databaseConnection.prepareStatement(sql);
        statement.setString(1, (String) request.get("USERNAME"));
        final ResultSet results = statement.executeQuery();
        if (results.next()) {
            if (results.getString("verified").equals("F")) {
                if (encryptionManager.authenticate(((String) request.get("VERIFICATION_KEY")).toUpperCase(), results.getBytes("hash"), results.getBytes("salt"))) {
                    final String sql2 = "update emailverificationrequests set verified = ? where email = ?";
                    final PreparedStatement statement2 = databaseConnection.prepareStatement(sql2);
                    statement2.setString(1, "T");
                    statement2.setString(2, (String) request.get("USERNAME"));
                    statement2.execute();
                    response.put("RESULT", "success");
                    return new ApiResponseData(response, HttpConstants.STATUS_OK);
                } else {
                    response.put("ERROR", "Incorrect Verification Key");
                    return new ApiResponseData(response, HttpConstants.STATUS_OK);
                }
            } else { //already verified email
                response.put("ERROR", "Email Has Already Been Verified");
                return new ApiResponseData(response, HttpConstants.STATUS_OK);
            }
        } else { //not found 
            response.put("ERROR", "Username Not Found");
            return new ApiResponseData(response, HttpConstants.STATUS_NOT_FOUND);
        }
    }

    //POST
    protected ApiResponseData createAccount(JSONObject request, Connection databaseConnection) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        JSONObject response = new JSONObject();
        final EncryptionManager encryptionManager = new EncryptionManager();
        final AuthenticationToken authenticationToken = new AuthenticationToken();
        final String authToken = authenticationToken.generateToken();
        long authTokenExpiry = authenticationToken.getNewTokenExpirationMilliseonds();
        if (((String) (isUsernameAvailable(request, databaseConnection).getResponse()).get("USERNAME_AVAILABLE")).equals("true")) {
            switch ((String) (isUsernameAvailable(request, databaseConnection).getResponse()).get("VERIFICATION_STATUS")) {
                case "VERIFIED" -> {
                    if (((String) (isCultureIDAvailable(request, databaseConnection).getResponse()).get("CULTURE_ID_AVAILABLE")).equals("true")) {
                        byte[] salt = encryptionManager.generateSalt();
                        byte[] passwordBytes = encryptionManager.generateSaltedHash((String) request.get("PASSWORD"), salt);
                        final String sql = "insert into logins values (?,?,?,?,?,?,?)";
                        final PreparedStatement statement = databaseConnection.prepareStatement(sql);
                        statement.setNull(1, 0);
                        statement.setString(2, (String) request.get("USERNAME"));
                        statement.setString(3, (String) request.get("CULTURE_ID"));
                        statement.setBytes(4, salt);
                        statement.setBytes(5, passwordBytes);
                        statement.setString(6, authToken);
                        statement.setLong(7, authTokenExpiry);
                        statement.execute();
                        response.put("RESULT", "success");
                        Map<String, String> extraHeaders = new HashMap<>();
                        extraHeaders.put("AUTH_TOKEN", authToken);
                        return new ApiResponseData(response, HttpConstants.STATUS_CREATED, extraHeaders);
                    } else {
                        response.put("ERROR", "Culture ID Already Taken");
                        return new ApiResponseData(response, HttpConstants.STATUS_OK);
                    }
                }
                case "PENDING" -> { //username is pending verification
                    response.put("ERROR", "Username Pending Verification");
                    return new ApiResponseData(response, HttpConstants.STATUS_OK);
                }
                default -> { //username not verified 
                    response.put("ERROR", "Username Has Not Been Verified");
                    return new ApiResponseData(response, HttpConstants.STATUS_OK);
                }
            }
        } else {
            response.put("ERROR", "Account Already Exists");
            return new ApiResponseData(response, HttpConstants.STATUS_OK);
        }
    }

    protected ApiResponseData sendUsernameVerification(JSONObject request, Connection databaseConnection) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        JSONObject response = new JSONObject();
        //generate a random verification code
        final EncryptionManager encryptionManager = new EncryptionManager();
        final String charcterSet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final SecureRandom secureRandom = new SecureRandom();
        final StringBuilder stringBuilder = new StringBuilder(8);
        for (int i = 0; i < 8; i++) {
            stringBuilder.append(charcterSet.charAt(secureRandom.nextInt(charcterSet.length())));
        }
        String verificationKey = stringBuilder.toString();
        byte[] salt = encryptionManager.generateSalt();
        byte[] verificationKeyHashBytes = encryptionManager.generateSaltedHash(verificationKey, salt);
        //load data into object for email use later 
        request.put("subject", "Earth Machine Email Verification");
        request.put("message", "Your Verification Code Is: \"" + verificationKey + "\"");
        request.put("email", (String) request.get("USERNAME"));
        //database stuff
        final String sql = "select ID, verified from emailverificationrequests where email = ?";
        final PreparedStatement statement = databaseConnection.prepareStatement(sql);
        statement.setString(1, (String) request.get("USERNAME"));
        final ResultSet results = statement.executeQuery();
        if (results.next()) { //entry for username in database
            if (results.getString("verified").equals("T")) {
                response.put("ERROR", "Username Already Verified");
                return new ApiResponseData(response, HttpConstants.STATUS_OK);
            } else { //update with new verification code 
                final String sql2 = "update emailverification set salt = ?, hash = ? where ID = ?";
                final PreparedStatement statement2 = databaseConnection.prepareStatement(sql2);
                statement2.setBytes(1, salt);
                statement2.setBytes(2, verificationKeyHashBytes);
                statement2.setInt(3, results.getInt("ID"));
                statement2.execute();
                new Thread(new EmailText(request)).start();
                response.put("RESULT", "success");
                return new ApiResponseData(response, HttpConstants.STATUS_CREATED);
            }
        } else { //no entry for username in database yet, add one  
            final String sql3 = "insert into emailverificationrequests values (?,?,?,?,?)";
            final PreparedStatement statement3 = databaseConnection.prepareStatement(sql3);
            statement3.setNull(1, 0);
            statement3.setString(2, (String) request.get("USERNAME"));
            statement3.setBytes(3, salt);
            statement3.setBytes(4, verificationKeyHashBytes);
            statement3.setString(5, "F");
            statement3.execute();
            new Thread(new EmailText(request)).start();
            response.put("RESULT", "success");
            return new ApiResponseData(response, HttpConstants.STATUS_CREATED);
        }
    }

    //DELETE
    //HELPER FUNCTIONS 
}
