package org.elections.service;

import jakarta.transaction.Transactional;
import org.elections.domain.Voter;
import org.elections.dto.CreateVoterRequest;
import org.elections.dto.VoterListItem;
import org.elections.dto.VoterResponse;
import org.elections.exception.ConflictException;
import org.elections.exception.NotFoundException;
import org.elections.repository.VoterRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class VoterService {
    private final VoterRepository repo;

    public VoterService(VoterRepository repo) { this.repo = repo; }

    @Transactional
    public VoterResponse create(CreateVoterRequest req) {
        if (repo.existsByPesel(req.pesel())) {
            throw new ConflictException("Voter with given PESEL already exists");
        }

        Voter v = new Voter();
        v.setFirstName(req.firstName());
        v.setLastName(req.lastName());
        v.setPesel(req.pesel());
        v.setBlocked(false);

        v = repo.save(v);
        return toDto(v);
    }

    public VoterResponse get(Long id) {
        return repo.findById(id).map(this::toDto)
                .orElseThrow(() -> new NotFoundException("Voter not found"));
    }

    @Transactional
    public VoterResponse setBlocked(Long id, boolean blocked) {
        Voter v = repo.findById(id).orElseThrow(() -> new NotFoundException("Voter not found"));
        v.setBlocked(blocked);
        return toDto(v);
    }

    public Page<VoterListItem> list(Pageable pageable) {
        return repo.findAll(pageable).map(v ->
                new VoterListItem(v.getId(), v.getFirstName(), v.getLastName(), v.getPesel(), v.isBlocked())
        );
    }

    private VoterResponse toDto(Voter v) {
        return new VoterResponse(v.getId(), v.getFirstName(), v.getLastName(), v.getPesel(), v.isBlocked());
    }
}
