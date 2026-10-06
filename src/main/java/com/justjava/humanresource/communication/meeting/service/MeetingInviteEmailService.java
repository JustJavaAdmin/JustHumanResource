package com.justjava.humanresource.communication.meeting.service;

import com.justjava.humanresource.communication.meeting.entity.HrMeeting;
import com.justjava.humanresource.communication.meeting.entity.HrMeetingParticipant;
// ...existing imports...
import com.justjava.humanresource.communication.meeting.repository.MeetingEmailDeliveryRepository;
import com.justjava.humanresource.communication.meeting.entity.MeetingEmailDelivery;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingInviteEmailService {

    private final MeetingInviteMessageBuilder messageBuilder;
    private final MeetingEmailDeliveryRepository deliveryRepository;

    /**
     * Schedule a meeting invite email for background delivery. This method will not throw on delivery
     * failures — it persists a delivery record and returns immediately so calling code may continue.
     */
    @Transactional
    public String sendInvite(HrMeeting meeting, HrMeetingParticipant participant) {
        String email = participant.getEmployeeEmail();
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Participant has no email address.");
        }

        MeetingEmailDelivery delivery = new MeetingEmailDelivery();
        delivery.setMeetingId(meeting.getId());
        delivery.setParticipantId(participant.getId());
        delivery.setEmail(email.trim());
        delivery.setSubject("Meeting invite: " + meeting.getSubject());
        delivery.setHtmlBody(messageBuilder.html(meeting));
        delivery.setTextBody(messageBuilder.text(meeting));
        // initial attempt scheduled immediately
        delivery.setAttempts(0);
        delivery.setNextAttemptAt(java.time.LocalDateTime.now());
        delivery = deliveryRepository.save(delivery);
        // return the delivery id so callers can reference it if required
        return delivery.getId();
    }
}
