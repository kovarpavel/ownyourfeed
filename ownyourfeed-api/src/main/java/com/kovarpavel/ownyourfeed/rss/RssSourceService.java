package com.kovarpavel.ownyourfeed.rss;

import com.kovarpavel.ownyourfeed.source.dto.SourceDetailsDTO;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.ByteArrayInputStream;

@Component
public class RssSourceService extends RssService {

    Logger logger = LoggerFactory.getLogger(RssSourceService.class);

    @Autowired
    public RssSourceService(WebClient webClient) {
        super(webClient);
    }

    public SourceDetailsDTO getRssChannelInfo(final String url) throws RssApiException {
        String xmlBody = getResponseBody(url);
        if (xmlBody == null) {
            throw new RssApiException("Response from URL: " + url + " is empty.");
        }
        SourceDetailsDTO sourceInfoDTO = parseRssSourceDetails(xmlBody);
        if (sourceInfoDTO == null) {
            throw new RssApiException("Parsing source info from URL: " + url + " failed.");
        }
        return sourceInfoDTO;
    }

    private SourceDetailsDTO parseRssSourceDetails(String rssChannelXml) {
        try {
            final Document rss = saxBuilder.build(new ByteArrayInputStream(rssChannelXml.getBytes()));
            final Element channel = rss.getRootElement().getChild("channel");

            return new SourceDetailsDTO(
                    channel.getChildText("title"),
                    channel.getChildText("description"),
                    channel.getChildText("link"));

        } catch (JDOMException | java.io.IOException | NullPointerException exception) {
            logger.error(exception.getMessage(), exception);
            return null;
        }
    }

}
