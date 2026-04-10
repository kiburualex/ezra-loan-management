package world.ezra.loan_management.product.internal.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import world.ezra.loan_management.product.internal.model.Product;

import java.util.Optional;

/**
 * @author Alex Kiburu
 */
@Repository
public interface ProductRepository extends JpaRepository<@NonNull Product, @NonNull Long> {
    @Query("SELECT c FROM Product c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
            "LOWER(c.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<@NonNull Product> searchByAllFields(@Param("searchTerm") String searchTerm, Pageable pageable);

    Optional<Product> findFirstByName(String name);
}
