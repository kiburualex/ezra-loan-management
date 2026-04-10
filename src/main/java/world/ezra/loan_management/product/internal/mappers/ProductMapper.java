package world.ezra.loan_management.product.internal.mappers;

import org.springframework.stereotype.Component;
import world.ezra.loan_management.product.internal.dto.ProductRequest;
import world.ezra.loan_management.product.internal.dto.ProductResponse;
import world.ezra.loan_management.product.internal.model.Product;

/**
 * @author Alex Kiburu
 */
@Component
public class ProductMapper {
    public Product toEntity(ProductRequest request) {
        if (request == null) {
            return null;
        }

        return Product.builder()
                .name(request.name())
                .description(request.description())
                .tenureType(request.tenureType())
                .tenureValue(request.tenureValue())
                .interestRate(request.interestRate())
                .serviceFeeType(request.serviceFeeType())
                .serviceFeeValue(request.serviceFeeValue())
                .dailyFeeType(request.dailyFeeType())
                .dailyFeeValue(request.dailyFeeValue())
                .lateFeeType(request.lateFeeType())
                .lateFeeValue(request.lateFeeValue())
                .daysAfterDueForLateFee(request.daysAfterDueForLateFee())
                .daysAfterDueForDailyFee(request.daysAfterDueForDailyFee())
                .minLoanAmount(request.minLoanAmount())
                .maxLoanAmount(request.maxLoanAmount())
                .active(request.active() != null ? request.active() : true)
                .build();
    }

    /**
     * Convert Product entity to ProductResponse
     */
    public ProductResponse toResponse(Product product) {
        if (product == null) {
            return null;
        }

        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getTenureType(),
                product.getTenureValue(),
                product.getInterestRate(),
                product.getServiceFeeType(),
                product.getServiceFeeValue(),
                product.getDailyFeeType(),
                product.getDailyFeeValue(),
                product.getLateFeeType(),
                product.getLateFeeValue(),
                product.getDaysAfterDueForLateFee(),
                product.getDaysAfterDueForDailyFee(),
                product.getMinLoanAmount(),
                product.getMaxLoanAmount(),
                product.getActive(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    /**
     * Update existing product from request (for partial updates)
     */
    public void updateEntity(Product existing, ProductRequest request) {
        if (request.name() != null)
            existing.setName(request.name());

        if (request.description() != null)
            existing.setDescription(request.description());

        if (request.tenureType() != null)
            existing.setTenureType(request.tenureType());

        if (request.tenureValue() != null)
            existing.setTenureValue(request.tenureValue());

        if (request.interestRate() != null)
            existing.setInterestRate(request.interestRate());

        if (request.serviceFeeType() != null)
            existing.setServiceFeeType(request.serviceFeeType());

        if (request.serviceFeeValue() != null)
            existing.setServiceFeeValue(request.serviceFeeValue());

        if (request.dailyFeeType() != null)
            existing.setDailyFeeType(request.dailyFeeType());

        if (request.dailyFeeValue() != null)
            existing.setDailyFeeValue(request.dailyFeeValue());

        if (request.lateFeeType() != null)
            existing.setLateFeeType(request.lateFeeType());

        if (request.lateFeeValue() != null)
            existing.setLateFeeValue(request.lateFeeValue());

        if (request.daysAfterDueForLateFee() != null)
            existing.setDaysAfterDueForLateFee(request.daysAfterDueForLateFee());

        if (request.daysAfterDueForDailyFee() != null)
            existing.setDaysAfterDueForDailyFee(request.daysAfterDueForDailyFee());

        if (request.minLoanAmount() != null)
            existing.setMinLoanAmount(request.minLoanAmount());

        if (request.maxLoanAmount() != null)
            existing.setMaxLoanAmount(request.maxLoanAmount());

        if (request.active() != null)
            existing.setActive(request.active());
    }
}
