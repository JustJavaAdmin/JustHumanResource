package com.justjava.humanresource.request.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FreeRouteSendCommand {
    @NotNull
    private Long toEmployeeId;

    private String comment;
}