package bflow.transactionimport.model;

/**
 * A single row from any importable source (CSV record, XLSX row,
 * etc.), accessible by resolved header name. Format-specific readers
 * implement this to plug into the shared {@code TransactionRowParser}
 * without duplicating parsing/validation logic per format.
 */
public interface ImportRow {

    /**
     * Returns the raw string value under the given header, or
     * {@code null} if the header is absent or the cell is empty.
     *
     * @param header the source header name (as resolved by
     *        {@code ImportColumnMapper}, not the logical field)
     * @return the raw cell value, or null
     */
    String get(String header);
}
