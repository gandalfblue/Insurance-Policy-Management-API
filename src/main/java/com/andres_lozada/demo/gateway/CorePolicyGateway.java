package com.andres_lozada.demo.gateway;

import com.andres_lozada.demo.exception.CoreGatewayException;
import com.andres_lozada.demo.gateway.dto.CoreQueryRequest;
import com.andres_lozada.demo.gateway.dto.CoreQueryResponse;
import com.andres_lozada.demo.gateway.dto.EventResponse;
import com.andres_lozada.demo.gateway.dto.SyncRequest;
import com.andres_lozada.demo.gateway.dto.SyncResponse;
import com.andres_lozada.demo.gateway.event.PolicyEvent;

public interface CorePolicyGateway {

    SyncResponse syncWithCore(SyncRequest syncRequest) throws CoreGatewayException;

    CoreQueryResponse queryCore(CoreQueryRequest queryRequest) throws CoreGatewayException;

    EventResponse sendPolicyEvent(PolicyEvent event) throws CoreGatewayException;
}
