package document_service.v1.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class DocumentMetrics {

    private final MeterRegistry meterRegistry;

    public DocumentMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementDocumentsCreated(String status) {
        meterRegistry
                .counter("documents.created", "status", status)
                .increment();
    }

    public void incrementDocumentCreationFailed(String reason) {
        meterRegistry
                .counter("documents.creation.failed", "reason", reason)
                .increment();
    }

    public void incrementDocumentEventPublished(String eventType) {
        meterRegistry
                .counter("document.events.published", "eventType", eventType)
                .increment();
    }

    public void incrementDocumentEventPublishFailed(String eventType) {
        meterRegistry
                .counter("document.events.publish.failed", "eventType", eventType)
                .increment();
    }

    public void recordDocumentCreationDuration(long durationInNanos) {
        Timer.builder("document.creation.duration")
                .description("Time spent creating and publishing a document")
                .register(meterRegistry)
                .record(durationInNanos, TimeUnit.NANOSECONDS);
    }
}