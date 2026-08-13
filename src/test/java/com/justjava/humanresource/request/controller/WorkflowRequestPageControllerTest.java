package com.justjava.humanresource.request.controller;

import com.justjava.humanresource.core.config.AuthenticationManager;
import com.justjava.humanresource.hr.entity.Employee;
import com.justjava.humanresource.hr.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class WorkflowRequestPageControllerTest {

    private final AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
    private final EmployeeService employeeService = mock(EmployeeService.class);
    private final WorkflowRequestPageController controller = new WorkflowRequestPageController(authenticationManager, employeeService);

    @Test
    void employeeOnlyUserIsRedirectedFromSharedRequestsPage() {
        when(authenticationManager.isEmployee()).thenReturn(true);

        String view = controller.requests(new ConcurrentModel());

        assertEquals("redirect:/employee/requests", view);
    }

    @Test
    void hrUserCanOpenSharedRequestsPage() {
        when(authenticationManager.isHumanResource()).thenReturn(true);

        ConcurrentModel model = new ConcurrentModel();
        String view = controller.requests(model);

        assertEquals("request/main", view);
        assertEquals("Requests", model.getAttribute("title"));
    }

    @Test
    void employeeRoutesUseEmployeeTemplates() {
        String testEmail = "employee@test.com";
        Employee mockEmployee = new Employee();
        mockEmployee.setId(1L);

        when(authenticationManager.get("email")).thenReturn(testEmail);
        when(employeeService.getByEmail(testEmail)).thenReturn(mockEmployee);
        when(employeeService.getEmployeeWithBankDetails(1L)).thenReturn(mockEmployee);

        assertEquals("request/employee-main", controller.employeeRequests(new ConcurrentModel()));
        assertEquals("request/employee-detail", controller.employeeRequestDetail(new ConcurrentModel()));
        assertEquals("request/employee-userGuide", controller.employeeUserGuide(new ConcurrentModel()));
    }
}