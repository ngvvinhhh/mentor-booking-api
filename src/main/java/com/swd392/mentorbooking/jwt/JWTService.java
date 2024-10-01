package com.swd392.mentorbooking.jwt;

import com.swd392.mentorbooking.entity.Account;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.function.Function;

@Component
public class JWTService {
    private static final String SECRET = "!@#$FDGSDFGSGSGSGSHSHSHSSHGFFDSGSFGSSGHSDFSDFSFSFSFSDFSFSFSFDSADSA";

    public String generateToken(String email) {
        Date now = new Date(); // get current time
        long EXPIRATION = 24 * 60 * 60 * 1000;
        Date expirationDate = new Date(now.getTime() + EXPIRATION);

        //Return token
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, SECRET)
                .compact();
    }

    public String generateRefreshToken(String email) {
        Date now = new Date(); // get current time
        long EXPIRATION = 7 * 24 * 60 * 60 * 1000;
        Date expirationDate = new Date(now.getTime() + EXPIRATION);

        //Return token
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS256, SECRET)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try{
            //Return claims from token
            return Jwts.parser()
                    .setSigningKey(SECRET)
                    .parseClaimsJws(token)
                    .getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before((new Date()));
    }

    public Boolean validateToken(String token, Account userDetails){
        final String userName= extractEmail(token);
        return (userName.equals(userDetails.getEmail()) && !isTokenExpired(token));
    }
}
