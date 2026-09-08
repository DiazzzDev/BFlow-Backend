package bflow.transactionimport.csv;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

/**
 * Inspects raw file bytes before parsing to reject obviously
 * non-CSV uploads (e.g. an .xlsx renamed to .csv) with a clear
 * message, and to auto-detect comma vs. semicolon delimiters, which
 * are both common depending on the user's regional Excel settings.
 */
@Slf4j
@Component
public class CsvFileSniffer {

    private static final byte[] ZIP_MAGIC = {0x50, 0x4B, 0x03, 0x04};
    private static final byte[] OLE_MAGIC = {
            (byte) 0xD0, (byte) 0xCF, (byte) 0x11, (byte) 0xE0
    };
    private static final byte[] PDF_MAGIC = {0x25, 0x50, 0x44, 0x46};

    /**
     * Rejects uploads whose binary signature doesn't look like text,
     * before any decoding is attempted.
     *
     * @param content raw file bytes
     * @throws IllegalArgumentException if the file is a known
     *         non-CSV binary format
     */
    public void assertLooksLikeCsv(final byte[] content) {
        if (startsWith(content, ZIP_MAGIC)) {
            throw new IllegalArgumentException(
                    "The uploaded file looks like an .xlsx/.docx/compressed "
                            + "file, not a CSV. Export it as CSV "
                            + "(comma or semicolon separated) and try again."
            );
        }
        if (startsWith(content, OLE_MAGIC)) {
            throw new IllegalArgumentException(
                    "The uploaded file looks like a legacy .xls/.doc file, "
                            + "not a CSV. Export it as CSV and try again."
            );
        }
        if (startsWith(content, PDF_MAGIC)) {
            throw new IllegalArgumentException(
                    "The uploaded file looks like a PDF, not a CSV."
            );
        }
    }

    /**
     * Detects whether the file uses comma or semicolon as delimiter,
     * based on the header line only.
     *
     * @param content raw file bytes
     * @return the detected delimiter character
     */
    public char detectDelimiter(final byte[] content) {
        String firstLine = firstLine(content);
        long commas = firstLine.chars().filter(c -> c == ',').count();
        long semicolons = firstLine.chars().filter(c -> c == ';').count();

        char delimiter = semicolons > commas ? ';' : ',';
        log.info(
                "Detected CSV delimiter '{}' (commas={}, semicolons={})",
                delimiter, commas, semicolons
        );
        return delimiter;
    }

    private String firstLine(final byte[] content) {
        String text = new String(content, StandardCharsets.UTF_8);
        int newlineIndex = text.indexOf('\n');
        String line = newlineIndex >= 0 ? text.substring(0, newlineIndex) : text;
        return line.replace("\r", "");
    }

    private boolean startsWith(final byte[] content, final byte[] magic) {
        if (content.length < magic.length) {
            return false;
        }
        for (int i = 0; i < magic.length; i++) {
            if (content[i] != magic[i]) {
                return false;
            }
        }
        return true;
    }
}