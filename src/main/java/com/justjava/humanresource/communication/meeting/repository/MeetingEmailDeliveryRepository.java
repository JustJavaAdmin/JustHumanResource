package com.justjava.humanresource.communication.meeting.repository;

import com.justjava.humanresource.communication.meeting.entity.MeetingEmailDelivery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MeetingEmailDeliveryRepository extends JpaRepository<MeetingEmailDelivery, String> {
    List<MeetingEmailDelivery> findBySentAtIsNullAndNextAttemptAtBefore(LocalDateTime time);
}

