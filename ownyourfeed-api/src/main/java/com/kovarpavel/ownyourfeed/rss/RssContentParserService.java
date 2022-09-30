package com.kovarpavel.ownyourfeed.rss;

import com.kovarpavel.ownyourfeed.dto.SourceInfoDTO;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.springframework.stereotype.Service;
import java.io.ByteArrayInputStream;


@Service
public class RssContentParserService {

    private final SAXBuilder saxBuilder;

    public RssContentParserService() {
        saxBuilder = new SAXBuilder();
    }

    public SourceInfoDTO getRssChannelInfo(String rssChannelXml) {
        try {
            final Document rss = saxBuilder.build(new ByteArrayInputStream(rssChannelXml.getBytes()));
            final Element channel = rss.getRootElement().getChild("channel");

            return new SourceInfoDTO(
                    channel.getChildText("title"),
                    channel.getChildText("description"),
                    channel.getChildText("link")
            );

        } catch (JDOMException | java.io.IOException exception ) {
            System.out.println(exception.getMessage());
            return null;
        }
    }
}
