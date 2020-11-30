package com.util;

import com.common.Constant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.apache.commons.codec.binary.Base64;

import java.io.UnsupportedEncodingException;
import java.util.Date;

public class JwtUtil {

    //密钥
    public static final String KEY = "123456";

    public static long  ttlMillis = 1800;

    /**
     * 创建jwt
     * @param userId
     * @return String
     * @throws Exception
     */
    public static String createJWT(String userId) {

        byte[] secretKeyBytes = null;
        try {
            secretKeyBytes = new Base64().encodeBase64(KEY.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String compactJws = Jwts.builder()
                .claim("userId", userId)
                .setExpiration(new Date(System.currentTimeMillis() + Constant.TokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKeyBytes).compact();
        return compactJws;
    }

    /**
     * 解密jwt
     *
     * @param jwt
     * @return
     * @throws Exception
     */
    public static Claims parseJWT(String jwt) throws Exception {
        Claims claims = Jwts.parser()  //得到DefaultJwtParser
                            .setSigningKey(new Base64().encodeBase64(KEY.getBytes("utf-8")))  //设置签名的秘钥
                            .parseClaimsJws(jwt) //设置需要解析的jwt
                            .getBody();
        return claims;
    }

}
