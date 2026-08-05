package com.justjava.humanresource.payroll.statutory.service;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface TaxBandUploadService {
    UploadSummary uploadTaxBands(MultipartFile file, LocalDate effectiveFrom, LocalDate effectiveTo);

    record UploadSummary(int totalRows, int successRows, String regimeCode) {}
}
