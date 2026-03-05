package transitflow.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Simple health check endpoint.
 */
@RestController
public class HealthController {

    @GetMapping("/")
    public String health() {
        return "TransitFlow API running in Azure";
    }
}