package world.ezra.loan_management.repayment.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import world.ezra.loan_management.common.dto.GenericResponse;
import world.ezra.loan_management.common.enums.InstallmentStatus;
import world.ezra.loan_management.common.enums.LoanStatus;
import world.ezra.loan_management.credit_note.api.CreditNoteApi;
import world.ezra.loan_management.credit_note.internal.dto.CreditNoteRequest;
import world.ezra.loan_management.credit_note.internal.enums.CreditNoteReason;
import world.ezra.loan_management.credit_note.internal.model.CreditNote;
import world.ezra.loan_management.customer.api.CustomerApi;
import world.ezra.loan_management.loan.api.LoanApi;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.model.LoanInstallment;
import world.ezra.loan_management.repayment.api.RepaymentApi;
import world.ezra.loan_management.repayment.internal.dto.RepaymentRequest;
import world.ezra.loan_management.repayment.internal.dto.RepaymentResponse;
import world.ezra.loan_management.repayment.internal.model.Repayment;
import world.ezra.loan_management.repayment.internal.repository.RepaymentRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RepaymentServiceImpl implements RepaymentApi {
    private final RepaymentRepository repaymentRepository;
    private final LoanApi loanApi;
    private final CustomerApi customerApi;
    private final CreditNoteApi creditNoteApi;

    @Override
    public ResponseEntity<?> repay(RepaymentRequest request) {
        log.info("Repayment for loan: {}, amount: {}, paymentMethod: {}",
                request.loanId(), request.amount(), request.paymentMethod());

        // Get the loan
        Loan loan = loanApi.findById(request.loanId())
                .orElseThrow(() -> new NoSuchElementException("Loan not found with ID: " + request.loanId()));

        // Validate loan status
        if (loan.getStatus() == LoanStatus.CLOSED) {
            throw new IllegalStateException("Loan is already closed");
        }
        if (loan.getStatus() == LoanStatus.CANCELLED) {
            throw new IllegalStateException("Loan is cancelled");
        }
        if (loan.getStatus() == LoanStatus.WRITTEN_OFF) {
            throw new IllegalStateException("Loan is written off");
        }

        BigDecimal remainingAmount = request.amount();
        BigDecimal totalAllocated = BigDecimal.ZERO;

        // Get pending installments in order
        List<LoanInstallment> pendingInstallments = loanApi.findPendingInstallmentsByLoanId(loan.getId());

        if (pendingInstallments.isEmpty()) {
            throw new IllegalStateException("No pending installments found for this loan");
        }

        // Allocate payment to installments (oldest first)
        for (LoanInstallment installment : pendingInstallments) {
            if (remainingAmount.compareTo(BigDecimal.ZERO) <= 0) {
                break;
            }

            BigDecimal outstanding = installment.getAmountDue().subtract(installment.getAmountPaid());
            BigDecimal toPay = remainingAmount.min(outstanding);

            // Update installment
            BigDecimal newPaidAmount = installment.getAmountPaid().add(toPay);
            installment.setAmountPaid(newPaidAmount);

            // Check if installment is fully paid
            if (newPaidAmount.compareTo(installment.getAmountDue()) >= 0) {
                installment.setStatus(InstallmentStatus.PAID);
                installment.setPaidDate(LocalDateTime.now());
                log.info("Installment {} fully paid", installment.getId());
            }

            loanApi.updateInstallment(installment);

            // Create repayment record
            Repayment repayment = Repayment.builder()
                    .loan(loan)
                    .installment(installment)
                    .amount(toPay)
                    .paymentMethod(request.paymentMethod())
                    .paymentDate(LocalDateTime.now())
                    .build();
            repaymentRepository.save(repayment);

            totalAllocated = totalAllocated.add(toPay);
            remainingAmount = remainingAmount.subtract(toPay);

            log.info("Allocated {} to installment {}", toPay, installment.getInstallmentNumber());
        }

        // Handle overpayment (if any)
        if (remainingAmount.compareTo(BigDecimal.ZERO) > 0) {
            log.warn("Overpayment of {} cannot be allocated to any installment", remainingAmount);
            // Could create a credit note or refund in real system
            CreditNoteRequest creditNote = new CreditNoteRequest(
                    loan.getId(), remainingAmount, CreditNoteReason.OVERPAYMENT);
            CreditNote savedCreditNote = creditNoteApi.create(creditNote);
            log.info("Credit Note with id : {}, created for loan {}", savedCreditNote.getId(), loan.getId());
        }

        // Check if loan is fully repaid
        boolean allInstallmentsPaid = loanApi.areAllInstallmentsPaid(loan.getId());
        if (allInstallmentsPaid) {
            loan.setStatus(LoanStatus.CLOSED);
            loanApi.save(loan);
            log.info("Loan {} fully repaid and closed", loan.getId());
        }

        // 7. Update customer financial metrics
        customerApi.updateCustomerMetrics(loan.getCustomer().getId(), totalAllocated);

        RepaymentResponse repaymentResponse = new RepaymentResponse(
                null,
                loan.getId(),
                null,
                totalAllocated,
                LocalDateTime.now(),
                request.paymentMethod(),
                String.format("Successfully processed %.2f towards loan", totalAllocated)
        );

        GenericResponse response = GenericResponse.builder()
                .status("00")
                .message("Successful repayment")
                .data(repaymentResponse)
                .build();

        return ResponseEntity.ok(response);
    }

}
