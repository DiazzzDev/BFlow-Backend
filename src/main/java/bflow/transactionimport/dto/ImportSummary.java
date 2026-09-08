package bflow.transactionimport.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Summary response returned after processing a transaction import.
 */
@Getter
@Setter
public class ImportSummary {

    /** Total number of data rows processed (header excluded). */
    private int totalRows;

    /** Number of rows imported successfully. */
    private int successCount;

    /** Number of rows that failed validation or processing. */
    private int failureCount;

    /** Per-row results, in file order. */
    private List<ImportRowResult> results;
}