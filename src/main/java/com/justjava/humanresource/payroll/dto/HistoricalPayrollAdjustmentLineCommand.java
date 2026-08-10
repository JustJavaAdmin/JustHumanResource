package com.justjava.humanresource.payroll.dto;

import com.justjava.humanresource.payroll.enums.PayComponentType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class HistoricalPayrollAdjustmentLineCommand {

    @NotBlank
    private String componentCode;

    private String description;

    @NotNull
    private PayComponentType componentType;

    @NotNull
    private BigDecimal correctedAmount;

    private Boolean taxable;
    private Boolean pensionable;
    private Boolean taxRelief;
    private Boolean partOfGross;
    private Boolean outOfPayroll;
}
