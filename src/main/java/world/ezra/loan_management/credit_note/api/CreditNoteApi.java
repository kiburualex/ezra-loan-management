package world.ezra.loan_management.credit_note.api;

import org.springframework.http.ResponseEntity;
import world.ezra.loan_management.credit_note.internal.dto.CreditNoteRequest;
import world.ezra.loan_management.credit_note.internal.model.CreditNote;

/**
 * @author Alex Kiburu
 */
public interface CreditNoteApi {
    ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection);
    CreditNote create(CreditNoteRequest request);
}
