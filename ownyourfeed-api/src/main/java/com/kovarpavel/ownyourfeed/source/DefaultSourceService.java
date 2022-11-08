package com.kovarpavel.ownyourfeed.source;

import com.kovarpavel.ownyourfeed.authentication.UserEntity;
import com.kovarpavel.ownyourfeed.authentication.UserRepository;
import com.kovarpavel.ownyourfeed.exception.SourceNotFoundException;
import com.kovarpavel.ownyourfeed.exception.UserNotFoundException;
import com.kovarpavel.ownyourfeed.rss.RssSourceService;
import com.kovarpavel.ownyourfeed.source.dto.NewSourceDTO;
import com.kovarpavel.ownyourfeed.source.dto.SourceDTO;
import com.kovarpavel.ownyourfeed.source.dto.SourceDetailsDTO;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class DefaultSourceService implements SourceService {

    private final SourceRepository sourceRepository;
    private final UserRepository userRepository;
    private final RssSourceService rssService;

    public DefaultSourceService(SourceRepository sourceRepository, UserRepository userRepository, RssSourceService rssService) {
        this.sourceRepository = sourceRepository;
        this.userRepository = userRepository;
        this.rssService = rssService;
    }

    @Override
    public List<SourceDTO> getSources() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        List<SourceEntity> result = sourceRepository.findSourcesByUsersUsername(username);
        return result.stream()
                .map(sourceEntity ->
                        new SourceDTO(sourceEntity.getId(),
                                sourceEntity.getTitle(),
                                sourceEntity.getDescription(),
                                sourceEntity.getUrl()))
                .toList();
    }

    @Override
    public SourceDTO addSource(NewSourceDTO newSourceDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        Optional<SourceEntity> sourceEntityOptional = sourceRepository.findByUrl(newSourceDTO.url());
        if (sourceEntityOptional.isPresent()) {
            SourceEntity addedSource = sourceEntityOptional.get();
            userRepository.save(addSourceToUser(user, addedSource));
            return new SourceDTO(
                    addedSource.getId(),
                    addedSource.getTitle(),
                    addedSource.getDescription(),
                    addedSource.getUrl());
        } else {
            SourceDetailsDTO newSourceDetails = rssService.getRssChannelInfo(newSourceDTO.url());
            SourceEntity newSource = sourceRepository.save(new SourceEntity(
                    newSourceDetails.title(),
                    newSourceDTO.url(),
                    newSourceDetails.description()
            ));
            userRepository.save(addSourceToUser(user, newSource));
            return new SourceDTO(
                    newSource.getId(),
                    newSource.getTitle(),
                    newSource.getDescription(),
                    newSource.getUrl());
        }
    }

    @Override
    public Long removeSource(Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User " + username + " not found"));

        SourceEntity source = sourceRepository.findById(id)
                .orElseThrow(() -> new SourceNotFoundException("Source with id: " + id + " not found"));

        Set<SourceEntity> userSources = user.getSources();
        userSources.remove(source);
        user.setSources(userSources);
        userRepository.save(user);

        return id;
    }

    private UserEntity addSourceToUser(UserEntity user, SourceEntity source) {
        Set<SourceEntity> userSources = user.getSources();
        userSources.add(source);
        user.setSources(userSources);
        return  user;
    }

}
