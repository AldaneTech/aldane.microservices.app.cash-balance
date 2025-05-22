package net.aldane.cash_balance.config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class LogConfig implements Filter {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        var httpRequest = (HttpServletRequest) request;
        var requestQueryString = httpRequest.getQueryString() != null ? "?" + httpRequest.getQueryString() : "";
        var requestUriWithQueryParams = httpRequest.getRequestURI() + requestQueryString;

        if (requestUriWithQueryParams.contains("/health")) {
            log.debug("{} Request: '{}'", httpRequest.getMethod(), requestUriWithQueryParams);
        } else {
            log.info("{} Request: '{}'", httpRequest.getMethod(), requestUriWithQueryParams);
        }

        chain.doFilter(request, response);
    }

}
