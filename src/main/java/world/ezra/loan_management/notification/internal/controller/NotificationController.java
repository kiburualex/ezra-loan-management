package world.ezra.loan_management.notification.internal.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import world.ezra.loan_management.notification.internal.dto.NotificationTemplateRequest;
import world.ezra.loan_management.notification.internal.service.NotificationTemplateService;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class NotificationController {
    private final NotificationTemplateService notificationTemplateService;

    @PostMapping("/create-template")
    public ResponseEntity<?> create(@Valid @RequestBody NotificationTemplateRequest request) {
        return notificationTemplateService.create(request);
    }
}
