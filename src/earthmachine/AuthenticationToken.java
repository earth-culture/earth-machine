/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachine;

import java.security.SecureRandom;

/**
 *
 * @author Christopher Brett
 */
public class AuthenticationToken {

    private final String tokenAlphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@$%^&*(){}:;+-";
    private final SecureRandom secureRandom = new SecureRandom();

    protected String generateToken() {
        int tokenLength = 100;
        StringBuilder bobTheBuilder = new StringBuilder(tokenLength);
        for (int i = 0; i < tokenLength; i++) {
            bobTheBuilder.append(tokenAlphabet.charAt(secureRandom.nextInt(tokenAlphabet.length())));
        }
        return bobTheBuilder.toString();
    }

    protected static boolean isTokenExpired(long expireTime) {
        return expireTime < System.currentTimeMillis();
    }

    protected long getNewTokenExpirationMilliseonds() {
        return System.currentTimeMillis() + (12 * 60 * 60 * 1000); //12 hours
    }
}

