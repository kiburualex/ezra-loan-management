package world.ezra.loan_management.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Alex Kiburu
 */
@RestController
@RequestMapping("/api")
@SecurityRequirement(name = "Bearer Authentication")
public class HelloController {

    @GetMapping("/hello")
    @Operation(summary = "Protected Hello Endpoint", description = "Requires valid JWT token")
    public String hello(Authentication authentication) {
        return "Hello, " + authentication.getName() + "! Welcome to the protected endpoint.";
    }
}
