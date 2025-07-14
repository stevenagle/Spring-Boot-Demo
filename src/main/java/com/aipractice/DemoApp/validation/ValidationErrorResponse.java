package com.aipractice.DemoApp.validation;

import java.util.List;

public record ValidationErrorResponse(
        String message,
        List<String> errors
) {}

