package bflow.expenses.DTO;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Getter
@Setter
public class ExpenseResponse {

    private String id;

    private String title;

    private String description;

    private BigDecimal amount;

    private LocalDate date;

    private String expenseType;

    private Boolean taxDeductible;

    private Boolean recurring;

    private Boolean reimbursable;

    private String walletId;

    private String walletName;

    private String contributorId;

    private String contributorName;

    private Instant createdAt;

}
