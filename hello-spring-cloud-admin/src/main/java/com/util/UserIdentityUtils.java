package com.util;

import com.tunnelkey.tktim.api.model.UserIdentity;
import io.jsonwebtoken.Claims;

import java.util.Optional;
import java.util.UUID;

/**
 * @version:（1.0.0.0）
 * @Description: （对类进行功能描述）
 * @author: 刘毅
 * @date: 2019/3/25 9:39
 */
public class UserIdentityUtils {

    public static UserIdentity parse(String token) {
        Claims chaim = JWTUtil.parseJWT(token);
        UserIdentity ident = new UserIdentity();
        ident.setUserId(UUID.fromString(chaim.get("UserId").toString()));
        if (Optional.ofNullable(chaim.get("CompanyId")).isPresent()) {
            ident.setCompanyId(UUID.fromString(chaim.get("CompanyId").toString()));
        }
        ident.setRoleId(Integer.parseInt(chaim.get("Role").toString()));
        return ident;
    }
}
