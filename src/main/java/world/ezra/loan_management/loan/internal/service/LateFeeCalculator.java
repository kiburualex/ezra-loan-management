package world.ezra.loan_management.loan.internal.service;

import org.springframework.stereotype.Service;
import world.ezra.loan_management.common.enums.FeeType;
import world.ezra.loan_management.loan.internal.model.Loan;

import java.math.BigDecimal;

/**
 * @author Alex Kiburu
 */
@Service
public class LateFeeCalculator {
    // todo:: should use the Loan And App interface
//    public BigDecimal calculateLateFee(Loan loan, Product product) {
//        LocalDate dueDate = loan.getDueDate();
//        LocalDate currentDate = LocalDate.now();
//
//        long daysLate = ChronoUnit.DAYS.between(dueDate, currentDate);
//
//        BigDecimal totalFees = BigDecimal.ZERO;
//
//        // Check for late fee (one-time)
//        if (daysLate >= product.getDaysAfterDueForLateFee()) {
//            totalFees = totalFees.add(calculateOneTimeLateFee(loan, product));
//        }
//
//        // Check for daily fees (recurring)
//        if (product.getDaysAfterDueForDailyFee() != null &&
//                daysLate >= product.getDaysAfterDueForDailyFee()) {
//
//            long daysWithDailyFee = daysLate - product.getDaysAfterDueForDailyFee() + 1;
//            BigDecimal dailyFee = calculateDailyFee(loan, product);
//            totalFees = totalFees.add(dailyFee.multiply(BigDecimal.valueOf(daysWithDailyFee)));
//        }
//
//        return totalFees;
//    }
//
//    private BigDecimal calculateOneTimeLateFee(Loan loan, Product product) {
//        if (product.getLateFeeType() == FeeType.FIXED) {
//            return product.getLateFeeValue();
//        } else { // PERCENTAGE
//            return loan.getOutstandingAmount()
//                    .multiply(product.getLateFeeValue())
//                    .divide(BigDecimal.valueOf(100));
//        }
//    }
//
//    private BigDecimal calculateDailyFee(Loan loan, Product product) {
//        if (product.getDailyFeeType() == FeeType.FIXED) {
//            return product.getDailyFeeValue();
//        } else { // PERCENTAGE
//            return loan.getOutstandingAmount()
//                    .multiply(product.getDailyFeeValue())
//                    .divide(BigDecimal.valueOf(100));
//        }
//    }
}
