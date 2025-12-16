package com.example.insurancecompany.filter;

import jakarta.servlet.*;
import java.io.IOException;

public class CharacterEncodingFilter implements Filter {
    
    private static final String DEFAULT_ENCODING = "UTF-8";
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // No initialization needed
    }
    
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        // Skip filter for static resources
        if (request instanceof jakarta.servlet.http.HttpServletRequest) {
            jakarta.servlet.http.HttpServletRequest httpRequest = (jakarta.servlet.http.HttpServletRequest) request;
            String path = httpRequest.getRequestURI();
            
            if (path != null) {
                // Check for static resources - check both full path and relative path
                boolean isStaticResource = 
                    path.contains("/static/") ||
                    path.startsWith("/static/") ||
                    path.endsWith(".css") || 
                    path.endsWith(".js") || 
                    path.endsWith(".png") || 
                    path.endsWith(".jpg") || 
                    path.endsWith(".jpeg") || 
                    path.endsWith(".gif") || 
                    path.endsWith(".svg") || 
                    path.endsWith(".ico") || 
                    path.endsWith(".woff") || 
                    path.endsWith(".woff2") || 
                    path.endsWith(".ttf") || 
                    path.endsWith(".eot");
                
                if (isStaticResource) {
                    // Don't set encoding for static resources - let Tomcat handle them
                    chain.doFilter(request, response);
                    return;
                }
            }
        }
        
        // Set encoding for request
        if (request.getCharacterEncoding() == null) {
            request.setCharacterEncoding(DEFAULT_ENCODING);
        }
        
        // Set encoding for response
        response.setCharacterEncoding(DEFAULT_ENCODING);
        
        // Only set content type if not already set (JSP will set it)
        if (!response.isCommitted() && response.getContentType() == null) {
            response.setContentType("text/html; charset=" + DEFAULT_ENCODING);
        }
        
        chain.doFilter(request, response);
    }
    
    @Override
    public void destroy() {
        // No cleanup needed
    }
}

