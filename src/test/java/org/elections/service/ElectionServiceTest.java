package org.elections.service;

import org.elections.dto.AddOptionRequest;
import org.elections.dto.CreateElectionRequest;
import org.elections.exception.ConflictException;
import org.elections.exception.NotFoundException;
import org.elections.repository.ElectionOptionRepository;
import org.elections.repository.ElectionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.elections.domain.*;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.*;

class ElectionServiceTest {
    @Mock
    ElectionRepository elections;
    @Mock
    ElectionOptionRepository options;
    @InjectMocks ElectionService service;

    @Test
    void create_shouldCreateDraftElection() {
        when(elections.save(any(Election.class))).thenAnswer(inv -> {
            Election e = inv.getArgument(0);
            e.setId(5L);
            return e;
        });

        var resp = service.create(new CreateElectionRequest("Wybory 2025", null, null));

        assertThat(resp.id()).isEqualTo(5L);
        assertThat(resp.status()).isEqualTo(ElectionStatus.DRAFT);
        assertThat(resp.name()).isEqualTo("Wybory 2025");
    }

    @Test
    void addOption_shouldThrow_whenElectionNotFound() {
        when(elections.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.addOption(1L, new AddOptionRequest("X")))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void addOption_shouldThrow_whenNotDraft() {
        Election e = new Election();
        e.setId(1L);
        e.setStatus(ElectionStatus.OPEN);

        when(elections.findById(1L)).thenReturn(Optional.of(e));

        assertThatThrownBy(() -> service.addOption(1L, new AddOptionRequest("Kandydat")))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("DRAFT");
    }

    @Test
    void open_shouldThrow_whenNoOptions() {
        Election e = new Election();
        e.setId(1L);
        e.setStatus(ElectionStatus.DRAFT);

        when(elections.findById(1L)).thenReturn(Optional.of(e));
        when(options.findByElectionId(1L)).thenReturn(List.of());

        assertThatThrownBy(() -> service.open(1L))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("must have options");
    }

    @Test
    void open_shouldSetStatusOpen_whenDraftAndHasOptions() {
        Election e = new Election();
        e.setId(1L);
        e.setStatus(ElectionStatus.DRAFT);

        ElectionOption opt = new ElectionOption();
        opt.setId(10L);
        opt.setElection(e);
        opt.setName("A");

        when(elections.findById(1L)).thenReturn(Optional.of(e));
        when(options.findByElectionId(1L)).thenReturn(List.of(opt));

        var resp = service.open(1L);

        assertThat(resp.status()).isEqualTo(ElectionStatus.OPEN);
        assertThat(e.getStatus()).isEqualTo(ElectionStatus.OPEN);
    }
}