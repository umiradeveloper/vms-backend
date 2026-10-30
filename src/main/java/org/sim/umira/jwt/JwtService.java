package org.sim.umira.jwt;

import java.security.Key;
import java.util.Date;
import java.util.List;

import org.eclipse.microprofile.config.ConfigProvider;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class JwtService {
    
     
    /**
     * Sebelumnya key selalu random setiap aplikasi start, sehingga SEMUA token (termasuk
     * token mobile 150 hari) langsung invalid setiap restart/deploy.
     * Sekarang key diambil dari config `vms.jwt.secret` (env: VMS_JWT_SECRET, min 32 karakter).
     * Jika belum di-set, tetap fallback ke key random (perilaku lama).
     */
    private final Key key = buildKey();

    private static Key buildKey() {
        String secret = ConfigProvider.getConfig()
                .getOptionalValue("vms.jwt.secret", String.class)
                .orElse(null);
        if (secret != null && secret.getBytes(java.nio.charset.StandardCharsets.UTF_8).length >= 32) {
            return Keys.hmacShaKeyFor(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        }
        System.out.println("[JwtService] vms.jwt.secret belum di-set (min 32 karakter), memakai key random: token akan invalid setelah restart");
        return Keys.secretKeyFor(SignatureAlgorithm.HS256);
    }

    public String generateToken(String username, List<String> role, Long expirationMillis) {
         long exp = expirationMillis != null
            ? expirationMillis
            : 3600000L; 
        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuer("quarkus-app")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + exp)) // 1 jam
                .signWith(key)
                .compact();
    }

    @SuppressWarnings("unchecked")
    public List<String> getRoles(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Object role = claims.get("role");
        if (role instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        return List.of();
    }

    public String validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
