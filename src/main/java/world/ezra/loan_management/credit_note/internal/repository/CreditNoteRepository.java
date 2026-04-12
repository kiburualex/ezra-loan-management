package world.ezra.loan_management.credit_note.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.credit_note.internal.model.CreditNote;

/**
 * @author Alex Kiburu
 */
@Repository
public interface CreditNoteRepository extends JpaRepository<@NonNull CreditNote, @NonNull Long> {
}
