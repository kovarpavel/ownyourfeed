package com.kovarpavel.ownyourfeed.repository;

import com.kovarpavel.ownyourfeed.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
public interface UserRepository extends JpaRepository<User, Long> {
}
