package com.service;

import org.springframework.stereotype.Component;

@Component
public class AdminServiceHystrix implements AdminService {
    @Override
    public String sayHi(String message) {
        return String.format("Hi SpringCloud Message:%s but request bad",message);
    }
}
