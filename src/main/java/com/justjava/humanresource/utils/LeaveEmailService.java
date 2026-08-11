package com.justjava.humanresource.utils;

import com.justjava.humanresource.hr.entity.Employee;
import com.justjava.humanresource.hr.service.EmployeeService;
import com.justjava.humanresource.leave.entity.LeaveRequest;
import com.justjava.humanresource.orgStructure.entity.Company;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LeaveEmailService {

    private static final String DEFAULT_COMPANY_NAME = "Human Resources";

    private final EmployeeService employeeService;
    private final ResendService resendService;

    // These notify* methods are invoked from AfterCommitExecutor, i.e. AFTER the
    // originating transaction has already committed. Without an active transaction,
    // Hibernate falls back to an ad-hoc auto-commit session to lazily load
    // requester -> department -> company, which can fail outside a real transaction
    // depending on what's being fetched. REQUIRES_NEW opens a fresh, genuine
    // transaction for the entire lookup chain so those lazy loads are always safe.

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyLeaveSubmitted(LeaveRequest request) {
        if (request == null) {
            log.warn("Leave email: notifyLeaveSubmitted called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getEmployeeId(), "leave submission requester");
        if (requester == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);

        String employeeName = requester.getFullName();
        String subject = "Leave request received";
        String text = "Dear " + employeeName + ",\n\n"
                + "Your leave request has been received and is now being processed. You will be notified when a decision is made.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(employeeName) + ",</p>"
                + "<p>Your <strong>leave</strong> request has been received and is now being processed. You will be notified when a decision is made.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(requester, subject, html, text, "leave submission notice");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyPendingApproval(LeaveRequest request, Long approverEmployeeId) {
        if (request == null) {
            log.warn("Leave email: notifyPendingApproval called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getEmployeeId(), "pending approval requester (for company branding)");
        if (requester == null) {
            return;
        }

        Employee approver = safeGetEmployee(approverEmployeeId, "pending approval approver");
        if (approver == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);

        String managerName = approver.getFullName();
        String subject = "Pending leave approval";
        String text = "Dear " + managerName + ",\n\n"
                + "You have a pending leave request awaiting your review. Please log in to view the details.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(managerName) + ",</p>"
                + "<p>You have a pending <strong>leave</strong> request awaiting your review. Please log in to view the details.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(approver, subject, html, text, "pending approval notice");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyLeaveApproved(LeaveRequest request) {
        if (request == null) {
            log.warn("Leave email: notifyLeaveApproved called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getEmployeeId(), "leave approval requester");
        if (requester == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);

        String employeeName = requester.getFullName();
        String subject = "Leave request approved";
        String text = "Dear " + employeeName + ",\n\n"
                + "Your leave request has been approved.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(employeeName) + ",</p>"
                + "<p>Your <strong>leave</strong> request has been approved.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(requester, subject, html, text, "leave approved notice");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    public void notifyLeaveRejected(LeaveRequest request) {
        if (request == null) {
            log.warn("Leave email: notifyLeaveRejected called with a null request.");
            return;
        }

        Employee requester = safeGetEmployee(request.getEmployeeId(), "leave rejection requester");
        if (requester == null) {
            return;
        }

        Company company = resolveCompany(requester);
        String companyName = resolveCompanyName(company);

        String employeeName = requester.getFullName();
        String subject = "Leave request rejected";
        String text = "Dear " + employeeName + ",\n\n"
                + "Your leave request has been rejected.\n\n"
                + "Regards,\n" + companyName;
        String html = "<p>Dear " + html(employeeName) + ",</p>"
                + "<p>Your <strong>leave</strong> request has been rejected.</p>"
                + "<p>Regards,<br><strong>" + html(companyName) + "</strong></p>";

        sendBestEffort(requester, subject, html, text, "leave rejected notice");
    }

    private void sendBestEffort(Employee recipient, String subject, String html, String text, String context) {
        String email = recipient.getEmail();
        if (email == null || email.isBlank()) {
            log.warn("Leave email: skipping {} for employee {} - no email address on file.", context, recipient.getId());
            return;
        }

        try {
            resendService.sendEmail(email.trim(), subject, html, text);
        } catch (Exception e) {
            log.warn("Leave email: failed to send {} to employee {}: {}", context, recipient.getId(), e.getMessage());
        }
    }

    private Employee safeGetEmployee(Long employeeId, String context) {
        if (employeeId == null) {
            log.warn("Leave email: cannot resolve {} - employee id is null.", context);
            return null;
        }
        try {
            return employeeService.getById(employeeId);
        } catch (Exception e) {
            log.warn("Leave email: could not resolve {} (employee {}): {}", context, employeeId, e.getMessage());
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