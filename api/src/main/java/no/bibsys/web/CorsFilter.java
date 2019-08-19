package no.bibsys.web;

import java.io.IOException;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;

@Provider
public class CorsFilter implements ContainerResponseFilter {
 
    private static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    private static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    private static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    private static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";

    @Override
    public void filter(ContainerRequestContext requestContext, 
      ContainerResponseContext responseContext) throws IOException {
          responseContext.getHeaders().add(
            ACCESS_CONTROL_ALLOW_ORIGIN, "*");
          responseContext.getHeaders().add(
            ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
          responseContext.getHeaders().add(
           ACCESS_CONTROL_ALLOW_HEADERS,
           "origin, content-type, accept, authorization, x-amz-date, x-amz-security-token, api-key");
          responseContext.getHeaders().add(
            ACCESS_CONTROL_ALLOW_METHODS, 
            "GET, POST, PUT, DELETE, OPTIONS, HEAD");
    }
}