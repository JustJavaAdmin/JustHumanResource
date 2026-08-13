package com.justjava.humanresource.request.repository;

import com.justjava.humanresource.request.entity.WorkflowRequestFreeRouteEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WorkflowRequestFreeRouteEventRepository
        extends JpaRepository<WorkflowRequestFreeRouteEvent, Long> {
    List<WorkflowRequestFreeRouteEvent> findByWorkflowRequestIdOrderByCreatedAt(Long workflowRequestId);
}