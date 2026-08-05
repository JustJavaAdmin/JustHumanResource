package com.justjava.humanresource.payroll.dto;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
public class PayrollOriginalVsAdjustedDTO {

    private Long employeeId;
    private String employeeNumber;
    private String employeeName;
    private LocalDate periodStart;
    private LocalDate periodEnd;
    private Long originalRunId;
    private Long adjustedRunId;
    private Integer originalVersion;
    private Integer adjustedVersion;
    private BigDecimal originalGrossPay;
    private BigDecimal adjustedGrossPay;
    private BigDecimal grossDifference;
    private BigDecimal originalTotalDeductions;
    private BigDecimal adjustedTotalDeductions;
    private BigDecimal deductionsDifference;
    private BigDecimal originalNetPay;
    private BigDecimal adjustedNetPay;
    private BigDecimal netDifference;
    private String latestAmendmentReason;
    private List<PayrollLineItemDifferenceDTO> lineDifferences;
}
