/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachinev2;

import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Brett
 */
public class GenericErrorApiResponse {
    
    protected static ApiResponseData databaseError(){
        JSONObject response = new JSONObject();
        response.put("ERROR", "INTERNAL DATABASE ERROR");
        return new ApiResponseData(response, HttpConstants.STATUS_INTERNAL_SERVER_ERROR);
    }
    
    protected static ApiResponseData generalError(){
        JSONObject response = new JSONObject();
        response.put("ERROR", "GENERAL SERVER ERROR");
        return new ApiResponseData(response, HttpConstants.STATUS_INTERNAL_SERVER_ERROR);
    }
    
    protected static ApiResponseData methodNotFoundError(){
        JSONObject response = new JSONObject();
        response.put("ERROR", "API METHOD NOT FOUND");
        return new ApiResponseData(response, HttpConstants.STATUS_NOT_FOUND);
    }
}
