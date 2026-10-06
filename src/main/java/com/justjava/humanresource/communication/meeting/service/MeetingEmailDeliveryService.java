package com.justjava.humanresource.communication.meeting.service;

import com.justjava.humanresource.communication.meeting.entity.MeetingEmailDelivery;
import com.justjava.humanresource.communication.meeting.repository.MeetingEmailDeliveryRepository;
import com.justjava.humanresource.utils.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MeetingEmailDeliveryService {

    private final MeetingEmailDeliveryRepository deliveryRepository;
    private final EmailService emailService;

    // Maximum attempts before giving up
    private final int MAX_ATTEMPTS = 5;

    // Run every minute to process pending deliveries
    @Scheduled(fixedDelayString = "60000")
    @Transactional
    public void processPendingDeliveries() {
        List<MeetingEmailDelivery> pending = deliveryRepository.findBySentAtIsNullAndNextAttemptAtBefore(LocalDateTime.now());
        for (MeetingEmailDelivery d : pending) {
            try {
                String messageId = emailService.sendEmail(d.getEmail(), d.getSubject(), d.getHtmlBody(), d.getTextBody());
                d.setSentAt(LocalDateTime.now());
                d.setLastError(null);
                deliveryRepository.save(d);
                log.info("Meeting invite email delivered (deliveryId={} messageId={})", d.getId(), messageId);
            } catch (RuntimeException ex) {
                d.setAttempts(d.getAttempts() + 1);
                d.setLastError(ex.getMessage());
                if (d.getAttempts() >= MAX_ATTEMPTS) {
                    // stop retrying
                    d.setNextAttemptAt(null);
                    log.warn("Giving up on meeting invite delivery {} after {} attempts: {}", d.getId(), d.getAttempts(), ex.getMessage());
                } else {
                    // exponential backoff: base 60s * 2^(attempts-1)
                    long seconds = 60L * (1L << (d.getAttempts() - 1));
                    d.setNextAttemptAt(LocalDateTime.now().plusSeconds(seconds));
                    log.info("Retrying meeting invite delivery {} (attempt {}): next at {}", d.getId(), d.getAttempts(), d.getNextAttemptAt());
                }
                deliveryRepository.save(d);
            }
        }
    }
}

