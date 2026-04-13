package world.ezra.loan_management.notification.internal.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import world.ezra.loan_management.common.dto.GenericResponse;
import world.ezra.loan_management.common.dto.LoanCreationRequestEvent;
import world.ezra.loan_management.common.enums.PreferredChannel;
import world.ezra.loan_management.notification.internal.dto.NotificationTemplateRequest;
import world.ezra.loan_management.notification.internal.model.NotificationTemplate;
import world.ezra.loan_management.notification.internal.repository.NotificationTemplateRepository;

import java.util.Optional;

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
        var existingTemplate = templateRepository.findFirstByEventTypeAndChannel(
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


    public NotificationTemplate findNotificationTemplate(LoanCreationRequestEvent event, String eventType) {
        // Priority 1: Product-specific template
        Optional<NotificationTemplate> productTemplate = templateRepository
                .findFirstByProductIdAndEventType(event.productId(), eventType);
        if (productTemplate.isPresent()) {
            return productTemplate.get();
        }

        // Priority 2: Customer's preferred channel (default to SMS)
        String preferredChannel = event.preferredChannel() != null ? event.preferredChannel() : "SMS";
        Optional<NotificationTemplate> channelTemplate = templateRepository
                .findFirstByEventTypeAndChannel(eventType, PreferredChannel.valueOf(preferredChannel));

        // Priority 3: Default SMS template (fallback)
        return channelTemplate.orElseGet(() -> templateRepository
                .findFirstByEventTypeAndChannel(eventType, PreferredChannel.SMS)
                .orElse(null));
    }

}
