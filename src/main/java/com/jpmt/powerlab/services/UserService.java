package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.dto.user.RegisterRequest;
import com.jpmt.powerlab.models.dto.user.UserResponse;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List <UserResponse> getAllUsers();

    Optional<UserResponse> saveUser(RegisterRequest user);

    /** Datos del usuario autenticado (incluye el flag coach). */
    UserResponse me();

    /** Activa la capacidad de coach para el usuario actual. Requiere re-login para refrescar el token. */
    UserResponse enableCoach();

    Boolean  existByEmail(String email);

    Boolean  existByUsername(String username);


}
