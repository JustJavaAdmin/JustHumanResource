package com.justjava.humanresource.request.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FreeRouteForwardCommand {
    @NotBlank
    private String taskId;

    @NotNull
    private Long toEmployeeId;

    private String comment;
}