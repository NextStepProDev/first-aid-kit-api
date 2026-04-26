package com.firstaidkit.controller.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateLanguageRequest(
        @NotBlank(message = "Language is required")
        @Pattern(regexp = "^(pl|en)$", message = "Language must be 'pl' or 'en'")
        String language
) {}
