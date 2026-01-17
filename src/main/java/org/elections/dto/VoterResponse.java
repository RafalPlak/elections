package org.elections.dto;

public record VoterResponse(
        Long id, String firstName, String lastName, String pesel, boolean blocked
) {}
