package com.kovarpavel.ownyourfeed.rss;

import com.kovarpavel.ownyourfeed.dto.SourceInfoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;

@Service
public class RssService {

    private final WebClient webClient;
    private final RssContentParserService rssContentParserService;

    public RssService(RssContentParserService rssContentParserService) {
        webClient = WebClient.builder().build();
        this.rssContentParserService = rssContentParserService;
    }

    public SourceInfoDTO getRssChannelInfo(final String url) throws RssApiException {
        String xmlBody = getResponseBody(url);
        if (xmlBody == null) {
            throw new RssApiException("Response from URL: " + url + " is empty.");
        }
        SourceInfoDTO sourceInfoDTO = rssContentParserService.getRssChannelInfo(xmlBody);
        if (sourceInfoDTO == null) {
            throw new RssApiException("Parsing of response from URL: " + url + " failed.");
        }
        return sourceInfoDTO;
    }

    // TODO add function to save source to DB for user
    // check if url is already in db


    private String getResponseBody(final String url) {
        ResponseEntity<String> response =
                webClient.get().uri(URI.create(url)).retrieve().toEntity(String.class).block();
        return response != null ? response.getBody() : null;
    }

}
