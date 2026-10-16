package com.andres_lozada.demo.gateway;

import com.andres_lozada.demo.gateway.dto.CoreQueryRequest;
import com.andres_lozada.demo.gateway.dto.CoreQueryResponse;
import com.andres_lozada.demo.gateway.dto.EventResponse;
import com.andres_lozada.demo.gateway.dto.SyncRequest;
import com.andres_lozada.demo.gateway.dto.SyncResponse;
import com.andres_lozada.demo.gateway.dto.SyncStatistics;
import com.andres_lozada.demo.gateway.event.CoreEvent;
import com.andres_lozada.demo.gateway.event.CoreSyncEvent;
import com.andres_lozada.demo.gateway.event.PolicyEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@Profile("test")
public class MockCoreGateway implements CorePolicyGateway {

    private final List<CoreEvent> capturedEvents = new ArrayList<>();
    private boolean unavailable;

    @Override
    public SyncResponse syncWithCore(SyncRequest syncRequest) {
        if (unavailable) {
            throw new com.andres_lozada.demo.exception.CoreGatewayException("CORE unavailable");
        }
        capturedEvents.add(CoreSyncEvent.builder()
                .eventType("SYNC")
                .syncType(syncRequest.getSyncType())
                .timestamp(LocalDateTime.now())
                .requestId(UUID.randomUUID())
                .build());
        return SyncResponse.builder()
                .syncId(UUID.randomUUID())
                .status("SUCCESS")
                .statistics(SyncStatistics.builder()
                        .policiesCreated(10)
                        .policiesUpdated(5)
                        .policiesSkipped(0)
                        .risksCreated(2)
                        .risksUpdated(1)
                        .risksSkipped(0)
                        .build())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public CoreQueryResponse queryCore(CoreQueryRequest queryRequest) {
        if (unavailable) {
            throw new com.andres_lozada.demo.exception.CoreGatewayException("CORE unavailable");
        }
        return CoreQueryResponse.builder()
                .queryId(UUID.randomUUID())
                .type(queryRequest.getType())
                .results(new ArrayList<>())
                .resultCount(0)
                .status("SUCCESS")
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Override
    public EventResponse sendPolicyEvent(PolicyEvent event) {
        capturedEvents.add(event);
        return EventResponse.builder()
                .eventId(UUID.randomUUID())
                .status("PROCESSED")
                .message("ok")
                .timestamp(LocalDateTime.now())
                .build();
    }

    public List<CoreEvent> getCapturedEvents() {
        return new ArrayList<>(capturedEvents);
    }

    public void reset() {
        capturedEvents.clear();
        unavailable = false;
    }

    public void setUnavailable(boolean unavailable) {
        this.unavailable = unavailable;
    }
}
