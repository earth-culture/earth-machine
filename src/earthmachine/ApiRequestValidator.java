/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import com.sun.net.httpserver.Headers;
import java.util.concurrent.ConcurrentHashMap;
import org.json.simple.JSONObject;

/**
 *
 * @author Christopher Brett
 */
public class ApiRequestValidator {

    private final ConcurrentHashMap<Integer, ApiRequirements> apiRequirementsCache = new ConcurrentHashMap<>();

    protected ApiRequestValidationResult validateApiRequest(int apiIdentifier, JSONObject queryJSON, JSONObject bodyJSON, Headers headers) {
        checkAndLoadApiRequirements(apiIdentifier);
        ApiRequirements requirements = apiRequirementsCache.get(apiIdentifier);
        for (ParameterRequirement parameterRequirement : requirements.getParameterRequirements()) {
            switch (parameterRequirement.getParameterLocation()) {
                case "query" -> {
                    if (queryJSON.containsKey(parameterRequirement.getIdentifier())) {
                        ParameterRequirementValidationResult result = parameterRequirement.validateParameter(queryJSON.get(parameterRequirement.getIdentifier()));
                        if (result.getResult() != ParameterRequirement.PASSED_REQUIREMENTS) {
                            return new ApiRequestValidationResult(ApiRequestValidationResult.FAILED, result.getReason());
                        }
                    } else if (!parameterRequirement.isOptional()) {
                        return new ApiRequestValidationResult(ApiRequestValidationResult.FAILED, "Missing Parameter In Query: " + parameterRequirement.getIdentifier());
                    }
                }
                case "body" -> {
                    if (bodyJSON.containsKey(parameterRequirement.getIdentifier())) {
                        ParameterRequirementValidationResult result = parameterRequirement.validateParameter(bodyJSON.get(parameterRequirement.getIdentifier()));
                        if (result.getResult() != ParameterRequirement.PASSED_REQUIREMENTS) {
                            return new ApiRequestValidationResult(ApiRequestValidationResult.FAILED, result.getReason());
                        }
                    } else if (!parameterRequirement.isOptional()) {
                        return new ApiRequestValidationResult(ApiRequestValidationResult.FAILED, "Missing Parameter In Body: " + parameterRequirement.getIdentifier());
                    }
                }
                case "header" -> {
                    if (headers.containsKey(parameterRequirement.getIdentifier())) {
                        ParameterRequirementValidationResult result = parameterRequirement.validateParameter(headers.getFirst(parameterRequirement.getIdentifier()));
                        if (result.getResult() != ParameterRequirement.PASSED_REQUIREMENTS) {
                            return new ApiRequestValidationResult(ApiRequestValidationResult.FAILED, result.getReason());
                        }
                    } else if (!parameterRequirement.isOptional()) {
                        return new ApiRequestValidationResult(ApiRequestValidationResult.FAILED, "Missing Parameter In Header: " + parameterRequirement.getIdentifier());
                    }
                }
            }
        }
        return new ApiRequestValidationResult(ApiRequestValidationResult.PASSED, ApiRequestValidationResult.NO_REASON);
    }

    private void checkAndLoadApiRequirements(int apiIdentifier) {
        if (apiRequirementsCache.get(apiIdentifier) == null) {
            ApiRequirements newApiRequirements = new ApiRequirements(SupportedAPIs.getApiRequirementsFile(apiIdentifier));
            apiRequirementsCache.put(apiIdentifier, newApiRequirements);
        }//else it has already been loaded 
    }
}
