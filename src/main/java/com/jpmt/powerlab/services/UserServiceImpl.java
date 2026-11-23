package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.User;
import com.jpmt.powerlab.models.dto.user.RegisterRequest;
import com.jpmt.powerlab.models.dto.user.UserResponse;
import com.jpmt.powerlab.models.mappers.UserMapper;
import com.jpmt.powerlab.repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements  UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.userMapper = userMapper;
    }


    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream().map(userMapper::toResponse).collect(java.util.stream.Collectors.toList());
    }

    @Transactional
    public Optional<UserResponse> saveUser(RegisterRequest user) {
        User userEntity = userMapper.toEntity(user);
        userEntity.setCoach(false);
        userEntity.setPasswordHash(passwordEncoder.encode(user.password()));
       Optional< User> savedUserOptional = Optional.of(userRepository.save(userEntity));
        User savedUser = savedUserOptional.orElseThrow(() -> new RuntimeException("Failed to save user"));
       return Optional.ofNullable(userMapper.toResponse(savedUser));
    }

    @Transactional(readOnly = true)
    public Boolean  existByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Transactional(readOnly = true)
    public Boolean  existByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

}