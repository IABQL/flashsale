package com.iabql.flashsale.utils;

import org.springframework.util.DigestUtils;

public class MD5Util {
    public static final String salt = "1a2b3c4d";

    public static String md5(String key){
        return DigestUtils.md5DigestAsHex(key.getBytes());
    }

    public static String inputPasswordToFromPass(String inputPass){
        String str = "" + salt.charAt(0) + salt.charAt(2)+inputPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String fromPassToDBPass(String fromPass,String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2)+fromPass+salt.charAt(5)+salt.charAt(4);
        return md5(str);
    }

    public static String inputPassToDBPass(String inputPass,String salt){
        String fromPass = inputPasswordToFromPass(inputPass);
        String s = fromPassToDBPass(fromPass, salt);
        return s;
    }

    public static void main(String[] args) {
        System.out.println(fromPassToDBPass("d3b1294a61a07da9b49b6e22b2cbd7f9","1a2b3c4d"));
        System.out.println(inputPassToDBPass("123456","1a2b3c4d"));
    }
}
