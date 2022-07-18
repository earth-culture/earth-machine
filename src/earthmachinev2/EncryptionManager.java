/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package earthmachinev2;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 *
 * @author Christopher Brett
 */
public class EncryptionManager {
    
    private final String hashAlgorithm = "PBKDF2WithHmacSHA1";
    private final String saltAlgorithm = "SHA1PRNG";
    private final int derivedKeyLength = 160; //bits
    private final int iterationsToPerformHashingAlgorithm = 20000;
    
    /*
    Encypt the plain-text password using the same salt used to encrypt the password 
    from the user originaly then compare the salted hashes, if they match then the
    entered password is correct 
    */
    
    protected boolean authenticate(String enteredPassword, byte[] saltedHashOfCorrectPassword, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        byte[] saltedHashOfEnteredPassword = generateSaltedHash(enteredPassword, salt);
        return Arrays.equals(saltedHashOfEnteredPassword, saltedHashOfCorrectPassword);
    }
    
    protected byte[] generateSaltedHash(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException{
        KeySpec keySpec = new PBEKeySpec(password.toCharArray(), salt, iterationsToPerformHashingAlgorithm, derivedKeyLength);
        SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(hashAlgorithm);
        return secretKeyFactory.generateSecret(keySpec).getEncoded();
    }
    
    //Very important to use SecureRandom instead of generic Random, for security  
    
    protected byte[] generateSalt() throws NoSuchAlgorithmException {
        SecureRandom secureRandom = SecureRandom.getInstance(saltAlgorithm);
        byte[] salt = new byte[8];
        secureRandom.nextBytes(salt);
        return salt;
    }
    
}

