package org.elections.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.Instant;

public record CreateElectionRequest(
        @NotBlank @Size(max=255) String name,
        Instant startAt,
        Instant endAt
) {}
