package bflow.expenses.DTO;

import bflow.expenses.enums.ExpenseType;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO for creating or updating expense entries.
 */
@Getter
@Setter
public class ExpenseRequest {

    private static final int TITLE_MIN_LENGTH = 5;
    private static final int TITLE_MAX_LENGTH = 50;
    private static final int DESCRIPTION_MAX_LENGTH = 100;
    private static final int AMOUNT_INTEGER_DIGITS = 15;
    private static final int AMOUNT_FRACTION_DIGITS = 2;

    @NotBlank
    @Size(min = TITLE_MIN_LENGTH, max = TITLE_MAX_LENGTH)
    private String title;

    @Nullable
    @Size(max = DESCRIPTION_MAX_LENGTH)
    private String description;

    @Digits(integer = AMOUNT_INTEGER_DIGITS,
            fraction = AMOUNT_FRACTION_DIGITS)
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull
    private LocalDate date;

    @NotNull
    private UUID walletId;

    @NotNull
    private ExpenseType type;

    private Boolean taxDeductible = false;

    private Boolean recurring = false;

    private String recurrencePattern;

    private Boolean reimbursable = false;

}
