package processing_service.v1.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class ProcessingMetrics {

    private final MeterRegistry meterRegistry;

    public ProcessingMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    public void incrementDocumentEventsConsumed(String eventType) {
        meterRegistry
                .counter("document.events.consumed", "eventType", eventType)
                .increment();
    }

    public void incrementDocumentProcessingCompleted(String status) {
        meterRegistry
                .counter("document.processing.completed", "status", status)
                .increment();
    }

    public void incrementDocumentProcessingFailed(String reason) {
        meterRegistry
                .counter("document.processing.failed", "reason", reason)
                .increment();
    }

    public void recordDocumentProcessingDuration(long durationInNanos) {
        Timer.builder("document.processing.duration")
                .description("Time spent processing a document event")
                .register(meterRegistry)
                .record(durationInNanos, TimeUnit.NANOSECONDS);
    }
}
