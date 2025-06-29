package ru.practicum.ewm.client;

import jakarta.annotation.Nullable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public class BaseClient {

    private final RestTemplate restTemplate;

    public BaseClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    protected <T> ResponseEntity<T> get(String path,
                                        @Nullable Map<String, Object> parameters,
                                        ParameterizedTypeReference<T> responseType) {
        return makeAndSendRequest(HttpMethod.GET, path, parameters, null, responseType);
    }

    protected <T, R> ResponseEntity<R> post(String path,
                                            T body,
                                            ParameterizedTypeReference<R> responseType) {
        return makeAndSendRequest(HttpMethod.POST, path, null, body, responseType);
    }

    private <T, R> ResponseEntity<R> makeAndSendRequest(HttpMethod method,
                                                        String path,
                                                        @Nullable Map<String, Object> parameters,
                                                        @Nullable T body,
                                                        ParameterizedTypeReference<R> responseType) {
        HttpEntity<T> requestEntity = new HttpEntity<>(body, defaultHeaders());

        try {
            if (parameters != null) {
                return restTemplate.exchange(path, method, requestEntity, responseType, parameters);
            } else {
                return restTemplate.exchange(path, method, requestEntity, responseType);
            }
        } catch (HttpStatusCodeException exception) {
            return ResponseEntity
                    .status(exception.getStatusCode())
                    .headers(exception.getResponseHeaders())
                    .body(null);
        }
    }

    private HttpHeaders defaultHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return headers;
    }
}