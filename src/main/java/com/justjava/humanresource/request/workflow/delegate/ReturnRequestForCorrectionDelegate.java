package com.justjava.humanresource.request.workflow.delegate;
import com.justjava.humanresource.request.entity.WorkflowRequest;
import com.justjava.humanresource.request.enums.RequestStatus;
import com.justjava.humanresource.request.repository.WorkflowRequestRepository;
import com.justjava.humanresource.utils.AfterCommitExecutor;
import com.justjava.humanresource.utils.RequestEmailService;
import lombok.RequiredArgsConstructor;
import org.flowable.engine.delegate.*;
import org.springframework.stereotype.Component;
@Component("returnRequestForCorrectionDelegate") @RequiredArgsConstructor public class ReturnRequestForCorrectionDelegate implements JavaDelegate {private final WorkflowRequestRepository repository;private final RequestEmailService requestEmailService;private final AfterCommitExecutor afterCommitExecutor;public void execute(DelegateExecution e){Long id=((Number)e.getVariable("workflowRequestId")).longValue();var r=repository.findById(id).orElseThrow();r.setStatus(RequestStatus.RETURNED_FOR_CORRECTION);r.setWorkflowInstanceId(null);r.setFreeRouteCurrentApproverEmployeeId(null);WorkflowRequest saved=repository.save(r);afterCommitExecutor.runAfterCommit(()->requestEmailService.notifyRequestReturnedForCorrection(saved));} }