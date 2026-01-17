package org.elections.dto;

public record OptionResponse(
        Long id, Long electionId, String name
) {}
