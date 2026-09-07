package bflow.transactionimport.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * Sanitizes free-text fields (title, description) coming from
 * user-uploaded files before they're persisted. Two distinct threats:
 * <ul>
 *   <li>Formula/CSV injection: a value starting with {@code =+-@} gets
 *       interpreted as a formula if this data is ever re-exported to
 *       CSV/Excel by another feature. Neutralized by prefixing with a
 *       single quote, which Excel treats as "force text".</li>
 *   <li>HTML/script injection: if this text is ever rendered
 *       unescaped in a frontend, embedded markup becomes stored XSS.
 *       Rejected outright rather than stripped, since legitimate
 *       transaction titles never need HTML.</li>
 * </ul>
 */
@Slf4j
@Component
public class InputSanitizer {

    private static final Pattern HTML_OR_SCRIPT = Pattern.compile(
            "<\\s*(script|iframe|img|svg|a|object|embed|style|link)\\b"
                    + "|javascript:"
                    + "|on\\w+\\s*=",
            Pattern.CASE_INSENSITIVE
    );

    private static final char[] FORMULA_TRIGGERS = {'=', '+', '-', '@', '\t'};

    /**
     * Sanitizes a free-text field value.
     *
     * @param fieldName field name, used only in error messages
     * @param value the raw value from the uploaded file
     * @return the sanitized value, or {@code null} if the input was
     *         {@code null}
     * @throws IllegalArgumentException if the value contains
     *         HTML/script content
     */
    public String sanitize(final String fieldName, final String value) {
        if (value == null) {
            return null;
        }

        if (HTML_OR_SCRIPT.matcher(value).find()) {
            log.warn(
                    "Rejected '{}' value containing HTML/script content "
                            + "during import",
                    fieldName
            );
            throw new IllegalArgumentException(
                    fieldName + " contains disallowed HTML/script content"
            );
        }

        if (value.length() > 0 && isFormulaTrigger(value.charAt(0))) {
            log.warn(
                    "Neutralized formula-injection prefix in '{}' during import",
                    fieldName
            );
            return "'" + value;
        }

        return value;
    }

    private boolean isFormulaTrigger(final char c) {
        for (char trigger : FORMULA_TRIGGERS) {
            if (trigger == c) {
                return true;
            }
        }
        return false;
    }
}