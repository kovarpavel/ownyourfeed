package com.kovarpavel.ownyourfeed.rss;

import java.io.ByteArrayInputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.kovarpavel.ownyourfeed.item.dto.ItemDTO;

@Component
public class RssItemService extends RssService {

    Logger logger = LoggerFactory.getLogger(RssItemService.class);

    @Autowired
    public RssItemService(final WebClient webClient) {
        super(webClient);
    }

    public List<ItemDTO> getLatestItems(final String sourceUrl) throws RssApiException {
        String xmlBody = getResponseBody(sourceUrl);
        if (xmlBody == null) {
            throw new RssApiException("Response from URL: " + sourceUrl + " is empty.");
        }

        var items = parseItems(xmlBody);
        if (items.size() < 1) {
            throw new RssApiException("Parsing of items from URL: " + sourceUrl + " failed.");
        }
        return items;
    }

    private List<ItemDTO> parseItems(final String xml) {
        try {
            final Document rss = saxBuilder.build(new ByteArrayInputStream(xml.getBytes()));
            final Element channel = rss.getRootElement().getChild("channel");

            List<ItemDTO> items = new ArrayList<>();
            channel.getChildren("item")
                    .stream()
                    .forEach(item -> {
                        var pubDate = LocalDateTime.parse(
                                item.getChildText("pubDate"),
                                DateTimeFormatter.RFC_1123_DATE_TIME);
                        items.add(
                                new ItemDTO(
                                        item.getChildText("title"),
                                        item.getChildText("link"),
                                        item.getChildText("description"),
                                        item.getChildText("author"),
                                        pubDate,
                                        item.getChildText("guid")));
                    });

            return items;

        } catch (JDOMException | java.io.IOException | NullPointerException exception) {
            logger.error(exception.getMessage(), exception);
            return List.of();
        }
    }
}