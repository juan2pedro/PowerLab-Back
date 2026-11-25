package com.jpmt.powerlab.services;

import com.jpmt.powerlab.exceptions.ResourceNotFoundException;
import com.jpmt.powerlab.models.domain.Bodyweight;
import com.jpmt.powerlab.models.dto.bodyweight.BodyweightRequest;
import com.jpmt.powerlab.models.dto.bodyweight.BodyweightResponse;
import com.jpmt.powerlab.repositories.BodyweightRepository;
import com.jpmt.powerlab.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BodyweightServiceImpl implements BodyweightService {

    private final BodyweightRepository repository;
    private final CurrentUserProvider currentUserProvider;

    public BodyweightServiceImpl(BodyweightRepository repository, CurrentUserProvider currentUserProvider) {
        this.repository = repository;
        this.currentUserProvider = currentUserProvider;
    }

    @Override
    public BodyweightResponse record(BodyweightRequest request) {
        Long ownerId = currentUserProvider.currentUserId();
        // Un pesaje por dia: si ya existe, se actualiza (upsert) en vez de duplicar.
        Bodyweight entity = repository
                .findTopByUser_IdAndDateLessThanEqualOrderByDateDesc(ownerId, request.date())
                .filter(bw -> bw.getDate().equals(request.date()))
                .orElseGet(() -> Bodyweight.builder()
                        .user(currentUserProvider.currentUser())
                        .date(request.date())
                        .build());
        entity.setWeight(request.weight());
        entity.setNotes(request.notes());
        return toResponse(repository.save(entity));
    }

    @Override
    @Transactional(readOnly = true)
    public List<BodyweightResponse> findMine() {
        return repository.findByUser_IdOrderByDateDesc(currentUserProvider.currentUserId())
                .stream().map(this::toResponse).toList();
    }

    @Override
    public void deleteById(Long id) {
        Bodyweight entity = repository.findByIdAndUser_Id(id, currentUserProvider.currentUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Bodyweight", "id", id));
        repository.delete(entity);
    }

    private BodyweightResponse toResponse(Bodyweight bw) {
        return new BodyweightResponse(bw.getId(), bw.getDate(), bw.getWeight(), bw.getNotes());
    }
}
