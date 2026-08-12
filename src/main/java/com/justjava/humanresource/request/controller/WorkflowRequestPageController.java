package com.justjava.humanresource.request.controller;

import com.justjava.humanresource.core.config.AuthenticationManager;
import com.justjava.humanresource.hr.entity.Employee;
import com.justjava.humanresource.hr.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class WorkflowRequestPageController {

    private final AuthenticationManager authenticationManager;
    private final EmployeeService employeeService;

    @GetMapping("/requests")
    public String requests(Model model) {
        if (authenticationManager.isEmployee() && !isHrUser()) {
            return "redirect:/employee/requests";
        }
        model.addAttribute("title", "Requests");
        model.addAttribute("subTitle", "Create, approve, and track organizational requests");
        return "request/main";
    }

    @GetMapping("/requests/{id}")
    public String detail(Model model) {
        model.addAttribute("title", "Request Detail");
        model.addAttribute("subTitle", "Review request details, approvals, comments, and attachments");
        return "request/detail";
    }

    @GetMapping("/requests/userGuide")
    public String userGuide(Model model) {
        model.addAttribute("title", "Request Workflow User Guide");
        model.addAttribute("subTitle", "How to create, submit, approve, and track organizational requests");
        return "request/userGuide";
    }

    @GetMapping("/employee/requests")
    public String employeeRequests(Model model) {
        populateEmployeeModel(model);
        model.addAttribute("title", "My Requests");
        model.addAttribute("subTitle", "Create, submit, and track your organizational requests");
        return "request/employee-main";
    }

    @GetMapping("/employee/requests/{id}")
    public String employeeRequestDetail(Model model) {
        populateEmployeeModel(model);
        model.addAttribute("title", "Request Detail");
        model.addAttribute("subTitle", "Review request details, approvals, comments, and attachments");
        return "request/employee-detail";
    }

    @GetMapping("/employee/requests/userGuide")
    public String employeeUserGuide(Model model) {
        populateEmployeeModel(model);
        model.addAttribute("title", "Request Workflow User Guide");
        model.addAttribute("subTitle", "How to create, submit, approve, and track organizational requests");
        return "request/employee-userGuide";
    }

    private void populateEmployeeModel(Model model) {
        String email = (String) authenticationManager.get("email");
        if (email != null) {
            Employee loginEmployee = employeeService.getByEmail(email);
            if (loginEmployee != null) {
                Employee employee = employeeService.getEmployeeWithBankDetails(loginEmployee.getId());
                model.addAttribute("employee", employee);
            }
        }
    }

    private boolean isHrUser() {
        return authenticationManager.isHumanResource()
                || authenticationManager.isJobHR()
                || authenticationManager.isAdmin()
                || authenticationManager.isRestrictedHr();
    }
}