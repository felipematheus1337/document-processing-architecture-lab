package document_service.v1.filter;

import document_service.v1.constants.ConstantsUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@Component
public class CorrelationIdFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException,
            IOException {

        String correlationId = Optional
                .ofNullable(request.getHeader(ConstantsUtils.CORRELATION_ID_HEADER))
                .filter(value -> !value.isBlank())
                .orElse(UUID.randomUUID().toString());

        try {
            MDC.put(ConstantsUtils.CORRELATION_ID_MDC_KEY, correlationId);
            response.setHeader(ConstantsUtils.CORRELATION_ID_HEADER, correlationId);

            filterChain.doFilter(request, response);
        } finally  {

            MDC.remove(ConstantsUtils.CORRELATION_ID_MDC_KEY);
            MDC.remove("documentId");
        }
    }
}
