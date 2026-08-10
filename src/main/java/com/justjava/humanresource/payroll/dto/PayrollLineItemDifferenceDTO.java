package com.justjava.humanresource.payroll.dto;

import com.justjava.humanresource.payroll.enums.PayComponentType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class PayrollLineItemDifferenceDTO {

    private String componentCode;
    private String description;
    private PayComponentType componentType;
    private BigDecimal originalAmount;
    private BigDecimal adjustedAmount;
    private BigDecimal differenceAmount;
    private boolean taxable;
    private boolean pensionable;
    private boolean taxRelief;
    private boolean partOfGross;
    private boolean outOfPayroll;
}
