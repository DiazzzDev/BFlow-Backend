package bflow.transactionimport.controllers;

import bflow.auth.services.CurrentUserService;
import bflow.common.response.ApiResponse;
import bflow.transactionimport.dto.ImportSummary;
import bflow.transactionimport.csv.CsvImportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Tag(name = "Transaction Import", description = "Bulk import of expenses and incomes (transfers not supported)")
@RestController
@RequestMapping("/api/v1/import/csv")
@RequiredArgsConstructor
public final class ControllerCsvImport {

    /** Service handling CSV import business logic. */
    private final CsvImportService csvImportService;

    /** Service used to resolve the authenticated user. */
    private final CurrentUserService currentUserService;

    /**
     * Imports expenses/incomes from a CSV file into a single wallet.
     * Column names are auto-detected (Spanish/English synonyms
     * accepted); date is optional and defaults to today.
     *
     * @param walletId the wallet to import transactions into
     * @param file the CSV file to import
     * @param authentication the authenticated user
     * @return a per-row summary of the import
     */
    @Operation(
            summary = "Imports expenses and incomes from a CSV file.",
            description = "Bulk-creates expense/income entries for a wallet "
                    + "from a CSV file. Column names are auto-detected. "
                    + "Transfer rows are rejected."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public ApiResponse<ImportSummary> importCsv(
            @RequestParam final UUID walletId,
            @RequestParam("file") final MultipartFile file,
            final Authentication authentication
    ) {
        UUID userId = currentUserService.getCurrentUserId(authentication);

        ImportSummary summary = csvImportService.importTransactions(
                file, walletId, userId
        );

        return ApiResponse.success(
                "CSV import processed",
                summary,
                "/api/v1/import/csv"
        );
    }
}
