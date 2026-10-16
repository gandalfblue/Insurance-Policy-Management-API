package com.andres_lozada.demo.config;

import com.andres_lozada.demo.exception.CoreGatewayException;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.ResponseErrorHandler;

import java.io.IOException;
import java.net.URI;

public class CoreRestTemplateErrorHandler implements ResponseErrorHandler {

    @Override
    public boolean hasError(ClientHttpResponse response) throws IOException {
        return response.getStatusCode().isError();
    }

    @Override
    public void handleError(URI url, HttpMethod method, ClientHttpResponse response) throws IOException {
        throw new CoreGatewayException("CORE returned status: " + response.getStatusCode());
    }
}
