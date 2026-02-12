package com.example.otp.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "otp_tokens", indexes = {
        @Index(name = "idx_contact_channel", columnList = "contact,channel"),
        @Index(name = "idx_expires", columnList = "expiresAt")
})
public class OtpToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String contact;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OtpChannel channel;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant expiresAt;

    @Column(nullable = false)
    private boolean consumed;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public OtpChannel getChannel() { return channel; }
    public void setChannel(OtpChannel channel) { this.channel = channel; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getExpiresAt() { return expiresAt; }
    public void setExpiresAt(Instant expiresAt) { this.expiresAt = expiresAt; }

    public boolean isConsumed() { return consumed; }
    public void setConsumed(boolean consumed) { this.consumed = consumed; }
}
