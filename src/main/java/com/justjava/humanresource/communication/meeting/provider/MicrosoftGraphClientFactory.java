package com.justjava.humanresource.communication.meeting.provider;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.justjava.humanresource.communication.meeting.config.MeetingIntegrationProperties;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class MicrosoftGraphClientFactory {

    private static final String GRAPH_DEFAULT_SCOPE = "https://graph.microsoft.com/.default";

    public GraphServiceClient create(MeetingIntegrationProperties.Microsoft microsoft) {
        try {
            log.debug("Creating GraphServiceClient with tenant: {}", microsoft.getTenantId());
            ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                    .tenantId(microsoft.getTenantId())
                    .clientId(microsoft.getClientId())
                    .clientSecret(microsoft.getClientSecret())
                    .build();
            
            log.debug("Building GraphServiceClient with scope: {}", GRAPH_DEFAULT_SCOPE);
            GraphServiceClient client = new GraphServiceClient(credential, GRAPH_DEFAULT_SCOPE);
            log.info("Successfully created GraphServiceClient");
            return client;
        } catch (Exception ex) {
            log.error("Failed to create GraphServiceClient. Check your Azure credentials (TenantId: {}, ClientId: {})", 
                    microsoft.getTenantId(), microsoft.getClientId(), ex);
            throw new RuntimeException("Failed to create Microsoft Graph client: " + ex.getMessage(), ex);
        }
    }
}
