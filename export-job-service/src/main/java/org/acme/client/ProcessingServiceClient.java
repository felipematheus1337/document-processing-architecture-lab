package org.acme.client;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import org.acme.domain.ProcessingEventResponse;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "processing-service")
public interface ProcessingServiceClient {

    @GET
    @Path("/api/v1/processing-events/completed")
    List<ProcessingEventResponse> findCompleted();
}
