package world.ezra.loan_management.loan.api;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.loan.internal.dto.LoanRequest;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanInstallment;

import java.util.List;
import java.util.Optional;

/**
 * @author Alex Kiburu
 */
public interface LoanApi {
    ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection);
    ResponseEntity<?> newLoanApplication(LoanRequest request);
    Optional<Loan> findById(Long id);
    Loan save(Loan loan);
    List<LoanInstallment> findPendingInstallmentsByLoanId(Long loanId);
    void updateInstallment(LoanInstallment installment);
    boolean areAllInstallmentsPaid(Long loanId);
}
