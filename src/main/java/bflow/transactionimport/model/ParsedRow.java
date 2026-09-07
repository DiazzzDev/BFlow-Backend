package bflow.transactionimport.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * A row that has already passed field-level validation and category
 * resolution, ready to be persisted in a batch.
 */
public record ParsedRow(
        int rowNumber,
        String type,
        String title,
        String description,
        BigDecimal amount,
        LocalDate date,
        UUID categoryId
) {
}
