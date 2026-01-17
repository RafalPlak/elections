package org.elections.dto;

import java.time.Instant;

public record VoteResponse(
        Long id, Long voterId, Long electionId, Long optionId, Instant castAt
) {}
