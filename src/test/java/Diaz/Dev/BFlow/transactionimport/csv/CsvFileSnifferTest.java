package Diaz.Dev.BFlow.transactionimport.csv;


import bflow.transactionimport.csv.CsvFileSniffer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;

class CsvFileSnifferTest {

    private final CsvFileSniffer sniffer = new CsvFileSniffer();

    @Test
    void rejectsXlsxRenamedAsCsv() {
        byte[] zipMagic = {0x50, 0x4B, 0x03, 0x04, 0x00, 0x00};

        assertThatThrownBy(() -> sniffer.assertLooksLikeCsv(zipMagic))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("xlsx");
    }

    @Test
    void acceptsPlainTextCsv() {
        byte[] content = "title,amount\nAlmuerzo,5\n"
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);

        assertThatCode(() -> sniffer.assertLooksLikeCsv(content))
                .doesNotThrowAnyException();
    }

    @Test
    void detectsSemicolonDelimiter() {
        byte[] content = "titulo;monto;categoria\nAlmuerzo;5;Comida\n"
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);

        assertThat(sniffer.detectDelimiter(content)).isEqualTo(';');
    }

    @Test
    void detectsCommaDelimiterByDefault() {
        byte[] content = "title,amount,category\nAlmuerzo,5,Comida\n"
                .getBytes(java.nio.charset.StandardCharsets.UTF_8);

        assertThat(sniffer.detectDelimiter(content)).isEqualTo(',');
    }

    // falta el import de assertThatCode arriba
}