package com.kovarpavel.ownyourfeed.rss;

import java.net.URI;

import org.jdom2.input.SAXBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

public abstract class RssService {

    private final WebClient webClient;
    protected final SAXBuilder saxBuilder;    

    public RssService(WebClient webClient) {
        this.webClient = webClient;
        this.saxBuilder = new SAXBuilder();
    }

    public String getResponseBody(String url) {
        ResponseEntity<String> response =
                webClient.get().uri(URI.create(url)).retrieve().toEntity(String.class).block();
        return response != null ? response.getBody() : null;
    }

}
