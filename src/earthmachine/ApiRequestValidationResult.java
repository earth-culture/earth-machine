/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Brett
 */
public class ApiRequestValidationResult {
    
    protected static final boolean PASSED = true;
    protected static final boolean FAILED = false;
    protected static final String NO_REASON = "NO REASON";
    private final boolean passed;
    private  final String reason;
    
    protected ApiRequestValidationResult(boolean passed, String reason){
        this.passed = passed;
        this.reason = reason;
    }
    
    protected boolean passedApiValidation(){
        return this.passed;
    }
    
    protected JSONObject getReason(){
        JSONObject reasonJSON = new JSONObject();
        reasonJSON.put("ERROR", reason);
        return reasonJSON;
    }
}
