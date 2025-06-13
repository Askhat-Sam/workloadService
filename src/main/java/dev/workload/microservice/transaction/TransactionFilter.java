package dev.workload.microservice.transaction;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

@Component
@WebFilter("/*")
public class TransactionFilter implements Filter {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;

        // Get transactionId from the request header using header name
        String transactionId = httpRequest.getHeader("transactionId");
        if (transactionId == null) {
            transactionId = UUID.randomUUID().toString();
        }

        try {
            MDC.put("transactionId", transactionId);
            logger.info("Transaction started | transactionId={}", transactionId);
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove("transactionId");
        }
    }

}
