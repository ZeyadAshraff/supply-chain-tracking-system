package entity.events;

import java.time.LocalDateTime;

public class InspectionEvent extends Event {
    private final String inspectionStatus;
    private final String inspectionNotes;

    public InspectionEvent(String eventId, LocalDateTime timestamp, String description,
                           String inspectionStatus, String inspectionNotes) {
        super(eventId, timestamp, description);
        this.inspectionStatus = inspectionStatus;
        this.inspectionNotes = inspectionNotes;
    }

    public String getInspectionStatus() {
        return inspectionStatus;
    }

    public String getInspectionNotes() {
        return inspectionNotes;
    }

    @Override
    public String getEventType() {
        return "INSPECTION";
    }

    @Override
    public String getEventDetails() {
        return "inspectionStatus=" + inspectionStatus + ";notes=" + inspectionNotes;
    }
}
