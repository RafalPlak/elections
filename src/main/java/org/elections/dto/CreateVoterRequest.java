package org.elections.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateVoterRequest(
        @NotBlank @Size(max=100) String firstName,
        @NotBlank @Size(max=100) String lastName,
        @NotBlank @Pattern(regexp="\\d{11}") String pesel
) {}
