package project.event;

import project.event.payloads.CreateUserRequest;
import project.web.NotificationController;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {
    private final NotificationController notificationController;

    public KafkaConsumer(NotificationController notificationController) {
        this.notificationController = notificationController;
    }

    @KafkaListener(topics = "user-added-event.v1", groupId = "notification-system")
    public void consumeUserAddedEvent(CreateUserRequest request) {
        notificationController.createUser(request);
    }
}
