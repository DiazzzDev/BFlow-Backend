package Diaz.Dev.BFlow.transactionimport.service;

import bflow.transactionimport.service.InputSanitizer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class InputSanitizerTest {

    private final InputSanitizer sanitizer = new InputSanitizer();

    @Test
    void rejectsScriptTag() {
        assertThatThrownBy(() ->
                sanitizer.sanitize("title", "<script>alert(1)</script>")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsOnErrorAttribute() {
        assertThatThrownBy(() ->
                sanitizer.sanitize("description", "<img src=x onerror=alert(1)>")
        ).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void neutralizesLeadingEqualsSign() {
        String result = sanitizer.sanitize("title", "=CMD('/c calc')");
        assertThat(result).startsWith("'=");
    }

    @Test
    void leavesOrdinaryTextUnchanged() {
        String result = sanitizer.sanitize("title", "Almuerzo con cliente");
        assertThat(result).isEqualTo("Almuerzo con cliente");
    }
}