package org.elections.service;

import jakarta.transaction.Transactional;
import org.elections.domain.Election;
import org.elections.domain.ElectionOption;
import org.elections.domain.ElectionStatus;
import org.elections.dto.*;
import org.elections.exception.ConflictException;
import org.elections.exception.NotFoundException;
import org.elections.repository.ElectionOptionRepository;
import org.elections.repository.ElectionRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ElectionService {
    private final ElectionRepository elections;
    private final ElectionOptionRepository options;

    public ElectionService(ElectionRepository elections, ElectionOptionRepository options) {
        this.elections = elections;
        this.options = options;
    }

    @Transactional
    public ElectionResponse create(CreateElectionRequest req) {
        Election e = new Election();
        e.setName(req.name());
        e.setStartAt(req.startAt());
        e.setEndAt(req.endAt());
        e.setStatus(ElectionStatus.DRAFT);

        e = elections.save(e);
        return toDto(e);
    }

    public ElectionResponse get(Long id) {
        Election e = elections.findById(id).orElseThrow(() -> new NotFoundException("Election not found"));
        return toDto(e);
    }

    public Page<ElectionListItem> list(Optional<ElectionStatus> status, Pageable pageable) {
        Page<Election> page = status
                .map(s -> elections.findByStatus(s, pageable))
                .orElseGet(() -> elections.findAll(pageable));

        return page.map(e -> new ElectionListItem(
                e.getId(), e.getName(), e.getStartAt(), e.getEndAt(), e.getStatus()
        ));
    }

    @CacheEvict(cacheNames = "electionOptions", key = "#electionId")
    @Transactional
    public OptionResponse addOption(Long electionId, AddOptionRequest req) {
        Election e = elections.findById(electionId)
                .orElseThrow(() -> new NotFoundException("Election not found"));

        if (e.getStatus() != ElectionStatus.DRAFT) {
            throw new ConflictException("Options can be modified only in DRAFT status");
        }

        ElectionOption opt = new ElectionOption();
        opt.setElection(e);
        opt.setName(req.name());

        opt = options.save(opt);
        return new OptionResponse(opt.getId(), e.getId(), opt.getName());
    }

    @Cacheable(cacheNames = "electionOptions", key = "#electionId")
    public List<OptionResponse> listOptions(Long electionId) {
        return options.findByElectionId(electionId).stream()
                .map(o -> new OptionResponse(o.getId(), o.getElection().getId(), o.getName()))
                .toList();
    }

    @Transactional
    public ElectionResponse open(Long electionId) {
        Election e = elections.findById(electionId)
                .orElseThrow(() -> new NotFoundException("Election not found"));

        if (e.getStatus() != ElectionStatus.DRAFT) throw new ConflictException("Election is not in DRAFT");
        if (options.findByElectionId(electionId).isEmpty()) throw new ConflictException("Election must have options");

        e.setStatus(ElectionStatus.OPEN);
        return toDto(e);
    }

    @Transactional
    public ElectionResponse close(Long electionId) {
        Election e = elections.findById(electionId)
                .orElseThrow(() -> new NotFoundException("Election not found"));

        if (e.getStatus() != ElectionStatus.OPEN) throw new ConflictException("Election is not OPEN");

        e.setStatus(ElectionStatus.CLOSED);
        return toDto(e);
    }

    private ElectionResponse toDto(Election e) {
        return new ElectionResponse(e.getId(), e.getName(), e.getStartAt(), e.getEndAt(), e.getStatus());
    }
}
