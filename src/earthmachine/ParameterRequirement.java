/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

/**
 *
 * @author Christopher Brett
 */
public class ParameterRequirement {

    protected static final int PASSED_REQUIREMENTS = 0;
    protected static final int DATA_TYPE_INCORRECT = 1;
    protected static final int DATA_FORMAT_INCORRECT = 2;
    protected static final int DATA_EXCEEDS_ALLOWED_SIZE = 3;
    protected static final int DATA_SUBCEEDS_ALLOWED_SIZE = 4;

    private final String parameterIdentifier;
    private final String parameterDataType; //String? Int? File Type?
    private final String parameterFormat; //Email? Password? etc 
    private final long parameterMaxSize; //dependent on datatype: bytes? characters? int size?
    private final long parameterMinSize; //dependent on datatype: bytes? characters? int size?
    private final boolean parameterOptional; //if identifier needs to be present in request 
    private final String parameterLocation; //in query? in header?
    
    protected ParameterRequirement(String identifier, String dataType, String format, long maxSize, long minSize, boolean optional, String location) {
        this.parameterIdentifier = identifier;
        this.parameterDataType = dataType;
        this.parameterFormat = format;
        this.parameterMaxSize = maxSize;
        this.parameterMinSize = minSize;
        this.parameterOptional = optional;
        this.parameterLocation = location;
    }

    protected String getIdentifier() {
        return this.parameterIdentifier;
    }

    protected boolean isOptional() {
        return this.parameterOptional;
    }
    
    protected String getParameterLocation() {
        return this.parameterLocation;
    }

    protected ParameterRequirementValidationResult validateParameter(Object parameter) {
        if (!parameterDataType.equals(ParameterDataType.NOT_SET)) {
            if (!dataTypeCheckPassed(parameter)) {
                return new ParameterRequirementValidationResult(DATA_TYPE_INCORRECT, "Parameter: (" + parameterIdentifier + ") Must Be Of Type: " + parameterDataType);
            }
        }
        if (!parameterFormat.equals(ParameterFormat.NOT_SET)) {
            if (!formatCheckPassed(parameter)) {
                return new ParameterRequirementValidationResult(DATA_FORMAT_INCORRECT, "Parameter: (" + parameterIdentifier + ") Must Be Formatted As: " + parameterFormat);
            }
        }
        if (parameterMaxSize != ParameterSize.NOT_SET) {
            if (!maxSizeCheckPassed(parameter)) {
                return new ParameterRequirementValidationResult(DATA_EXCEEDS_ALLOWED_SIZE, "Parameter: (" + parameterIdentifier + ") Is Larger Than Max Size Of: " + parameterMaxSize);
            }
        }
        if (parameterMinSize != ParameterSize.NOT_SET) {
            if (!minSizeCheckPassed(parameter)) {
                return new ParameterRequirementValidationResult(DATA_SUBCEEDS_ALLOWED_SIZE, "Parameter: (" + parameterIdentifier + ") Is Larger Than Min Size Of: " + parameterMinSize);
            }
        }
        return new ParameterRequirementValidationResult(PASSED_REQUIREMENTS, "");
    }

    private boolean dataTypeCheckPassed(Object parameter) {
        switch (parameterDataType) {
            case ParameterDataType.STRING -> {
                return parameter instanceof String;
            }
            case ParameterDataType.INTEGER -> {
                return parameter instanceof Integer;
            }
            default -> {
                return true;
            }
        }
    }

    private boolean formatCheckPassed(Object parameter) {
        switch (parameterDataType) {
            case ParameterDataType.STRING -> {
                String parameterString = (String) parameter;
                switch (parameterFormat) {
                    case ParameterFormat.EMAIL -> {
                        return isEmailFormat(parameterString);
                    }
                    case ParameterFormat.PASSWORD -> {
                        return isPasswordFormat(parameterString);
                    }
                }
            }
        }
        return true;
    }

    private boolean maxSizeCheckPassed(Object parameter) {
        switch (parameterDataType) {
            case ParameterDataType.STRING -> {
                String parameterString = (String) parameter;
                return parameterString.length() <= parameterMaxSize;
            }
            case ParameterDataType.INTEGER -> {
                int parameterInt = (Integer) parameter;
                return parameterInt <= parameterMaxSize;
            }
        }
        return true;
    }

    private boolean minSizeCheckPassed(Object parameter) {
        switch (parameterDataType) {
            case ParameterDataType.STRING -> {
                String parameterString = (String) parameter;
                return parameterString.length() >= parameterMinSize;
            }
            case ParameterDataType.INTEGER -> {
                int parameterInt = (Integer) parameter;
                return parameterInt >= parameterMinSize;
            }
        }
        return true;
    }

    private boolean isEmailFormat(String email) {
        return email.matches("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");
    }
    
    private boolean isPasswordFormat(String password){
        return password.length() >= 8;
    }
}
