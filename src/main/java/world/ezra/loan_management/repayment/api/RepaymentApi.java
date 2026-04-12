package world.ezra.loan_management.repayment.api;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.repayment.internal.dto.RepaymentRequest;

/**
 * @author Alex Kiburu
 */
public interface RepaymentApi {
    ResponseEntity<?> repay(RepaymentRequest request);
}
