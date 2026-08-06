package com.jpmt.powerlab.services;

import com.jpmt.powerlab.models.domain.Sex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SexProviderImpl implements SexProvider {

    @Value("${athlete.sex:MALE}")
    private Sex sex;

    @Override
    public Sex sex(Long athleteId) {
        return sex;
    }
}
