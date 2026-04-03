package com.learn.project.md3_project.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class OtpService {
    private final Cache<String, String> otpCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(999999) + 1976);
        otpCache.put(email, otp);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String cachedOtp = otpCache.getIfPresent(email);
        if (cachedOtp != null && cachedOtp.equals(otp)) {
            otpCache.invalidate(email);
            return true;
        }
        return false;
    }
}
