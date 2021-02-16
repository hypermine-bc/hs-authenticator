package io.hypermine.hypersign.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.MalformedJwtException;
import org.junit.Test;

import static org.junit.Assert.*;

public class JWTManagerTest {

    @Test
    public void createJWT() {
        String payLoad = "1234-3434-34343";
        int jwtTimeToLive = 800000;

        String jwt = JWTManager.createJWT(
                jwtTimeToLive,
                payLoad
        );
        Claims claims = JWTManager.decodeJWT(jwt);
        assertEquals("1234-3434-34343", claims.get("GUID"));
    }


    @Test(expected = MalformedJwtException.class)
    public void decodeShouldFail() {

        String notAJwt = "This is not a JWT";

        Claims claims = JWTManager.decodeJWT(notAJwt);

    }
}