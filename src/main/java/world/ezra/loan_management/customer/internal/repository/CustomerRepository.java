package world.ezra.loan_management.customer.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.customer.internal.model.Customer;

/**
 * @author Alex Kiburu
 */
@Repository
public interface CustomerRepository extends JpaRepository<@NonNull Customer, @NonNull Long> {

    @Query("SELECT c FROM Customer c WHERE " +
            "LOWER(c.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.address) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<@NonNull Customer> searchByAllFields(@Param("searchTerm") String searchTerm, Pageable pageable);
}
