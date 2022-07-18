/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachinev2;

import java.util.Map;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Brett
 */
public class ApiResponseData {
    private final JSONObject response;
    private final int httpStatus;
    private Map<String, String> extraHeaders = null;
    
    protected ApiResponseData(JSONObject response, int httpStatus){
        this.response = response;
        this.httpStatus = httpStatus;
    }
    
    protected ApiResponseData(JSONObject response, int httpStatus, Map<String, String> extraHeaders){
        this.response = response;
        this.httpStatus = httpStatus;
        this.extraHeaders = extraHeaders;
    }
    
    protected JSONObject getResponse(){
        return this.response;
    }
    
    protected int getHttpStatus(){
        return this.httpStatus;
    }
    
    protected Map<String, String> getExtraHeaders(){
        return this.extraHeaders;
    }
}
