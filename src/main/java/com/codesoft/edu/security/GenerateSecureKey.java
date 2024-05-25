package com.codesoft.edu.security;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Base64;

public class GenerateSecureKey {
    public static void main(String[] args) {
        // Generate a secure key for HS512
        SecretKey secureKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);

        // Display the base64-encoded key
        String base64Key = Base64.getEncoder().encodeToString(secureKey.getEncoded());
        System.out.println("Secure Key (Base64): " + base64Key);
    }
}
