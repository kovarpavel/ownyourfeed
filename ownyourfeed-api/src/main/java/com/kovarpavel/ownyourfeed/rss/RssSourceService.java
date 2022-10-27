package com.kovarpavel.ownyourfeed.rss;

import com.kovarpavel.ownyourfeed.source.dto.SourceDetailsDTO;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;
import java.net.URI;

@Service
public class RssSourceService {

    private final WebClient webClient;
    private final SAXBuilder saxBuilder;

    public RssSourceService(WebClient webClient) {
        this.webClient = webClient;
        this.saxBuilder = new SAXBuilder();
    }

    public SourceDetailsDTO getRssChannelInfo(final String url) throws RssApiException {
        String xmlBody = getResponseBody(url);
        if (xmlBody == null) {
            throw new RssApiException("Response from URL: " + url + " is empty.");
        }
        SourceDetailsDTO sourceInfoDTO = parseRssSourceDetails(xmlBody);
        if (sourceInfoDTO == null) {
            throw new RssApiException("Parsing of response from URL: " + url + " failed.");
        }
        return sourceInfoDTO;
    }

    private String getResponseBody(String url) {
        ResponseEntity<String> response =
                webClient.get().uri(URI.create(url)).retrieve().toEntity(String.class).block();
        return response != null ? response.getBody() : null;
    }

    private SourceDetailsDTO parseRssSourceDetails(String rssChannelXml) {
        try {
            final Document rss = saxBuilder.build(new ByteArrayInputStream(rssChannelXml.getBytes()));
            final Element channel = rss.getRootElement().getChild("channel");

            return new SourceDetailsDTO(
                    channel.getChildText("title"),
                    channel.getChildText("description"),
                    channel.getChildText("link")
            );

        } catch (JDOMException | java.io.IOException | NullPointerException exception) {
            System.out.println(exception.getMessage());
            return null;
        }
    }

}
