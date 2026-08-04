package com.justjava.humanresource.hr.service;

import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

public interface EmployeePayItemUploadService {
    UploadSummary uploadPayItems(MultipartFile file, LocalDate effectiveFrom, LocalDate effectiveTo);

    record UploadSummary(int totalRows, int successRows) {}
}