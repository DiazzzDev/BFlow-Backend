package bflow.income;

import bflow.common.response.ApiResponse;
import bflow.income.DTO.IncomeRequest;
import bflow.income.DTO.IncomeResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing income operations.
 * Provides endpoints for creating and retrieving income entries.
 * This class is designed for extension through inheritance.
 */
@RestController
@RequestMapping("/api/v1/incomes")
@RequiredArgsConstructor
public class ControllerIncome {
    /**
     * Service for income business logic operations.
     */
    private final ServiceIncome serviceIncome;

    /**
     * Creates a new income entry for the authenticated user's wallet.
     *
     * @param request the income request containing income details
     * @param authentication the authentication object containing the
     *        authenticated user's principal (UUID)
     * @return a ResponseEntity containing the created income response with
     *         HTTP 201 Created status
     */
    @PostMapping
    public ResponseEntity<ApiResponse<IncomeResponse>> createIncome(
            @Valid @RequestBody final IncomeRequest request,
            final Authentication authentication
    ) {
        String userIdString = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdString);
        IncomeResponse response = serviceIncome.newIncome(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Income created successfully",
                        response,
                        "/api/v1/incomes"
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<IncomeResponse>> updateIncome(
            @PathVariable String id,
            @Valid @RequestBody final IncomeRequest request,
            final Authentication authentication
    ){
        String userIdString = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdString);

        UUID incomeId = UUID.fromString(id);

        IncomeResponse response = serviceIncome.updateIncome(incomeId, request, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Income updated successfully",
                        response,
                        "/api/v1/incomes"
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteIncome(
            @PathVariable String id,
            final Authentication authentication
    ){
        String userIdString = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdString);

        UUID incomeId = UUID.fromString(id);

        serviceIncome.deleteIncome(incomeId, userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
