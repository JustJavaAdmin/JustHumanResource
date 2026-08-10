package com.justjava.humanresource.payroll.service;

import com.justjava.humanresource.payroll.dto.HistoricalPayrollAdjustmentCommand;
import com.justjava.humanresource.payroll.dto.HistoricalPayrollPayItemDTO;
import com.justjava.humanresource.payroll.dto.PayrollOriginalVsAdjustedDTO;
import com.justjava.humanresource.payroll.dto.PayrollVersionHistoryDTO;

import java.time.LocalDate;
import java.util.List;

public interface HistoricalPayrollAdjustmentService {

    PayrollOriginalVsAdjustedDTO previewAdjustment(HistoricalPayrollAdjustmentCommand command);

    List<HistoricalPayrollPayItemDTO> getAdjustablePayItems(Long employeeId, Long periodId);

    PayrollVersionHistoryDTO createPostedAmendment(HistoricalPayrollAdjustmentCommand command);

    List<PayrollVersionHistoryDTO> getVersionHistory(Long employeeId, Long periodId);

    PayrollOriginalVsAdjustedDTO compareOriginalToLatest(Long employeeId, Long periodId);

    List<PayrollOriginalVsAdjustedDTO> compareOriginalToLatestForPeriod(Long companyId, Long periodId);

    PayrollOriginalVsAdjustedDTO compareVersions(
            Long employeeId,
            LocalDate periodStart,
            LocalDate periodEnd,
            Integer originalVersion,
            Integer adjustedVersion
    );
}
