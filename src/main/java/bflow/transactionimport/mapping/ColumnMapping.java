package bflow.transactionimport.mapping;

import java.util.Map;

/**
 * Resolved mapping from logical {@link ImportField}s to the actual
 * header names present in a specific uploaded file, regardless of
 * whether that file was CSV, XLSX, or any future format.
 */
public record ColumnMapping(Map<ImportField, String> headerByField) {

    /**
     * Returns the actual header name mapped to the given field, or
     * {@code null} if that (optional) field was not present in the file.
     *
     * @param field the logical field
     * @return the source header name, or null
     */
    public String header(final ImportField field) {
        return headerByField.get(field);
    }

    /**
     * Checks whether the given field was matched to a header.
     *
     * @param field the logical field
     * @return true if a header was found for this field
     */
    public boolean has(final ImportField field) {
        return headerByField.containsKey(field);
    }
}
