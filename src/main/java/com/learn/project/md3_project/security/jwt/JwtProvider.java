package com.learn.project.md3_project.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
@Slf4j
public class JwtProvider {
    @Value("${jwt.secret-key}")
    private String secretKey;
    @Value("${jwt.expired}")
    private long expired;
    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
    }

    public String generateAccessToken(String username){
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expired))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    public String generateRefreshToken(String username){
        Date now = new Date();
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime()+expired*7))
                .signWith(key(), SignatureAlgorithm.HS256)
                .compact();
    }
    // xác minh token
    public boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(key()).build().parse(token);
            return true;
        }catch (MalformedJwtException e){
            log.error("Invalid token ",e.getMessage());
        }catch (UnsupportedJwtException e){
            log.error("Unsupported token ",e.getMessage());
        }catch (IllegalArgumentException e){
            log.error("Jwt key string invalid ",e.getMessage());
        }catch (ExpiredJwtException e) {
            log.error("Token has expired", e.getMessage());
            throw e;
        }
        return false;
    }
    // giải mã token
    public String getUserNameFromToken(String token){
        return Jwts.parser().setSigningKey(key()).build().parseClaimsJws(token).getBody().getSubject();
    }
}
