package com.justjava.humanresource.payroll.controller;

import com.justjava.humanresource.payroll.dto.HistoricalPayrollAdjustmentCommand;
import com.justjava.humanresource.payroll.dto.PayrollOriginalVsAdjustedDTO;
import com.justjava.humanresource.payroll.dto.PayrollVersionHistoryDTO;
import com.justjava.humanresource.payroll.service.HistoricalPayrollAdjustmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payroll/historical-adjustments")
@RequiredArgsConstructor
public class HistoricalPayrollAdjustmentController {

    private final HistoricalPayrollAdjustmentService service;

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
