package org.acme.scheduler;

import io.quarkus.scheduler.Scheduled;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.acme.service.ExportJobService;
import org.jboss.logging.Logger;

@ApplicationScoped
public class ProcessingExportScheduler {

    private static final Logger LOG = Logger.getLogger(ProcessingExportScheduler.class);

    @Inject
    ExportJobService exportJobService;

    @Scheduled(every = "60s")
    void runExportJob() {
        LOG.info("Scheduled export job triggered");
        exportJobService.exportCompletedEvents();
    }
}