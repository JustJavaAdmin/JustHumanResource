package com.justjava.humanresource.payroll.dto;

import com.justjava.humanresource.core.enums.PayrollRunStatus;
import com.justjava.humanresource.payroll.enums.PayrollRunType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class PayrollVersionHistoryDTO {

    private Long payrollRunId;
    private Long employeeId;
    private String employeeNumber;
    private String employeeName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Integer versionNumber;
    private PayrollRunType runType;
    private PayrollRunStatus status;
    private BigDecimal grossPay;
    private BigDecimal totalDeductions;
    private BigDecimal netPay;
    private Long parentRunId;
    private String amendmentReason;
    private LocalDateTime createdAt;
}
