package challenge.configuration.logging;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Component
@WebFilter(filterName = "LoggingFilter", urlPatterns = "/*")
public class LoggingFilter extends OncePerRequestFilter {

    private static final String TRACE_ID = "TraceId";
    private static final Logger log = LoggerFactory.getLogger(LoggingFilter.class);

    private static final List<String> EXCLUDED_PATHS = List.of(
            "/v3/api-docs",
            "/swagger-ui",
            "/actuator/health"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (EXCLUDED_PATHS.stream().anyMatch(path -> request.getRequestURI().startsWith(path))) {
            filterChain.doFilter(request, response);
            return;
        }

        var traceId = UUID.randomUUID().toString();
        MDC.put(TRACE_ID, traceId);

        logRequest(request);
        filterChain.doFilter(request, response);
        logResponse(request, response);

        MDC.remove(TRACE_ID);
    }

    private void logRequest(HttpServletRequest request) {
        log.info("INCOMING REQUEST: method=[{}] path=[{}] params=[{}]",
                request.getMethod(),
                request.getRequestURI(),
                request.getQueryString() != null ? request.getQueryString() : ""
        );
    }

    private void logResponse(HttpServletRequest request, HttpServletResponse response) {
        log.info("REQUEST RESPONSE: method=[{}] path=[{}] status=[{}]",
                request.getMethod(),
                request.getRequestURI(),
                response.getStatus()
        );
    }

}
