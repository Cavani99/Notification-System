package main.web.dto;

import lombok.*;
import main.model.Notification;
import main.model.NotificationType;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {
    private UUID id;
    private String title;
    private String message;
    private String link;
    private NotificationType type;
    private LocalDateTime createdOn;
    private UUID sender;

    public NotificationResponse(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.link = notification.getLink();
        this.type = notification.getType();
        this.createdOn = notification.getCreatedOn();
        this.sender = notification.getSender() != null ? notification.getSender().getId() : null;
    }
}
