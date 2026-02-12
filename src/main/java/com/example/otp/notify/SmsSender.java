package com.example.otp.notify;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class SmsSender {
    @Value("${otp.sms.enabled:false}")
    private boolean enabled;

    @Value("${otp.sms.from:+10000000000}")
    private String fromNumber;

    @Value("${otp.sms.provider:log}")
    private String provider;

    @Value("${otp.twilio.accountSid:}")
    private String twilioAccountSid;

    @Value("${otp.twilio.authToken:}")
    private String twilioAuthToken;

    public void sendOtp(String toPhone, String code) {
        if (!enabled) {
            System.out.println("SMS disabled. Pretending to send OTP " + code + " to " + toPhone);
            return;
        }
        if ("twilio".equalsIgnoreCase(provider)) {
            sendViaTwilio(toPhone, code);
        } else {
            System.out.println("Sending SMS from " + fromNumber + " to " + toPhone + ": Your OTP code is " + code);
        }
    }

    private void sendViaTwilio(String toPhone, String code) {
        try {
            String bodyText = "Your OTP code is " + code + " . It expires in 5 minutes.";
            String form = "To=" + URLEncoder.encode(toPhone, StandardCharsets.UTF_8)
                    + "&From=" + URLEncoder.encode(fromNumber, StandardCharsets.UTF_8)
                    + "&Body=" + URLEncoder.encode(bodyText, StandardCharsets.UTF_8);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.twilio.com/2010-04-01/Accounts/" + twilioAccountSid + "/Messages.json"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString(
                            (twilioAccountSid + ":" + twilioAuthToken).getBytes(StandardCharsets.UTF_8)))
                    .POST(HttpRequest.BodyPublishers.ofString(form))
                    .build();
            HttpResponse<String> response = HttpClient.newHttpClient()
                    .send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("Twilio SMS sent to " + toPhone);
            } else {
                System.out.println("Twilio SMS failed: HTTP " + response.statusCode() + " " + response.body());
            }
        } catch (Exception ex) {
            System.out.println("Twilio SMS failed: " + ex.getMessage());
        }
    }
}
