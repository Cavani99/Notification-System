package project.event.payloads;

import lombok.*;
import project.model.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationMessage {

    private UUID id;
    private String message;
    private UUID sender;
    private UUID receiver;
    private LocalDateTime createdOn;

    public NotificationMessage(Notification notification) {
        this.id = notification.getId();
        this.message = notification.getMessage();
        this.createdOn = notification.getCreatedOn();
        this.sender = notification.getSender() != null ? notification.getSender().getUserId() : null;
        this.receiver = notification.getReceiver() != null ? notification.getReceiver().getUserId() : null;
    }
}
