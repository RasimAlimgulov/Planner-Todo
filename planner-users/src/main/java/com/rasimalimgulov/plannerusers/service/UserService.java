package com.rasimalimgulov.plannerusers.service;

import com.rasimalimgulov.plannerentity.entity.User;
import com.rasimalimgulov.plannerusers.repo.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User addUser(User user) {
        return userRepository.save(user);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public void deleteByUserId(Long id) {
        userRepository.deleteById(id);
    }
    public void deleteByEmail(String email) {
        userRepository.deleteByEmail(email);
    }
    public Optional<User> findUserById(Long id) {
        return userRepository.findById(id);
    }
    public Page<User> findByParams(String username, String email, PageRequest pageRequest) {
        return userRepository.findByParams(username,email,pageRequest);
    }
}
