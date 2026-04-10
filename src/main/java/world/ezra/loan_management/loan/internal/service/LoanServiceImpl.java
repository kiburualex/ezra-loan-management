package world.ezra.loan_management.loan.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import world.ezra.loan_management.loan.api.LoanApi;
import world.ezra.loan_management.loan.internal.repository.LoanRepository;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanServiceImpl implements LoanApi {
    private final LoanRepository loanRepository;
}
