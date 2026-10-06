package com.justjava.humanresource.communication.meeting.provider;

import com.justjava.humanresource.communication.meeting.MeetingProvider;
import com.justjava.humanresource.communication.meeting.config.MeetingIntegrationProperties;
import com.microsoft.graph.models.OnlineMeeting;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class MicrosoftTeamsMeetingProviderClient implements MeetingProviderClient {

    private final MeetingIntegrationProperties properties;
    private final MicrosoftGraphClientFactory graphClientFactory;

    @Override
    public MeetingProvider provider() {
        return MeetingProvider.MICROSOFT_TEAMS;
    }

    @Override
    public boolean isConfigured() {
        MeetingIntegrationProperties.Microsoft microsoft = properties.getProviders().getMicrosoft();
        boolean enabled = microsoft.isEnabled();
        boolean hasTenantId = hasText(microsoft.getTenantId());
        boolean hasClientId = hasText(microsoft.getClientId());
        boolean hasClientSecret = hasText(microsoft.getClientSecret());
        boolean hasOrganizerUserId = hasText(microsoft.getDefaultOrganizerUserId());

        boolean configured = enabled && hasTenantId && hasClientId && hasClientSecret && hasOrganizerUserId;

        if (!configured) {
            log.warn("Microsoft Teams provider not fully configured - Enabled: {}, TenantId: {}, ClientId: {}, ClientSecret: {}, OrganizerUserId: {}",
                    enabled, hasTenantId, hasClientId, hasClientSecret, hasOrganizerUserId);
        } else {
            log.debug("Microsoft Teams provider is fully configured");
        }

        return configured;
    }

    @Override
    public CreatedMeeting createMeeting(CreateMeetingRequest request) {
        if (!isConfigured()) {
            throw new MeetingProviderException("Microsoft Teams meeting provider is not configured");
        }
        MeetingIntegrationProperties.Microsoft microsoft = properties.getProviders().getMicrosoft();
        log.debug("Creating Microsoft Teams meeting with organizer: {}", microsoft.getDefaultOrganizerUserId());

        OnlineMeeting meeting = new OnlineMeeting();
        meeting.setSubject(request.subject());
        meeting.setStartDateTime(request.startTime().toOffsetDateTime());
        meeting.setEndDateTime(request.endTime().toOffsetDateTime());

        try {
            log.debug("Building GraphServiceClient with tenant: {}", microsoft.getTenantId());
            GraphServiceClient graphClient = graphClientFactory.create(microsoft);

            log.debug("Creating online meeting via Microsoft Graph API");
            OnlineMeeting response = graphClient
                    .users()
                    .byUserId(microsoft.getDefaultOrganizerUserId())
                    .onlineMeetings()
                    .post(meeting);

            if (response == null) {
                log.error("Microsoft Graph API returned null response for meeting creation");
                throw new MeetingProviderException("Microsoft Teams API returned null response");
            }

            log.info("Successfully created Microsoft Teams meeting: {}", response.getId());
            return new CreatedMeeting(
                    response.getId(),
                    response.getJoinWebUrl(),
                    null,
                    "Microsoft Teams meeting created"
            );
        } catch (RuntimeException ex) {
            log.error("Failed to create Microsoft Teams meeting", ex);
            log.error("Organizer User ID: {}", microsoft.getDefaultOrganizerUserId());
            log.error("Tenant ID: {}", microsoft.getTenantId());
            throw new MeetingProviderException("Microsoft Teams meeting creation failed: " + ex.getMessage(), ex);
        }
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

}
