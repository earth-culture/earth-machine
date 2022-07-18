/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachinev2;

import java.util.concurrent.ConcurrentHashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Brett
 */
public class ApiRequestValidator {
    
    private final ConcurrentHashMap<Integer, ApiRequirements> apiRequirementsCache = new ConcurrentHashMap<>();
    
    protected ApiRequestValidationResult validateApiRequest(int apiIdentifier, JSONObject json){
        checkAndLoadApiRequirements(apiIdentifier);
        ApiRequirements requirements = apiRequirementsCache.get(apiIdentifier);
        for(ParameterRequirement parameterRequirement : requirements.getParameterRequirements()){
            if(json.containsKey(parameterRequirement.getIdentifier())){
                ParameterRequirementValidationResult result = parameterRequirement.validateParameter(json.get(parameterRequirement.getIdentifier()));
                if(result.getResult() != ParameterRequirement.PASSED_REQUIREMENTS){
                    return new ApiRequestValidationResult(ApiRequestValidationResult.FAILED, result.getReason());
                }
            }else if(!parameterRequirement.isOptional()){
                return new ApiRequestValidationResult(ApiRequestValidationResult.FAILED, "Missing Parameter: " + parameterRequirement.getIdentifier());
            }
        }
        return new ApiRequestValidationResult(ApiRequestValidationResult.PASSED, ApiRequestValidationResult.NO_REASON);
    }
    
    private void checkAndLoadApiRequirements(int apiIdentifier){
        if(apiRequirementsCache.get(apiIdentifier) == null){
            ApiRequirements newApiRequirements = new ApiRequirements(SupportedAPIs.getApiRequirementsFile(apiIdentifier));
            apiRequirementsCache.put(apiIdentifier, newApiRequirements);
        }//else it has already been loaded 
    }   
}
