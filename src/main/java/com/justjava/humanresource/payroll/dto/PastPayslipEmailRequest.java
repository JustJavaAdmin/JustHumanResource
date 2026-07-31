package com.justjava.humanresource.payroll.dto;

import java.util.List;

public record PastPayslipEmailRequest(List<Long> paySlipIds) {
}