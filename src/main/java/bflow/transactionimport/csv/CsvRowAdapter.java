package bflow.transactionimport.csv;

import bflow.transactionimport.model.ImportRow;
import org.apache.commons.csv.CSVRecord;

public record CsvRowAdapter(CSVRecord record) implements ImportRow {

    @Override
    public String get(final String header) {
        try {
            return record.isMapped(header) ? record.get(header) : null;
        } catch (RuntimeException e) {
            // Broadened from IllegalArgumentException: a row with
            // fewer columns than the header throws
            // ArrayIndexOutOfBoundsException here, which previously
            // propagated uncaught and could abort the whole file
            // instead of failing just this row.
            return null;
        }
    }
}