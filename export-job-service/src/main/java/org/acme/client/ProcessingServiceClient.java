package org.acme.client;

import org.acme.domain.ProcessingEventResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import java.util.List;

@RegisterRestClient(configKey = "processing-service")
public interface ProcessingServiceClient {

    @GET
    @Path("/api/v1/processing-events/completed")
    List<ProcessingEventResponse> findCompleted();
}