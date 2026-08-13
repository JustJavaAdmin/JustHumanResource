package com.justjava.humanresource.request.entity;

import com.justjava.humanresource.core.entity.BaseEntity;
import com.justjava.humanresource.request.enums.FreeRouteEventType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "workflow_request_free_route_events", indexes = {
        @Index(name = "idx_request_free_route_request", columnList = "workflowRequestId"),
        @Index(name = "idx_request_free_route_to", columnList = "toEmployeeId")
})
public class WorkflowRequestFreeRouteEvent extends BaseEntity {
    @Column(nullable = false)
    private Long workflowRequestId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private FreeRouteEventType eventType;

    @Column(nullable = false)
    private Long fromEmployeeId;

    @Column(nullable = false)
    private Long toEmployeeId;

    @Column(length = 1000)
    private String comment;
}