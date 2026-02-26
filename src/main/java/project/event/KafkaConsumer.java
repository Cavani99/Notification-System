package project.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import project.event.payloads.CreateNotificationRequest;
import project.event.payloads.CreateUserRequest;
import project.web.NotificationController;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaConsumer {
    private final NotificationController notificationController;

    public KafkaConsumer(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    @KafkaListener(topics = "user-added-event.v1", groupId = "notification-system")
    public void consumeUserAddedEvent(CreateUserRequest request) {
        notificationController.createUser(request);
    }

    @KafkaListener(topics = "notification-save-event.v1", groupId = "notification-system")
    public void consumeNotificationSaveEvent(CreateNotificationRequest request) {
        notificationController.createNotification(request);
    }
}
