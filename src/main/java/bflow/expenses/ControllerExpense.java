package bflow.expenses;

import bflow.common.response.ApiResponse;
import bflow.expenses.DTO.ExpenseRequest;
import bflow.expenses.DTO.ExpenseResponse;
import bflow.income.DTO.IncomeRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/expenses")
@RequiredArgsConstructor
public class ControllerExpense {
    private final ServiceExpense serviceExpense;

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> createExpense(
            @Valid @RequestBody final ExpenseRequest request,
            final Authentication authentication
    ) {
        String userIdString = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdString);
        ExpenseResponse response = serviceExpense.newExpense(request, userId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(
                        "Expense created successfully",
                        response,
                        "/api/v1/expenses"
                ));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(
            @PathVariable String id,
            @Valid @RequestBody final ExpenseRequest request,
            final Authentication authentication
    ){
        String userIdString = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdString);

        UUID expenseId = UUID.fromString(id);

        ExpenseResponse response = serviceExpense.updateExpense(expenseId, request, userId);

        return ResponseEntity.status(HttpStatus.OK)
                .body(ApiResponse.success(
                        "Expense updated successfully",
                        response,
                        "/api/v1/expenses"
                ));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(
            @PathVariable String id,
            final Authentication authentication
    ){
        String userIdString = (String) authentication.getPrincipal();
        UUID userId = UUID.fromString(userIdString);

        UUID expenseId = UUID.fromString(id);

        serviceExpense.deleteExpense(expenseId, userId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
