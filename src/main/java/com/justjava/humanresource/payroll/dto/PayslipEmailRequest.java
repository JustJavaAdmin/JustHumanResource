package com.justjava.humanresource.payroll.dto;

import java.util.List;

public record PayslipEmailRequest(List<Long> employeeIds) {
}
