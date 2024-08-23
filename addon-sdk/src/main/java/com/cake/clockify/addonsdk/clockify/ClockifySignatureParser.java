package com.cake.clockify.addonsdk.clockify;

import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;

public class ClockifySignatureParser {
    public static final String CLAIM_TYPE = "type";
    public static final String CLAIM_BACKEND_URL = "backendUrl";
    public static final String CLAIM_PTO_URL = "ptoUrl";
    public static final String CLAIM_REPORTS_URL = "reportsUrl";
    public static final String CLAIM_WORKSPACE_ID = "workspaceId";
    public static final String CLAIM_ADDON_ID = "addonId";
    public static final String CLAIM_USER_ID = "user";
    public static final String CLAIM_WORKSPACE_ROLE = "workspaceRole";

    public static final String ISSUER = "clockify";
    public static final String ADDON = "addon";
    private final JwtParser parser;

    /**
     * @param addonKey  the key declared inside the addon manifest
     * @param publicKey the RSA256 public key
     */
    public ClockifySignatureParser(String addonKey, RSAPublicKey publicKey) {
        this.parser = Jwts.parserBuilder()
                .requireIssuer(ISSUER)
                .requireSubject(addonKey)
                .require(CLAIM_TYPE, ADDON)
                .setSigningKey(publicKey)
                .build();
    }

    public Map<String, Object> parseClaims(String token) {
        return parser.parseClaimsJws(token).getBody();
    }
}
