package com.andres_lozada.demo.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoreSyncResponse {

    private UUID syncId;
    private String status;
    private SyncStatistics statistics;
    private LocalDateTime timestamp;
}
