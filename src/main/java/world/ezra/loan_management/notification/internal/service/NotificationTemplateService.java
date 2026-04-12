package world.ezra.loan_management.notification.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.common.dto.GenericResponse;
import world.ezra.loan_management.notification.internal.dto.NotificationTemplateRequest;
import world.ezra.loan_management.notification.internal.model.NotificationTemplate;
import world.ezra.loan_management.notification.internal.repository.NotificationTemplateRepository;

/**
 * @author Alex Kiburu
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationTemplateService {
    private final NotificationTemplateRepository templateRepository;

    /**
     * Create a new notification template
     */
    @Transactional
    public ResponseEntity<?> create(NotificationTemplateRequest request) {
        log.info("Creating notification template for event: {} and channel: {}",
                request.eventType(), request.channel().name());

        // Check if template already exists for this event type and channel
        var existingTemplate = templateRepository.findByEventTypeAndChannel(
                request.eventType(), request.channel());

        if (existingTemplate.isPresent()) {
            throw new IllegalStateException(
                    String.format("Template already exists for event type '%s' and channel '%s'",
                            request.eventType(), request.channel().name()));
        }

        NotificationTemplate template = NotificationTemplate.builder()
                .eventType(request.eventType())
                .channel(request.channel())
                .subject(request.subject())
                .body(request.body())
                .productId(request.productId())
                .build();

        NotificationTemplate saved = templateRepository.save(template);
        log.info("Created notification template with ID: {}", saved.getId());

        GenericResponse response = GenericResponse.builder()
                .status("00")
                .message("Notification template created successfully")
                .data(saved)
                .build();
        return ResponseEntity.ok().body(response);
    }
}
