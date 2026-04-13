package world.ezra.loan_management.loan.internal.sweep_jobs;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.common.enums.LoanStatus;
import world.ezra.loan_management.loan.internal.model.Loan;
import world.ezra.loan_management.loan.internal.repository.LoanRepository;
import world.ezra.loan_management.loan.internal.service.LoanInstallmentService;

import java.util.List;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoanOverdueScheduler {
    private final LoanInstallmentService installmentService;
    private final LoanRepository loanRepository;

    /**
     * Overdue Loan Processing - Runs daily at 1 AM
     * Detects overdue installments, updates loan status, applies fees, triggers notifications
     */
    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void processOverdueInstallments() {
        log.info("=== OVERDUE LOAN PROCESSING STARTED ===");

        // Update installment statuses
        int updatedInstallments = installmentService.updateOverdueInstallments();

        // Update loan statuses if any installment is overdue
        if (updatedInstallments > 0) {
            // Find loans with overdue installments and update their status
            List<Loan> loans = loanRepository.findAll();
            for (Loan loan : loans) {
                if (installmentService.hasOverdueInstallments(loan) &&
                        loan.getStatus() != LoanStatus.OVERDUE) {
                    loan.setStatus(LoanStatus.OVERDUE);
                    loanRepository.save(loan);
                    log.info("Updated loan {} to OVERDUE status", loan.getId());
                    // todo:: apply late fees or trigger notification
                }
            }
        }

        log.info("Completed overdue installment processing");
    }


    /**
     * Payment Reminders - Runs daily at 9 AM
     * Sends reminders for payments due in 3 days and tomorrow
     */
    @Scheduled(cron = "0 0 9 * * *")
    @Transactional
    public void sendPaymentReminders() {
        log.info("=== PAYMENT REMINDERS STARTED ===");
        // TODO: send event reminder for late payment and fees incurred
    }
}
