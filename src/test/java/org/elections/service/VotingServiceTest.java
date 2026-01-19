package org.elections.service;

import org.elections.domain.*;
import org.elections.dto.CastVoteRequest;
import org.elections.exception.BadRequestException;
import org.elections.exception.ConflictException;
import org.elections.exception.NotFoundException;
import org.elections.repository.ElectionOptionRepository;
import org.elections.repository.ElectionRepository;
import org.elections.repository.VoteRepository;
import org.elections.repository.VoterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VotingServiceTest {
    @Mock
    VoterRepository voters;
    @Mock
    ElectionRepository elections;
    @Mock
    ElectionOptionRepository options;
    @Mock
    VoteRepository votes;

    @InjectMocks VotingService service;

    @Test
    void castVote_shouldThrowNotFound_whenVoterMissing() {
        when(voters.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.castVote(10L, new CastVoteRequest(1L, 100L)))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Voter not found");
    }

    @Test
    void castVote_shouldThrow_whenVoterBlocked() {
        Voter v = new Voter();
        v.setId(1L);
        v.setBlocked(true);

        when(voters.findById(1L)).thenReturn(Optional.of(v));

        assertThatThrownBy(() -> service.castVote(10L, new CastVoteRequest(1L, 100L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("blocked");
    }

    @Test
    void castVote_shouldThrow_whenElectionNotOpen() {
        Voter v = new Voter();
        v.setId(1L);
        v.setBlocked(false);

        Election e = new Election();
        e.setId(10L);
        e.setStatus(ElectionStatus.DRAFT);

        when(voters.findById(1L)).thenReturn(Optional.of(v));
        when(elections.findById(10L)).thenReturn(Optional.of(e));

        assertThatThrownBy(() -> service.castVote(10L, new CastVoteRequest(1L, 100L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("not OPEN");
    }

    @Test
    void castVote_shouldThrow_whenOptionNotInElection() {
        Voter v = new Voter();
        v.setId(1L);

        Election e = new Election();
        e.setId(10L);
        e.setStatus(ElectionStatus.OPEN);

        when(voters.findById(1L)).thenReturn(Optional.of(v));
        when(elections.findById(10L)).thenReturn(Optional.of(e));
        when(options.findByIdAndElectionId(100L, 10L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.castVote(10L, new CastVoteRequest(1L, 100L)))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Option does not belong");
    }

    @Test
    void castVote_shouldThrow_whenAlreadyVoted() {
        Voter v = new Voter();
        v.setId(1L);

        Election e = new Election();
        e.setId(10L);
        e.setStatus(ElectionStatus.OPEN);

        ElectionOption opt = new ElectionOption();
        opt.setId(100L);
        opt.setElection(e);

        when(voters.findById(1L)).thenReturn(Optional.of(v));
        when(elections.findById(10L)).thenReturn(Optional.of(e));
        when(options.findByIdAndElectionId(100L, 10L)).thenReturn(Optional.of(opt));
        when(votes.existsByVoterIdAndElectionId(1L, 10L)).thenReturn(true);

        assertThatThrownBy(() -> service.castVote(10L, new CastVoteRequest(1L, 100L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already voted");

        verify(votes, never()).save(any());
    }

    @Test
    void castVote_shouldReturnCreatedVote_whenOk() {
        Voter v = new Voter();
        v.setId(1L);

        Election e = new Election();
        e.setId(10L);
        e.setStatus(ElectionStatus.OPEN);

        ElectionOption opt = new ElectionOption();
        opt.setId(100L);
        opt.setElection(e);

        when(voters.findById(1L)).thenReturn(Optional.of(v));
        when(elections.findById(10L)).thenReturn(Optional.of(e));
        when(options.findByIdAndElectionId(100L, 10L)).thenReturn(Optional.of(opt));
        when(votes.existsByVoterIdAndElectionId(1L, 10L)).thenReturn(false);

        when(votes.save(any(Vote.class))).thenAnswer(inv -> {
            Vote vote = inv.getArgument(0);
            vote.setId(999L);
            return vote;
        });

        var resp = service.castVote(10L, new CastVoteRequest(1L, 100L));

        assertThat(resp.id()).isEqualTo(999L);
        assertThat(resp.voterId()).isEqualTo(1L);
        assertThat(resp.electionId()).isEqualTo(10L);
        assertThat(resp.optionId()).isEqualTo(100L);
        assertThat(resp.castAt()).isNotNull();

        verify(votes).save(any(Vote.class));
    }

    @Test
    void castVote_shouldMapDataIntegrityViolation_toConflict() {
        Voter v = new Voter();
        v.setId(1L);

        Election e = new Election();
        e.setId(10L);
        e.setStatus(ElectionStatus.OPEN);

        ElectionOption opt = new ElectionOption();
        opt.setId(100L);
        opt.setElection(e);

        when(voters.findById(1L)).thenReturn(Optional.of(v));
        when(elections.findById(10L)).thenReturn(Optional.of(e));
        when(options.findByIdAndElectionId(100L, 10L)).thenReturn(Optional.of(opt));
        when(votes.existsByVoterIdAndElectionId(1L, 10L)).thenReturn(false);

        when(votes.save(any(Vote.class))).thenThrow(new DataIntegrityViolationException("uq"));

        assertThatThrownBy(() -> service.castVote(10L, new CastVoteRequest(1L, 100L)))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("already voted");
    }
}