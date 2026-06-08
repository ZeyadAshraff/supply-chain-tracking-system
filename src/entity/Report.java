package entity;

import entity.events.Event;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Report {
    private final String reportId;
    private final LocalDateTime generatedAt;
    private final List<Event> includedEvents;

    public Report(String reportId, List<Event> includedEvents) {
        this.reportId = reportId;
        this.generatedAt = LocalDateTime.now();
        this.includedEvents = new ArrayList<>(includedEvents);
    }

    public String getReportId() {
        return reportId;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public List<Event> getIncludedEvents() {
        return Collections.unmodifiableList(includedEvents);
    }

    public boolean exportPdf() {
        return false;
    }
}
