package com.justjava.humanresource.utils;

import com.justjava.humanresource.hr.entity.Employee;
import com.justjava.humanresource.hr.service.EmployeeService;
import com.justjava.humanresource.orgStructure.entity.Company;
import com.justjava.humanresource.request.entity.WorkflowRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestEmailService {

    private static final String DEFAULT_COMPANY_NAME = "Human Resources";

    private final EmployeeService employeeService;
    private final ResendService resendService;



    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyRequestSubmitted(WorkflowRequest request) {
        if (request == null) {
            log.warn("Request email: notifyRequestSubmitted called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getRequesterEmployeeId(), "request submission requester");
        if (requester == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);
        String requestTypePhrase = requestTypePhrase(request);

        String employeeName = requester.getFullName();
        String subject = "Request received";
        String text = "Dear " + employeeName + ",\n\n"
                + "Your " + requestTypePhrase + " has been received and is now being processed. You will be notified when a decision is made.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(employeeName) + ",</p>"
                + "<p>Your <strong>" + html(requestTypePhrase) + "</strong> has been received and is now being processed. You will be notified when a decision is made.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(requester, subject, html, text, "request submission notice");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyPendingApproval(WorkflowRequest request, Long approverEmployeeId) {
        if (request == null) {
            log.warn("Request email: notifyPendingApproval called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getRequesterEmployeeId(), "pending approval requester (for company branding)");
        if (requester == null) {
            return;
        }

        Employee approver = safeGetEmployee(approverEmployeeId, "pending approval approver");
        if (approver == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);
        String requestTypePhrase = requestTypePhrase(request);

        String approverName = approver.getFullName();
        String subject = "Pending request approval";
        String text = "Dear " + approverName + ",\n\n"
                + "You have a pending " + requestTypePhrase + " awaiting your review. Please log in to view the details.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(approverName) + ",</p>"
                + "<p>You have a pending <strong>" + html(requestTypePhrase) + "</strong> awaiting your review. Please log in to view the details.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(approver, subject, html, text, "pending approval notice");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyRequestApproved(WorkflowRequest request) {
        if (request == null) {
            log.warn("Request email: notifyRequestApproved called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getRequesterEmployeeId(), "request approval requester");
        if (requester == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);
        String requestTypePhrase = requestTypePhrase(request);

        String employeeName = requester.getFullName();
        String subject = "Request approved";
        String text = "Dear " + employeeName + ",\n\n"
                + "Your " + requestTypePhrase + " has been approved.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(employeeName) + ",</p>"
                + "<p>Your <strong>" + html(requestTypePhrase) + "</strong> has been approved.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(requester, subject, html, text, "request approved notice");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyRequestRejected(WorkflowRequest request) {
        if (request == null) {
            log.warn("Request email: notifyRequestRejected called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getRequesterEmployeeId(), "request rejection requester");
        if (requester == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);
        String requestTypePhrase = requestTypePhrase(request);

        String employeeName = requester.getFullName();
        String subject = "Request rejected";
        String text = "Dear " + employeeName + ",\n\n"
                + "Your " + requestTypePhrase + " has been rejected.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(employeeName) + ",</p>"
                + "<p>Your <strong>" + html(requestTypePhrase) + "</strong> has been rejected.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(requester, subject, html, text, "request rejected notice");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyRequestReturnedForCorrection(WorkflowRequest request) {
        if (request == null) {
            log.warn("Request email: notifyRequestReturnedForCorrection called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getRequesterEmployeeId(), "request return-for-correction requester");
        if (requester == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);
        String requestTypePhrase = requestTypePhrase(request);

        String employeeName = requester.getFullName();
        String subject = "Request returned for correction";
        String text = "Dear " + employeeName + ",\n\n"
                + "Your " + requestTypePhrase + " has been returned for correction. Please log in to review the comments and resubmit when ready.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(employeeName) + ",</p>"
                + "<p>Your <strong>" + html(requestTypePhrase) + "</strong> has been returned for correction. Please log in to review the comments and resubmit when ready.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(requester, subject, html, text, "request returned-for-correction notice");
    }

    private void sendBestEffort(Employee recipient, String subject, String html, String text, String context) {
        String email = recipient.getEmail();
        if (email == null || email.isBlank()) {
            log.warn("Request email: skipping {} for employee {} - no email address on file.", context, recipient.getId());
            return;
        }

        try {
            resendService.sendEmail(email.trim(), subject, html, text);
        } catch (Exception e) {
            log.warn("Request email: failed to send {} to employee {}: {}", context, recipient.getId(), e.getMessage());
        }
    }

    private Employee safeGetEmployee(Long employeeId, String context) {
        if (employeeId == null) {
            log.warn("Request email: cannot resolve {} - employee id is null.", context);
            return null;
        }
        try {
            return employeeService.getById(employeeId);
        } catch (Exception e) {
            log.warn("Request email: could not resolve {} (employee {}): {}", context, employeeId, e.getMessage());
            return null;
        }
    }

    private Company resolveCompany(Employee requester) {
        return requester.getDepartment() != null ? requester.getDepartment().getCompany() : null;
    }

    private String resolveCompanyName(Company company) {
        if (company != null && company.getName() != null && !company.getName().isBlank()) {
            return company.getName();
        }
        return DEFAULT_COMPANY_NAME;
    }

    private String requestTypePhrase(WorkflowRequest request) {
        String label = switch (request.getRequestType()) {
            case STAFF_REQUISITION -> "Staff Requisition";
            case FILE_REQUEST -> "File Request";
            case ASSET_REQUEST -> "Asset Request";
            case EXPENSE_REIMBURSEMENT -> "Expense Reimbursement";
            case GENERAL_REQUEST -> "General Request";
        };
        return label.toLowerCase(Locale.ROOT).endsWith("request")
                ? label
                : label + " request";
    }

    private String html(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}