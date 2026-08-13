package com.justjava.humanresource.request.workflow.delegate;
import com.justjava.humanresource.request.entity.WorkflowRequest;
import com.justjava.humanresource.request.enums.RequestStatus;
import com.justjava.humanresource.request.handler.WorkflowRequestHandlerRegistry;
import com.justjava.humanresource.request.repository.WorkflowRequestRepository;
import com.justjava.humanresource.utils.AfterCommitExecutor;
import com.justjava.humanresource.utils.RequestEmailService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.*;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
@Component("finalizeRequestRejectionDelegate") @RequiredArgsConstructor public class FinalizeRequestRejectionDelegate implements JavaDelegate { private final WorkflowRequestRepository repository;private final WorkflowRequestHandlerRegistry handlers;private final RequestEmailService requestEmailService;private final AfterCommitExecutor afterCommitExecutor;public void execute(DelegateExecution e){Long id=((Number)e.getVariable("workflowRequestId")).longValue();var r=repository.findById(id).orElseThrow();r.setStatus(RequestStatus.REJECTED);r.setRejectedAt(LocalDateTime.now());r.setFreeRouteCurrentApproverEmployeeId(null);repository.save(r);handlers.get(r.getRequestType()).afterRejected(r);WorkflowRequest notificationRequest=r;afterCommitExecutor.runAfterCommit(()->requestEmailService.notifyRequestRejected(notificationRequest));} }