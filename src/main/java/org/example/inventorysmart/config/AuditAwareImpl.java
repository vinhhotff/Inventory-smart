package org.example.inventorysmart.config;

import org.springframework.data.domain.AuditorAware;
import java.util.Optional;

public class AuditAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        // Mock current user until Spring Security is integrated
        return Optional.of("SYSTEM");
    }
}
