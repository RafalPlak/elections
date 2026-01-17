package org.elections.dto;

import jakarta.validation.constraints.NotNull;

public record CastVoteRequest(
        @NotNull Long voterId,
        @NotNull Long optionId
) {}
