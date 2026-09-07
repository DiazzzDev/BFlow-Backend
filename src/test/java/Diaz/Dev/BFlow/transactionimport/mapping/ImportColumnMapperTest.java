package Diaz.Dev.BFlow.transactionimport.mapping;

import bflow.transactionimport.mapping.ColumnMapping;
import bflow.transactionimport.mapping.ImportColumnMapper;
import bflow.transactionimport.mapping.ImportField;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImportColumnMapperTest {

    private final ImportColumnMapper mapper = new ImportColumnMapper();

    @Test
    void resolvesSpanishHeadersWithAccentsAndSpacing() {
        ColumnMapping mapping = mapper.resolve(List.of(
                "Título", "Monto ", "Categoría", "Fecha"
        ));

        assertThat(mapping.header(ImportField.TITLE)).isEqualTo("Título");
        assertThat(mapping.header(ImportField.AMOUNT)).isEqualTo("Monto ");
        assertThat(mapping.header(ImportField.CATEGORY)).isEqualTo("Categoría");
        assertThat(mapping.has(ImportField.DATE)).isTrue();
    }

    @Test
    void allowsMissingOptionalDateColumn() {
        ColumnMapping mapping = mapper.resolve(List.of(
                "title", "amount", "category"
        ));

        assertThat(mapping.has(ImportField.DATE)).isFalse();
    }

    @Test
    void failsWithClearMessageWhenRequiredColumnMissing() {
        assertThatThrownBy(() -> mapper.resolve(List.of("titulo", "fecha")))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("AMOUNT")
                .hasMessageContaining("CATEGORY");
    }
}