package world.ezra.loan_management.common.enums;

/**
 * @author Alex Kiburu
 */
public enum LoanStatus {
    OPEN,         // Active loan, payments being made
    CLOSED,       // Fully paid
    CANCELLED,    // Cancelled before disbursement
    OVERDUE,      // Payments past due
    WRITTEN_OFF   // Defaulted, written off as loss
}
