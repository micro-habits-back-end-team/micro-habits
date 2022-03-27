package org.twt.microhabits.service.users;

import org.springframework.util.DigestUtils;

public class md5Encryption {
    public static String md5Encryption(String raw, String salt) {
        return DigestUtils.md5DigestAsHex((raw + salt).getBytes());
    }
}
