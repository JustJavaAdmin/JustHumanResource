package com.justjava.humanresource.communication.meeting.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "communication_meeting_email_deliveries")
public class MeetingEmailDelivery {

    @Id
    @Column(length = 36)
    private String id = UUID.randomUUID().toString();

    private Long meetingId;
    private Long participantId;
    private String email;
    private String subject;

    @Lob
    private String htmlBody;

    @Lob
    private String textBody;

    private int attempts;
    private LocalDateTime nextAttemptAt;
    private LocalDateTime sentAt;

    @Lob
    private String lastError;

    private LocalDateTime createdAt = LocalDateTime.now();

    public MeetingEmailDelivery() {}

    // getters and setters
    public String getId() { return id; }
    public Long getMeetingId() { return meetingId; }
    public void setMeetingId(Long meetingId) { this.meetingId = meetingId; }
    public Long getParticipantId() { return participantId; }
    public void setParticipantId(Long participantId) { this.participantId = participantId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getHtmlBody() { return htmlBody; }
    public void setHtmlBody(String htmlBody) { this.htmlBody = htmlBody; }
    public String getTextBody() { return textBody; }
    public void setTextBody(String textBody) { this.textBody = textBody; }
    public int getAttempts() { return attempts; }
    public void setAttempts(int attempts) { this.attempts = attempts; }
    public LocalDateTime getNextAttemptAt() { return nextAttemptAt; }
    public void setNextAttemptAt(LocalDateTime nextAttemptAt) { this.nextAttemptAt = nextAttemptAt; }
    public LocalDateTime getSentAt() { return sentAt; }
    public void setSentAt(LocalDateTime sentAt) { this.sentAt = sentAt; }
    public String getLastError() { return lastError; }
    public void setLastError(String lastError) { this.lastError = lastError; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}

