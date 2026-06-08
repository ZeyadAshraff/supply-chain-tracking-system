package entity;

import java.time.LocalDateTime;

public class DeliveryEvidence {
    private final String evidenceId;
    private String filePath;
    private final LocalDateTime timestamp;
    private String verificationStatus;

    public DeliveryEvidence(String evidenceId, String filePath, LocalDateTime timestamp, String verificationStatus) {
        this.evidenceId = evidenceId;
        this.filePath = filePath;
        this.timestamp = timestamp;
        this.verificationStatus = verificationStatus;
    }

    public String getEvidenceId() {
        return evidenceId;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void verify() {
        verificationStatus = "VERIFIED";
    }

    public void reject() {
        verificationStatus = "REJECTED";
    }
}
