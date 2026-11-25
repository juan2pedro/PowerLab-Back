package com.jpmt.powerlab.security;

import com.jpmt.powerlab.models.domain.User;

public interface CurrentUserProvider {
    Long currentUserId();
    User currentUser();
}
