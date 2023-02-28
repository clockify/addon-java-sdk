package com.cake.clockify.addonsdk.shared.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

public class JwtUtils {
    private static final String CLAIM_WORKSPACE_ID = "workspaceId";

    public static Jwt<Header, Claims> parseJwtClaims(String jwt, String keyContent) {
        return null; // todo
    }

    public static String parseWorkspaceIdFromJwt(String jwt, String keyContent) {
        return parseJwtClaims(jwt, keyContent).getBody().get(CLAIM_WORKSPACE_ID, String.class);
    }

    public static Jwt<Header, Claims> parseJwtClaimsWithoutVerifying(String jwt) {
        int i = jwt.lastIndexOf('.');
        String withoutSignature = jwt.substring(0, i+1);
        return Jwts.parserBuilder().build().parseClaimsJwt(withoutSignature);
    }

    public static String parseWorkspaceIdFromJwtWithoutVerifying(String jwt) {
        return parseJwtClaimsWithoutVerifying(jwt).getBody().get(CLAIM_WORKSPACE_ID, String.class);
    }
}
