package org.acme.domain;


import java.time.LocalDateTime;

public class ProcessingEventResponse {

    private String id;

    private Long documentId;
    private String title;
    private String ownerName;
    private String fileName;
    private String status;
    private LocalDateTime receivedAt;
    private LocalDateTime processedAt;
    private Integer attempts;
    private String errorMessage;
}
