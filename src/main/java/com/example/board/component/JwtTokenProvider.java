package com.example.board.component;

import com.example.board.domain.User;
import com.example.board.dto.response.ResponseLoginDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
public class JwtTokenProvider {
    private final RedisTemplate<String, String> redisTemplate;
    private final String secretKey;
    private final long accessTokenValidity;
    private final long refreshTokenValidity;

    public ResponseLoginDto generateToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user.getUsername());

        return ResponseLoginDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .role(user.getRole())
                .message("로그인이 완료되었습니다.")
                .build();
    }

    private String generateAccessToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("id", user.getId())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + accessTokenValidity))
                .signWith(getSigningKey())
                .compact();
    }

    private String generateRefreshToken(String username) {
        String refreshToken = Jwts.builder()
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshTokenValidity))
                .signWith(getSigningKey())
                .compact();

        redisTemplate.opsForValue().set(
                "RT:" + username,
                refreshToken,
                refreshTokenValidity,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get("role") == null) {
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }


        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get("role").toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        User user = User.builder()
                .id(Long.valueOf(claims.get("id").toString()))
                .email(claims.getSubject())
                .role(claims.get("role").toString())
                .build();

        return new UsernamePasswordAuthenticationToken(user, "", authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("만료된 토큰입니다.");
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Date getExpirationFromToken(String token) {
        try {
            return parseClaims(token)
                    .getExpiration();
        } catch (ExpiredJwtException e) {
            return e.getClaims().getExpiration();
        } catch (Exception e) {
            throw new RuntimeException("토큰 만료시간을 가져오는데 실패했습니다.", e);
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token: " + e.getMessage());
            return false;
        } catch (io.jsonwebtoken.security.SecurityException | io.jsonwebtoken.MalformedJwtException e) {
            log.error("Invalid JWT token: " + e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("JWT token validation error: " + e.getMessage());
            return false;
        }
    }
}
