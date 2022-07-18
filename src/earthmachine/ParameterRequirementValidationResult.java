/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

/**
 *
 * @author Christopher Brett
 */
public class ParameterRequirementValidationResult {
    
    private final int result;
    private final String reason;
    
    protected ParameterRequirementValidationResult(int result, String reason){
        this.result = result;
        this.reason = reason;
    }
    
    protected int getResult(){
        return this.result;
    }
    
    protected String getReason(){
        return this.reason;
    }
}
