package com.andres_lozada.demo.gateway.dto;

import com.andres_lozada.demo.enums.SyncType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncRequest {

    @NotNull
    private SyncType syncType;

    @Builder.Default
    private Integer timeoutSeconds = 30;
}
