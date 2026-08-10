package com.justjava.humanresource.payroll.controller;

import com.justjava.humanresource.payroll.dto.HistoricalPayrollAdjustmentCommand;
import com.justjava.humanresource.payroll.dto.HistoricalPayrollPayItemDTO;
import com.justjava.humanresource.payroll.dto.PayrollOriginalVsAdjustedDTO;
import com.justjava.humanresource.payroll.dto.PayrollVersionHistoryDTO;
import com.justjava.humanresource.payroll.service.HistoricalPayrollAdjustmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/payroll/historical-adjustments")
@RequiredArgsConstructor
public class HistoricalPayrollAdjustmentController {

    private final HistoricalPayrollAdjustmentService service;

    @GetMapping("/pay-items")
    public ResponseEntity<List<HistoricalPayrollPayItemDTO>> getAdjustablePayItems(
            @RequestParam Long employeeId,
            @RequestParam Long periodId) {
        return ResponseEntity.ok(service.getAdjustablePayItems(employeeId, periodId));
    }

    @PostMapping("/preview")
    public ResponseEntity<PayrollOriginalVsAdjustedDTO> preview(
            @Valid @RequestBody HistoricalPayrollAdjustmentCommand command) {
        return ResponseEntity.ok(service.previewAdjustment(command));
    }

    @PostMapping
    public ResponseEntity<PayrollVersionHistoryDTO> createPostedAmendment(
            @Valid @RequestBody HistoricalPayrollAdjustmentCommand command) {
        return ResponseEntity.ok(service.createPostedAmendment(command));
    }
}
