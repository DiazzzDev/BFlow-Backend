package bflow.transactionimport.mapping;

import java.text.Normalizer;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Detects which uploaded-file column corresponds to each logical
 * {@link ImportField}, tolerating header naming variation (Spanish/
 * English synonyms, accents, spacing, casing). Format-agnostic: only
 * needs the header names, so it works the same for CSV, XLSX, etc.
 */
@Component
public class ImportColumnMapper {

    /** Fields the file must contain in some recognizable form. */
    private static final Set<ImportField> REQUIRED_FIELDS = EnumSet.of(
            ImportField.TITLE, ImportField.AMOUNT, ImportField.CATEGORY
    );

    /** Pattern used to strip everything but letters/digits after normalizing. */
    private static final Pattern NON_ALNUM = Pattern.compile("[^a-z0-9]");

    /** Known header aliases per logical field, already normalized. */
    private static final Map<ImportField, Set<String>> ALIASES = buildAliases();

    private static Map<ImportField, Set<String>> buildAliases() {
        Map<ImportField, Set<String>> aliases = new EnumMap<>(ImportField.class);

        aliases.put(ImportField.TITLE, Set.of(
                "title", "titulo", "concept", "concepto", "name", "nombre",
                "memo", "detalle", "movimiento"
        ));

        aliases.put(ImportField.DESCRIPTION, Set.of(
                "description", "descripcion", "notes", "nota", "notas",
                "detail", "details", "comentario", "comentarios", "observacion",
                "observaciones"
        ));

        aliases.put(ImportField.AMOUNT, Set.of(
                "amount", "monto", "valor", "importe", "total", "cantidad",
                "price", "precio", "montotransaccion"
        ));

        aliases.put(ImportField.DATE, Set.of(
                "date", "fecha", "transactiondate", "fechatransaccion",
                "fechamovimiento"
        ));

        aliases.put(ImportField.CATEGORY, Set.of(
                "category", "categoria", "rubro", "clasificacion",
                "tipogasto", "tipoingreso"
        ));

        aliases.put(ImportField.TYPE, Set.of(
                "type", "tipo", "transactiontype", "tipotransaccion",
                "tipomovimiento"
        ));

        return aliases;
    }

    /**
     * Resolves a header-name mapping for the given file headers.
     *
     * @param headers the header names as read from the uploaded file
     * @return the resolved mapping
     * @throws IllegalArgumentException if a required field could not
     *         be matched to any header
     */
    public ColumnMapping resolve(final List<String> headers) {
        Map<ImportField, String> resolved = new EnumMap<>(ImportField.class);
        Map<String, String> normalizedToOriginal = new LinkedHashMap<>();

        for (String header : headers) {
            normalizedToOriginal.putIfAbsent(normalize(header), header);
        }

        for (Map.Entry<ImportField, Set<String>> entry : ALIASES.entrySet()) {
            ImportField field = entry.getKey();
            for (String alias : entry.getValue()) {
                String original = normalizedToOriginal.get(alias);
                if (original != null) {
                    resolved.put(field, original);
                    break;
                }
            }
        }

        Set<ImportField> missing = EnumSet.copyOf(REQUIRED_FIELDS);
        missing.removeAll(resolved.keySet());

        if (!missing.isEmpty()) {
            throw new IllegalArgumentException(
                    "Could not detect required column(s) "
                            + missing
                            + " in the file header. Columns found: "
                            + headers
                            + ". Accepted names include, e.g.: title/titulo, "
                            + "amount/monto, category/categoria."
            );
        }

        return new ColumnMapping(resolved);
    }

    private String normalize(final String header) {
        if (header == null) {
            return "";
        }
        String noAccents = Normalizer.normalize(header.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return NON_ALNUM.matcher(noAccents.toLowerCase()).replaceAll("");
    }
}
