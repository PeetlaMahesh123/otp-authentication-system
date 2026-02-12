package com.example.otp.web;

import com.example.otp.model.OtpChannel;
import com.example.otp.service.OtpService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/otp")
@Validated
public class OtpController {
    private final OtpService otpService;

    public OtpController(OtpService otpService) {
        this.otpService = otpService;
    }

    public static class RequestOtpBody {
        @NotBlank
        public String channel;
        @NotBlank
        public String identifier;
    }

    public static class VerifyOtpBody {
        @NotBlank
        public String channel;
        @NotBlank
        public String identifier;
        @NotBlank
        @Pattern(regexp = "\\d{4,8}")
        public String code;
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestOtp(@RequestBody RequestOtpBody body) {
        OtpChannel ch = parseChannel(body.channel);
        try {
            otpService.generateAndSend(body.identifier, ch);
            return ResponseEntity.ok(Map.of("status", "ok", "message", "OTP sent"));
        } catch (IllegalStateException ex) {
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", ex.getMessage()));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyOtpBody body) {
        OtpChannel ch = parseChannel(body.channel);
        boolean ok = otpService.verify(body.identifier, ch, body.code);
        if (ok) {
            return ResponseEntity.ok(Map.of("status", "ok", "message", "Authenticated"));
        }
        return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Invalid or expired code"));
    }

    private OtpChannel parseChannel(String channel) {
        if (channel == null) throw new IllegalArgumentException("channel required");
        String c = channel.trim().toLowerCase();
        if (c.equals("email")) return OtpChannel.EMAIL;
        if (c.equals("sms") || c.equals("phone")) return OtpChannel.SMS;
        throw new IllegalArgumentException("channel must be 'email' or 'sms'");
    }
}
