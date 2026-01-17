package org.elections.dto;

import org.elections.domain.ElectionStatus;

import java.time.Instant;

public record ElectionListItem(
        Long id, String name, Instant startAt, Instant endAt, ElectionStatus status
) {}
