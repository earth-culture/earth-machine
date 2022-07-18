/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import java.io.File;

/**
 *
 * @author Christopher Brett
 */
public class SupportedAPIs {

    private static final String FILE_PATH = "API_REQUIREMENTS/";
    protected static final int VERIFY_EMAIL_IS_AVAILABLE = 1;
    protected static final int VERIFY_CULTURE_ID_IS_AVAILABLE = 2;
    protected static final int VALIDATE_LOGIN = 3;
    protected static final int VALIDATE_USERNAME_VERIFICATION_KEY = 4;
    protected static final int CREATE_ACCOUNT = 5;
    protected static final int REGISTER_USERNAME_FOR_VERIFICATION = 6;

    protected static File getApiRequirementsFile(int API_ID) {
        switch (API_ID) {
            case VERIFY_EMAIL_IS_AVAILABLE -> {
                return new File(FILE_PATH + "verifyEmailIsAvailable.txt");
            }
            case VERIFY_CULTURE_ID_IS_AVAILABLE -> {
                return new File(FILE_PATH + "verifyCultureIDIsAvailable.txt");
            }
            case VALIDATE_LOGIN -> {
                return new File(FILE_PATH + "validateLogin.txt");
            }
            case VALIDATE_USERNAME_VERIFICATION_KEY -> {
                return new File(FILE_PATH + "validateUsernameVerificationKey.txt");
            }
            case CREATE_ACCOUNT -> {
                return new File(FILE_PATH + "createAccount.txt");
            }
            case REGISTER_USERNAME_FOR_VERIFICATION -> {
                return new File(FILE_PATH + "registerUsernameForVerification.txt");
            }
            default -> {
                return null;
            }
        }
    }

}
