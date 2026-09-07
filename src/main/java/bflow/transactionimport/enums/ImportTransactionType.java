package bflow.transactionimport.enums;

/**
 * Transaction types accepted by transaction import (any source
 * format). Transfers are intentionally excluded.
 */
public enum ImportTransactionType {
    /** Expense row. */
    EXPENSE,

    /** Income row. */
    INCOME
}
