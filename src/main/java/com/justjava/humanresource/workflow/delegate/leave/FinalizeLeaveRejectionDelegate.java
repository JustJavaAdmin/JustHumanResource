package com.justjava.humanresource.workflow.delegate.leave;

import com.justjava.humanresource.leave.entity.LeaveApprovalStep;
import com.justjava.humanresource.leave.entity.LeaveRequest;
import com.justjava.humanresource.leave.enums.LeaveApprovalDecision;
import com.justjava.humanresource.leave.enums.LeaveRequestStatus;
import com.justjava.humanresource.leave.repository.LeaveApprovalStepRepository;
import com.justjava.humanresource.leave.repository.LeaveRequestRepository;
import com.justjava.humanresource.utils.AfterCommitExecutor;
import com.justjava.humanresource.utils.LeaveEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
public class FinalizeLeaveRejectionDelegate implements JavaDelegate {

    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveApprovalStepRepository leaveApprovalStepRepository;
    private final LeaveEmailService leaveEmailService;
    private final AfterCommitExecutor afterCommitExecutor;

    @Override
    public void execute(DelegateExecution execution) {
        Long leaveRequestId = ((Number) execution.getVariable("leaveRequestId")).longValue();
        Integer currentLevel = ((Number) execution.getVariable("currentLevel")).intValue();
        String comment = execution.getVariable("approvalComment") != null
                ? String.valueOf(execution.getVariable("approvalComment"))
                : null;

        int currentSequenceNo = currentLevel + 1;

        leaveApprovalStepRepository.findByLeaveRequestIdAndSequenceNo(leaveRequestId, currentSequenceNo)
                .ifPresentOrElse(step -> {
                    step.setDecision(LeaveApprovalDecision.REJECTED);
                    step.setComments(comment);
                    step.setDecisionAt(LocalDateTime.now());
                    leaveApprovalStepRepository.save(step);
                }, () -> log.warn(
                        "FinalizeLeaveRejectionDelegate: no approval step found for leave request {} at sequence {}; continuing rejection without audit update.",
                        leaveRequestId, currentSequenceNo));

        LeaveRequest request = leaveRequestRepository.findById(leaveRequestId)
                .orElseThrow(() -> new IllegalStateException("Leave request not found: " + leaveRequestId));
        request.setStatus(LeaveRequestStatus.REJECTED);
        leaveRequestRepository.save(request);

        LeaveRequest notificationRequest = request;
        afterCommitExecutor.runAfterCommit(() ->
                leaveEmailService.notifyLeaveRejected(notificationRequest)
        );
    }
}