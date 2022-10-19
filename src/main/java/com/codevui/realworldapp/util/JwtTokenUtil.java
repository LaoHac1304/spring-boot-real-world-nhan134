package com.codevui.realworldapp.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.stereotype.Component;

import com.codevui.realworldapp.entity.User;
import com.codevui.realworldapp.model.user.TokenPayload;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtTokenUtil {

    private String secret = "QUAN_DZ";

    public String generateToken(User user, Long expiredDate) {

        Map<String, Object> claims = new HashMap<>();
        TokenPayload tokenPayload = TokenPayload.builder().userID(user.getId()).email(user.getEmail()).build();
        claims.put("payload", tokenPayload);
        String token = Jwts.builder().setClaims(claims).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiredDate * 1000))
                .signWith(SignatureAlgorithm.HS512, secret).compact();
        return token;
    }

    public TokenPayload getTokenPayLoad(String token) {
        return getClaimsFromToken(token, (Claims claim) -> {
            Map<String, Object> mapResult = (Map<String, Object>) claim.get("payload");
            return TokenPayload.builder().userID((int) mapResult.get("userID"))
                    .email((String) mapResult.get("email")).build();
        });
    }

    private <T> T getClaimsFromToken(String token, Function<Claims, T> claimResolver) {
        final Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claimResolver.apply(claims);
    }

    public boolean vailidate(String token, User user) {
        TokenPayload tokenPayload = getTokenPayLoad(token);
        return tokenPayload.getUserID() == user.getId() &&
                 tokenPayload.getEmail().equals(user.getEmail()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expDate = getClaimsFromToken(token, Claims::getExpiration  );
        return expDate.before(new Date());
    }
}
