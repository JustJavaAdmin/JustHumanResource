package com.justjava.humanresource.payroll.dto;

import java.util.List;

public record PayslipEmailResponse(
        int requested,
        int sent,
        int skipped,
        int failed,
        List<PayslipEmailResult> results
) {
}
