package com.andres_lozada.demo.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SyncStatistics {

    private int policiesCreated;
    private int policiesUpdated;
    private int policiesSkipped;
    private int risksCreated;
    private int risksUpdated;
    private int risksSkipped;
}
