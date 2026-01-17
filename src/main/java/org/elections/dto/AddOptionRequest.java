package org.elections.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddOptionRequest(
        @NotBlank @Size(max=255) String name
) {}