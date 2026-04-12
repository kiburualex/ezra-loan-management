package world.ezra.loan_management.credit_note.internal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import world.ezra.loan_management.credit_note.api.CreditNoteApi;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api/credit-notes")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class CreditNoteController {
    private final CreditNoteApi creditNoteApi;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false) String searchTerm,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    @RequestParam(defaultValue = "id") String sortBy,
                                    @RequestParam(defaultValue = "desc") String sortDirection) {
        return creditNoteApi.findAll(searchTerm, page, size, sortBy, sortDirection);
    }
}
