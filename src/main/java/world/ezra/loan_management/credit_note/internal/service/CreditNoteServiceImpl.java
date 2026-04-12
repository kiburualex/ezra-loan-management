package world.ezra.loan_management.credit_note.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import world.ezra.loan_management.common.dto.PaginatedResponse;
import world.ezra.loan_management.credit_note.api.CreditNoteApi;
import world.ezra.loan_management.credit_note.internal.dto.CreditNoteRequest;
import world.ezra.loan_management.credit_note.internal.model.CreditNote;
import world.ezra.loan_management.credit_note.internal.repository.CreditNoteRepository;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CreditNoteServiceImpl implements CreditNoteApi {
    private final CreditNoteRepository creditNoteRepository;

    @Override
    public ResponseEntity<?> findAll(String searchTerm, int page, int size, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<@NonNull CreditNote> dataPage = creditNoteRepository.findAll(pageable);
        return ResponseEntity.ok(new PaginatedResponse<>(dataPage));
    }

    @Override
    public CreditNote create(CreditNoteRequest request) {
        CreditNote creditNote = CreditNote.builder()
                .loanId(request.loanId())
                .amount(request.amount())
                .reason(request.reason())
                .used(false)
                .build();
        return creditNoteRepository.save(creditNote);
    }
}
