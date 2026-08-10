package com.justjava.humanresource.payroll.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class HistoricalPayrollAdjustmentCommand {

    @NotNull
    private Long employeeId;

    @NotNull
    private Long periodId;

    @NotBlank
    private String reason;

    @Valid
    @NotEmpty
    private List<HistoricalPayrollAdjustmentLineCommand> lines;
}
