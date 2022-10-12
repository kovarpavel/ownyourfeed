package com.kovarpavel.ownyourfeed.rss;

import com.kovarpavel.ownyourfeed.source.dto.SourceDetailsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringJUnitConfig
@ExtendWith(MockitoExtension.class)
public class RssServiceTest {

    @MockBean
    private WebClient webClient;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersUriSpec requestHeadersUriSpecMock;

    @Mock
    @SuppressWarnings("rawtypes")
    private WebClient.RequestHeadersSpec requestHeadersSpecMock;

    @Mock
    private WebClient.ResponseSpec responseSpecMock;

    @Test
    void getChannelInfo_Success() {
        String rss = "<rss xmlns:media=\"http://search.yahoo.com/mrss/\" version=\"2.0\">\n" +
                "<channel>\n" +
                "<image>\n" +
                "<link>https://www.root.cz/</link>\n" +
                "<title>Root.cz</title>\n" +
                "<url>https://i.iinfo.cz/r/rss-88x31.gif</url>\n" +
                "<width>88</width>\n" +
                "<height>31</height>\n" +
                "</image>\n" +
                "<title>Root.cz - články</title>\n" +
                "<link>https://www.root.cz/clanky/</link>\n" +
                "<description>Root.cz - informace nejen ze světa Linuxu</description>\n" +
                "<language>cs</language>\n" +
                "<pubDate>Mon, 10 Oct 2022 22:00:10 GMT</pubDate>" +
                "</channel>" +
                "</rss>";

        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(URI.create("url"))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.toEntity(String.class)).thenReturn(Mono.just(
                new ResponseEntity<String>(rss, HttpStatus.OK)
        ));

        RssService service = new RssService(webClient);
        assertEquals(
                service.getRssChannelInfo("url"),
                new SourceDetailsDTO(
                        "Root.cz - články",
                        "Root.cz - informace nejen ze světa Linuxu",
                        "https://www.root.cz/clanky/"));
    }

    @Test
    void getChannelInfo_EmptyBody() {
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(URI.create("url"))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.toEntity(String.class)).thenReturn(Mono.just(
                new ResponseEntity<String>(HttpStatus.NOT_FOUND)
        ));

        RssService service = new RssService(webClient);
        Exception ex = assertThrows(RssApiException.class, () -> service.getRssChannelInfo("url"));
        assertEquals("Response from URL: url is empty.", ex.getMessage());
    }

    @Test
    void getChannelInfo_ParsingError() {
        when(webClient.get()).thenReturn(requestHeadersUriSpecMock);
        when(requestHeadersUriSpecMock.uri(URI.create("url"))).thenReturn(requestHeadersSpecMock);
        when(requestHeadersSpecMock.retrieve()).thenReturn(responseSpecMock);
        when(responseSpecMock.toEntity(String.class)).thenReturn(Mono.just(
                new ResponseEntity<>("", HttpStatus.OK)
        ));

        RssService service = new RssService(webClient);
        Exception ex = assertThrows(RssApiException.class, () -> service.getRssChannelInfo("url"));
        assertEquals("Parsing of response from URL: url failed.", ex.getMessage());
    }
}
