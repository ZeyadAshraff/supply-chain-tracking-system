package entity.events;

import java.time.LocalDateTime;

public class IssueReportedEvent extends Event {
    private final String issueDescription;
    private final String issueSeverity;

    public IssueReportedEvent(String eventId, LocalDateTime timestamp, String description,
                              String issueDescription, String issueSeverity) {
        super(eventId, timestamp, description);
        this.issueDescription = issueDescription;
        this.issueSeverity = issueSeverity;
    }

    @Override
    public String getEventType() {
        return "ISSUE_REPORTED";
    }

    @Override
    public String getEventDetails() {
        return "issueDescription=" + issueDescription + ";severity=" + issueSeverity;
    }
}
