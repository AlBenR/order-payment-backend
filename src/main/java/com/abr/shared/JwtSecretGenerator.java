package com.abr.shared;

import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Base64;

public class JwtSecretGenerator {

    public static void main(String[] args) {
        byte[] key = Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded();
        String base64Key = Base64.getEncoder().encodeToString(key);

        System.out.println("JWT SECRET (Base64):");
        System.out.println(base64Key);
    }
}

