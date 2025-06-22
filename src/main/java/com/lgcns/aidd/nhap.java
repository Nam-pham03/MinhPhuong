package com.lgcns.aidd;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class nhap {
    public static void main(String[] args) {
        String encoded = new BCryptPasswordEncoder().encode("password456");
        System.out.println(encoded);
    }
}
