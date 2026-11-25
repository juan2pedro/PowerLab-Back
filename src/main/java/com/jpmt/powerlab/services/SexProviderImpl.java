package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.Sex;
import com.jpmt.powerlab.repositories.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class SexProviderImpl implements SexProvider {

    private final UserRepository userRepository;

    public SexProviderImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Sexo del atleta (alimenta DOTS/IPF GL). Si el usuario no lo tiene fijado, se asume MALE. */
    @Override
    public Sex sex(Long athleteId) {
        Sex sex = userRepository.findById(athleteId)
                .map(user -> user.getSex())
                .orElse(null);
        return sex != null ? sex : Sex.MALE;
    }
}
