package com.example.otp.repo;

import com.example.otp.model.OtpChannel;
import com.example.otp.model.OtpToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.Optional;

public interface OtpTokenRepository extends JpaRepository<OtpToken, Long> {
    @Query("select t from OtpToken t where t.contact = :contact and t.channel = :channel order by t.createdAt desc")
    java.util.List<OtpToken> findLatestByContactAndChannel(String contact, OtpChannel channel);

    Optional<OtpToken> findTopByContactAndChannelOrderByCreatedAtDesc(String contact, OtpChannel channel);

    long countByContactAndChannelAndCreatedAtAfter(String contact, OtpChannel channel, Instant after);
}
