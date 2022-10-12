package com.kovarpavel.ownyourfeed.source;

import com.kovarpavel.ownyourfeed.authentication.UserEntity;
import com.kovarpavel.ownyourfeed.authentication.UserRepository;
import com.kovarpavel.ownyourfeed.exception.SourceNotFoundException;
import com.kovarpavel.ownyourfeed.exception.UserNotFoundException;
import com.kovarpavel.ownyourfeed.rss.RssApiException;
import com.kovarpavel.ownyourfeed.rss.RssService;
import com.kovarpavel.ownyourfeed.source.dto.NewSourceDTO;
import com.kovarpavel.ownyourfeed.source.dto.SourceDTO;
import com.kovarpavel.ownyourfeed.source.dto.SourceDetailsDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringJUnitConfig
@ExtendWith(MockitoExtension.class)
public class SourceServiceTest {

    @MockBean
    private RssService rssService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private SourceRepository sourceRepository;

    @Test
    @WithMockUser("user1")
    void getSources_Success() {
        var sourceEntities = List.of(new SourceEntity("title", "url", "description"));
        when(sourceRepository.findSourcesByUsersUsername("user1")).thenReturn(sourceEntities);

        SourceService sourceService = new DefaultSourceService(sourceRepository, userRepository, rssService);
        var sources = sourceService.getSources();

        assertEquals(List.of(new SourceDTO(null, "title", "description", "url")), sources);
    }

    @Test
    @WithMockUser("user1")
    void addSource_userNotExistInDatabase() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        SourceService sourceService = new DefaultSourceService(sourceRepository, userRepository, rssService);
        assertThrows(UserNotFoundException.class, () -> sourceService.addSource(new NewSourceDTO("")));
    }

    @Test
    @WithMockUser("user1")
    void addSource_rssServiceException() {
        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(new UserEntity("user1", "email@email.com", "password")));
        when(rssService.getRssChannelInfo("")).thenThrow(RssApiException.class);

        SourceService sourceService = new DefaultSourceService(sourceRepository, userRepository, rssService);
        assertThrows(RssApiException.class, () -> sourceService.addSource(new NewSourceDTO("")));
    }

    @Test
    @WithMockUser("user1")
    void addSource_Success() {
        var user = new UserEntity("user1", "email@email.com", "password");
        user.setSources(new HashSet<>());
        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        var source = new SourceEntity("title", "url", "description");
        source.setId(1L);
        when(sourceRepository.save(any())).thenReturn(source);

        when(rssService.getRssChannelInfo("myUrl"))
                .thenReturn(new SourceDetailsDTO("title", "description", "url"));

        SourceService sourceService = new DefaultSourceService(sourceRepository, userRepository, rssService);
        var result = sourceService.addSource(new NewSourceDTO("myUrl"));

        assertEquals(new SourceDTO(1L, "title", "description", "url"), result);
    }

    @Test
    @WithMockUser("user1")
    void addSource_Success_SourceExists() {
        var user = new UserEntity("user1", "email@email.com", "password");
        user.setSources(new HashSet<>());
        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        var source = new SourceEntity("title", "url", "description");
        source.setId(1L);
        when(sourceRepository.findByUrl("myUrl")).thenReturn(Optional.of(source));

        SourceService sourceService = new DefaultSourceService(sourceRepository, userRepository, rssService);
        var result = sourceService.addSource(new NewSourceDTO("myUrl"));

        assertEquals(new SourceDTO(1L, "title", "description", "url"), result);
    }

    @Test
    @WithMockUser("user1")
    void removeSource_userNotExistInDatabase() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());

        SourceService sourceService = new DefaultSourceService(sourceRepository, userRepository, rssService);
        assertThrows(UserNotFoundException.class, () -> sourceService.removeSource(1L));
    }

    @Test
    @WithMockUser("user1")
    void removeSource_SourceDoesNotExists() {
        var user = new UserEntity("user1", "email@email.com", "password");
        user.setSources(new HashSet<>());
        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(sourceRepository.findById(1L)).thenReturn(Optional.empty());

        SourceService sourceService = new DefaultSourceService(sourceRepository, userRepository, rssService);
        assertThrows(SourceNotFoundException.class, () -> sourceService.removeSource(1L));
    }

    @Test
    @WithMockUser("user1")
    void removeSource_Success() {
        var user = new UserEntity("user1", "email@email.com", "password");
        user.setSources(new HashSet<>());
        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        var source = new SourceEntity("title", "url", "description");
        source.setId(1L);
        when(sourceRepository.findById(1L)).thenReturn(Optional.of(source));

        SourceService sourceService = new DefaultSourceService(sourceRepository, userRepository, rssService);
        var result = sourceService.removeSource(1L);

        assertEquals(1L, result);
    }
}
