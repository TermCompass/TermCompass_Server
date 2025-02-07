package com.aivle.TermCompass.service;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
public class PostService {
    private final WebClient webClient;

    public PostService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("http://localhost:8000").build();
    }

    public Mono<String> sendPostRequest(String endpoint, Map<String, Object> requestData) {
        return webClient.post()
                .uri(endpoint)
                .contentType(MediaType.TEXT_PLAIN)
                .bodyValue(requestData)
                .retrieve()
                .bodyToMono(String.class);
    }
}
