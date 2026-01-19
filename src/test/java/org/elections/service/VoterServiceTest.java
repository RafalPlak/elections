package org.elections.service;

import org.elections.domain.Voter;
import org.elections.dto.CreateVoterRequest;
import org.elections.exception.ConflictException;
import org.elections.exception.NotFoundException;
import org.elections.repository.VoterRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VoterServiceTest {
    @Mock
    VoterRepository repo;
    @InjectMocks
    VoterService service;

    @Test
    void create_shouldCreateVoter_whenPeselUnique() {
        when(repo.existsByPesel("12345678901")).thenReturn(false);
        when(repo.save(any(Voter.class))).thenAnswer(inv -> {
            Voter v = inv.getArgument(0);
            v.setId(10L);
            return v;
        });

        var resp = service.create(new CreateVoterRequest("Jan", "Kowalski", "12345678901"));

        assertThat(resp.id()).isEqualTo(10L);
        assertThat(resp.blocked()).isFalse();
        assertThat(resp.pesel()).isEqualTo("12345678901");
        verify(repo).save(any(Voter.class));
    }

    @Test
    void create_shouldThrowConflict_whenPeselExists() {
        when(repo.existsByPesel("12345678901")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateVoterRequest("Jan", "K", "12345678901")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("PESEL");

        verify(repo, never()).save(any());
    }

    @Test
    void setBlocked_shouldThrowNotFound_whenMissing() {
        when(repo.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.setBlocked(1L, true))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Voter not found");
    }

    @Test
    void setBlocked_shouldUpdateFlag() {
        Voter v = new Voter();
        v.setId(1L);
        v.setFirstName("A");
        v.setLastName("B");
        v.setPesel("12345678901");
        v.setBlocked(false);

        when(repo.findById(1L)).thenReturn(Optional.of(v));

        var resp = service.setBlocked(1L, true);

        assertThat(resp.blocked()).isTrue();
        assertThat(v.isBlocked()).isTrue();
    }
}