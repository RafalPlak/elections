package org.elections.dto;

public record VoterListItem(
        Long id, String firstName, String lastName, String pesel, boolean blocked
) {}
