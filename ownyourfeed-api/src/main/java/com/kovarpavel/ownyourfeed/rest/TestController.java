package com.kovarpavel.ownyourfeed.rest;

import com.kovarpavel.ownyourfeed.dto.SourceInfoDTO;
import com.kovarpavel.ownyourfeed.rss.RssService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private final RssService rssService;

    public TestController(RssService rssService) {
        this.rssService = rssService;
    }

    @GetMapping
    public SourceInfoDTO testApi() throws Exception {
       //String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return rssService.getRssChannelInfo("https://www.root.cz/rss/clank/");
    }

}
