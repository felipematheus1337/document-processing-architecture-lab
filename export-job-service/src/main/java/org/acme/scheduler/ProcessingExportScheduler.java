package org.acme.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.service.ExportJobService;
import org.jboss.logging.Logger;
import org.slf4j.MDC;

import java.util.UUID;

@ApplicationScoped
public class ProcessingExportScheduler {

    private static final Logger LOG = Logger.getLogger(ProcessingExportScheduler.class);

    @Inject
    ExportJobService exportJobService;

    @Scheduled(every = "60s")
    void runExportJob() {
        String correlationId = UUID.randomUUID().toString();
        try {
            MDC.put("correlationId", correlationId);
            MDC.put("jobName", "processing-events-export");

            LOG.info("event=export_job_scheduled_triggered");

            LOG.info("event=export_job_scheduled_finished status=SUCCESS");
            exportJobService.exportCompletedEvents();
        } catch (Exception e) {
            LOG.errorf(e,
                    "event=export_job_scheduled_failed errorMessage=%s",
                    e.getMessage()
            );
        }
        finally {
            MDC.clear();
        }
    }
}