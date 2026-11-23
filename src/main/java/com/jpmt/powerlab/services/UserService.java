package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.dto.user.RegisterRequest;
import com.jpmt.powerlab.models.dto.user.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List <UserResponse> getAllUsers();

    Optional<UserResponse> saveUser(RegisterRequest user);

    Boolean  existByEmail(String email);

    Boolean  existByUsername(String username);


}
