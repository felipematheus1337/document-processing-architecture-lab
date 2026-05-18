package org.acme.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.context.Scope;
import io.smallrye.faulttolerance.api.CircuitBreakerName;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.client.ProcessingServiceClient;
import org.acme.domain.ProcessingEventResponse;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@ApplicationScoped
public class ExportJobService {

    private static final Logger LOG = Logger.getLogger(ExportJobService.class);
    private static final DateTimeFormatter FILE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

    @Inject
    @RestClient
    ProcessingServiceClient processingServiceClient;

    @Inject
    LocalFileStorageService localFileStorageService;

    @Inject
    ObjectMapper objectMapper;



    @Retry(maxRetries = 2, delay = 1000)
    @Timeout(5000)
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10000)
    @CircuitBreakerName("processing-service-export")
    public void exportCompletedEvents() {
        long startedAt = System.currentTimeMillis();

        LOG.info("event=export_job_started");

        List<ProcessingEventResponse> completedEvents = processingServiceClient.findCompleted();

        if (completedEvents == null || completedEvents.isEmpty()) {
            LOG.info("event=export_job_finished status=NO_RECORDS recordsCount=0");
            return;
        }

        LOG.infof("event=completed_events_found recordsCount=%d", completedEvents.size());

        String fileName = "processing-events-" +
                LocalDateTime.now().format(FILE_DATE_FORMAT) +
                ".json";

        String content = toJson(completedEvents);

        Path savedFile = localFileStorageService.save(fileName, content);

        long durationMs = System.currentTimeMillis() - startedAt;

        LOG.infof(
                "event=export_file_created status=SUCCESS recordsCount=%d fileName=%s exportPath=%s durationMs=%d",
                completedEvents.size(),
                fileName,
                savedFile.toAbsolutePath(),
                durationMs
        );
    }

    private String toJson(List<ProcessingEventResponse> events) {
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(events);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Error converting processing events to JSON", e);
        }
    }
}