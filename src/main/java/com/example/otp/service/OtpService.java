package com.example.otp.service;

import com.example.otp.model.OtpChannel;
import com.example.otp.model.OtpToken;
import com.example.otp.notify.EmailSender;
import com.example.otp.notify.SmsSender;
import com.example.otp.repo.OtpTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Optional;

@Service
public class OtpService {
    private final OtpTokenRepository repository;
    private final EmailSender emailSender;
    private final SmsSender smsSender;
    private final SecureRandom random = new SecureRandom();

    @Value("${otp.length:6}")
    private int otpLength;

    @Value("${otp.ttl-seconds:300}")
    private int ttlSeconds;

    @Value("${otp.rate-limit-seconds:60}")
    private int rateLimitSeconds;

    public OtpService(OtpTokenRepository repository, EmailSender emailSender, SmsSender smsSender) {
        this.repository = repository;
        this.emailSender = emailSender;
        this.smsSender = smsSender;
    }

    @Transactional
    public OtpToken generateAndSend(String contact, OtpChannel channel) {
        Instant now = Instant.now();
        long recentCount = repository.countByContactAndChannelAndCreatedAtAfter(
                contact, channel, now.minusSeconds(rateLimitSeconds));
        if (recentCount > 0) {
            throw new IllegalStateException("OTP recently requested. Please wait before requesting again.");
        }
        String code = generateCode(otpLength);
        OtpToken token = new OtpToken();
        token.setContact(contact);
        token.setChannel(channel);
        token.setCode(code);
        token.setCreatedAt(now);
        token.setExpiresAt(now.plusSeconds(ttlSeconds));
        token.setConsumed(false);
        repository.save(token);
        System.out.println("Generated OTP for " + channel + " " + contact + ": " + code);
        if (channel == OtpChannel.EMAIL) {
            emailSender.sendOtp(contact, code);
        } else if (channel == OtpChannel.SMS) {
            smsSender.sendOtp(contact, code);
        }
        return token;
    }

    @Transactional
    public boolean verify(String contact, OtpChannel channel, String code) {
        Optional<OtpToken> latestOpt = repository.findTopByContactAndChannelOrderByCreatedAtDesc(contact, channel);
        if (latestOpt.isEmpty()) return false;
        OtpToken latest = latestOpt.get();
        Instant now = Instant.now();
        if (latest.isConsumed()) return false;
        if (latest.getExpiresAt().isBefore(now)) return false;
        if (!latest.getCode().equals(code)) return false;
        latest.setConsumed(true);
        repository.save(latest);
        return true;
    }

    private String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
