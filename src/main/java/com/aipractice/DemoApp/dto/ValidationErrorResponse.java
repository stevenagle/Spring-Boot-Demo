package com.aipractice.DemoApp.dto;

import java.util.List;

public record ValidationErrorResponse(
        String message,
        List<String> errors
) {}

