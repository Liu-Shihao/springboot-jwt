package com.lsh.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;


import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT令牌工具类
 * 用于生成和验证JWT令牌
 */
@Component
public class JwtTokenUtil {

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.issuer}")
    private String issuer;

    @Autowired
    private RSAKeyProvider rsaKeyProvider;

    @PostConstruct
    public void init() throws Exception {
        rsaKeyProvider.init();
    }

    /**
     * 从令牌中获取用户名
     *
     * @param token 令牌
     * @return 用户名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * 从令牌中获取过期时间
     *
     * @param token 令牌
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * 从令牌中获取指定的声明
     *
     * @param token          令牌
     * @param claimsResolver 声明解析器
     * @return 声明
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * 从令牌中获取所有声明
     *
     * @param token 令牌
     * @return 所有声明
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(rsaKeyProvider.getPublicKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 检查令牌是否已过期
     *
     * @param token 令牌
     * @return 是否已过期
     */
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }


    public String generateToken(Map<String, Object> claims, String service) {
        return doGenerateToken(claims, service);
    }

    /**
     * 生成令牌
     *
     * @param claims  声明
     * @param subject 主题
     * @return 令牌
     */
    private String doGenerateToken(Map<String, Object> claims, String subject) {
        final Date createdDate = new Date();
        final Date expirationDate = new Date(createdDate.getTime() + expiration);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuer(issuer)
                .setIssuedAt(createdDate)
                .setExpiration(expirationDate)
                .signWith(rsaKeyProvider.getPrivateKey(), SignatureAlgorithm.RS256)
                .compact();
    }


    public Boolean validateToken(String token, String service) {
        final String username = getUsernameFromToken(token);
        return (username.equals(service) && !isTokenExpired(token));
    }

    public static void main(String[] args) {
        JwtTokenUtil jwtTokenUtil = new JwtTokenUtil();
        Map<String, Object> claims = Map.of("cilent_id", "account-transfers-ms");

        String token = jwtTokenUtil.generateToken(claims, "account-transfers-ms");
        System.out.println(token);
        System.out.println(jwtTokenUtil.validateToken(token,"account-transfers-ms"));


    }


}