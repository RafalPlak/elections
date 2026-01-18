package org.elections.service;

import jakarta.transaction.Transactional;
import org.elections.domain.*;
import org.elections.dto.CastVoteRequest;
import org.elections.dto.VoteResponse;
import org.elections.exception.BadRequestException;
import org.elections.exception.ConflictException;
import org.elections.exception.NotFoundException;
import org.elections.repository.ElectionOptionRepository;
import org.elections.repository.ElectionRepository;
import org.elections.repository.VoteRepository;
import org.elections.repository.VoterRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class VotingService {
    private final VoterRepository voters;
    private final ElectionRepository elections;
    private final ElectionOptionRepository options;
    private final VoteRepository votes;

    public VotingService(VoterRepository voters, ElectionRepository elections,
                         ElectionOptionRepository options, VoteRepository votes) {
        this.voters = voters;
        this.elections = elections;
        this.options = options;
        this.votes = votes;
    }

    @CacheEvict(cacheNames = "electionResults", key = "#electionId")
    @Transactional
    public VoteResponse castVote(Long electionId, CastVoteRequest req) {
        Voter voter = voters.findById(req.voterId())
                .orElseThrow(() -> new NotFoundException("Voter not found"));
        if (voter.isBlocked()) throw new ConflictException("Voter is blocked");

        Election election = elections.findById(electionId)
                .orElseThrow(() -> new NotFoundException("Election not found"));
        if (election.getStatus() != ElectionStatus.OPEN) throw new ConflictException("Election is not OPEN");

        ElectionOption option = options.findByIdAndElectionId(req.optionId(), electionId)
                .orElseThrow(() -> new BadRequestException("Option does not belong to this election"));

        if (votes.existsByVoterIdAndElectionId(voter.getId(), electionId)) {
            throw new ConflictException("Voter has already voted in this election");
        }

        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setElection(election);
        vote.setOption(option);
        vote.setCastAt(Instant.now());

        try {
            vote = votes.save(vote);
        } catch (DataIntegrityViolationException ex) {
            throw new ConflictException("Voter has already voted in this election");
        }

        return new VoteResponse(vote.getId(), voter.getId(), electionId, option.getId(), vote.getCastAt());
    }
}
