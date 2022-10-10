package com.kovarpavel.ownyourfeed.source;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SourceRepository extends JpaRepository<SourceEntity, Long> {
    List<SourceEntity> findSourcesByUsersUsername(String username);
    Optional<SourceEntity> findByUrl(String url);
}
