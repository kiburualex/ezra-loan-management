package world.ezra.loan_management.loan.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import world.ezra.loan_management.common.enums.LoanFeeType;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanFee;

import java.util.List;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
public interface LoanFeeRepository extends JpaRepository<@NonNull LoanFee, @NonNull Long> {

    List<LoanFee> findByLoan(Loan loan);

    // Find fees by type for a loan
    List<LoanFee> findByLoanIdAndFeeType(Long loanId, LoanFeeType feeType);

    // Find latest fee applied for a loan
    Optional<LoanFee> findFirstByLoanOrderByAppliedDateDesc(Loan loan);
}
