package com.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.lang.Nullable;

import java.util.*;

public class JWTUtil {

	public static final String key = "123ABCLY";

	/**
	 * @Description:生成token @param userId @param companyId @param
	 * role @return（展示方法参数和返回值） @date: 2018年11月6日.下午4:43:45 @throws
	 */
	public static String token(UUID userId, @Nullable UUID companyId, int role) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
		long nowMillis = System.currentTimeMillis(); // 生成JWT的时间
		Date now = new Date(nowMillis);
		Map<String, Object> claims = new HashMap<String, Object>(); // 创建payload
		claims.put("UserId", userId);
		claims.put("CompanyId", companyId);
		claims.put("Role", role);
		String subject = userId.toString();
		JwtBuilder builder = Jwts.builder().setClaims(claims).setId(UUID.randomUUID().toString()).setIssuedAt(now)
				.setSubject(subject).signWith(signatureAlgorithm, key);
		Calendar rightNow = Calendar.getInstance();
		rightNow.setTime(now);
		rightNow.add(Calendar.HOUR, 4);
		Date dt1 = rightNow.getTime();
		builder.setExpiration(dt1);
		return builder.compact();
	}

	/**
	 * Token的解密
	 *
	 * @param token 加密后的token
	 * @return
	 */
	public static Claims parseJWT(String token) {
		// 签名秘钥，和生成的签名的秘钥一模一样
		// 得到DefaultJwtParser
		Claims claims = Jwts.parser()
				// 设置签名的秘钥
				.setSigningKey(key)
				// 设置需要解析的jwt
				.parseClaimsJws(token).getBody();
		return claims;
	}
}
