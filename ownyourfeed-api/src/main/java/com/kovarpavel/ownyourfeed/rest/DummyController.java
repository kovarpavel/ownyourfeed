package com.kovarpavel.ownyourfeed.rest;

import com.kovarpavel.ownyourfeed.entity.User;
import com.kovarpavel.ownyourfeed.repository.UserRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class DummyController {

    private UserRepository userRepository;

    public DummyController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{userId}/source")
    public User getUserSources(@PathVariable Long userId) {
        return userRepository.findById(userId).get();
    }
}
