package world.ezra.loan_management.common.exceptions;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

/**
 * @author Alex Kiburu
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {
    private String status;
    private String message;
    private Integer errorCode;
    private String errorDescription;
    private Object error;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
