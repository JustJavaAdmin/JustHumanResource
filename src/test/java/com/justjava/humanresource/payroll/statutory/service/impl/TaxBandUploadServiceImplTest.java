package com.justjava.humanresource.payroll.statutory.service.impl;

import com.justjava.humanresource.core.enums.RecordStatus;
import com.justjava.humanresource.payroll.statutory.dto.TaxBandUploadRowDTO;
import com.justjava.humanresource.payroll.statutory.entity.PayeTaxBand;
import com.justjava.humanresource.payroll.statutory.repositories.PayeTaxBandRepository;
import com.justjava.humanresource.payroll.statutory.service.TaxBandCsvParserService;
import com.justjava.humanresource.payroll.statutory.service.impl.TaxBandUploadServiceImpl.TaxBandUploadValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TaxBandUploadServiceImplTest {

    @Mock
    private TaxBandCsvParserService csvParserService;

    @Mock
    private PayeTaxBandRepository payeTaxBandRepository;

    @Mock
    private MultipartFile file;

    private TaxBandUploadServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new TaxBandUploadServiceImpl(csvParserService, payeTaxBandRepository);
    }

    @Test
    void uploadTaxBands_shouldUseSelectedEffectiveDates() {
        LocalDate effectiveFrom = LocalDate.of(2026, 1, 1);
        LocalDate effectiveTo = LocalDate.of(2026, 12, 31);
        when(csvParserService.parse(file)).thenReturn(List.of(
                row(2, "0", "300000", "7"),
                row(3, "300000", null, "11")
        ));

        service.uploadTaxBands(file, effectiveFrom, effectiveTo);

        ArgumentCaptor<PayeTaxBand> bandCaptor = ArgumentCaptor.forClass(PayeTaxBand.class);
        verify(payeTaxBandRepository).deleteAllInBatch();
        verify(payeTaxBandRepository, times(2)).save(bandCaptor.capture());

        for (PayeTaxBand band : bandCaptor.getAllValues()) {
            assertEquals(effectiveFrom, band.getEffectiveFrom());
            assertEquals(effectiveTo, band.getEffectiveTo());
            assertEquals(RecordStatus.ACTIVE, band.getStatus());
        }
    }

    @Test
    void uploadTaxBands_shouldRejectMissingEffectiveFrom() {
        when(csvParserService.parse(file)).thenReturn(List.of(row(2, "0", null, "7")));

        TaxBandUploadValidationException ex = assertThrows(
                TaxBandUploadValidationException.class,
                () -> service.uploadTaxBands(file, null, null)
        );

        assertEquals("Effective from is required", ex.getRowErrors().getFirst().message());
        verify(payeTaxBandRepository, never()).deleteAllInBatch();
    }

    @Test
    void uploadTaxBands_shouldRejectEffectiveToBeforeEffectiveFrom() {
        LocalDate effectiveFrom = LocalDate.of(2026, 6, 1);
        LocalDate effectiveTo = LocalDate.of(2026, 5, 31);
        when(csvParserService.parse(file)).thenReturn(List.of(row(2, "0", null, "7")));

        TaxBandUploadValidationException ex = assertThrows(
                TaxBandUploadValidationException.class,
                () -> service.uploadTaxBands(file, effectiveFrom, effectiveTo)
        );

        assertEquals("Effective to cannot be before effective from", ex.getRowErrors().getFirst().message());
        verify(payeTaxBandRepository, never()).deleteAllInBatch();
    }

    private static TaxBandUploadRowDTO row(int rowNumber, String lowerBound, String upperBound, String rate) {
        TaxBandUploadRowDTO row = new TaxBandUploadRowDTO();
        row.setRowNumber(rowNumber);
        row.setLowerBound(new BigDecimal(lowerBound));
        row.setUpperBound(upperBound == null ? null : new BigDecimal(upperBound));
        row.setRate(new BigDecimal(rate));
        return row;
    }
}
