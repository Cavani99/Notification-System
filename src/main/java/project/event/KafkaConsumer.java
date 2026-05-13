package project.event;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import project.event.payloads.CreateNotificationRequest;
import project.event.payloads.CreateUserRequest;
import project.event.payloads.NotificationMessage;
import project.model.Notification;
import project.service.NotificationService;
import project.web.NotificationController;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "kafka.enabled", havingValue = "true")
public class KafkaConsumer {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationController notificationController;

    private final NotificationService notificationService;

    public KafkaConsumer(SimpMessagingTemplate messagingTemplate, NotificationController notificationController, NotificationService notificationService) {
        this.messagingTemplate = messagingTemplate;
        this.notificationController = notificationController;
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "user-added-event.v1", groupId = "notification-system")
    public void consumeUserAddedEvent(CreateUserRequest request) {
        notificationController.createUser(request);
    }

    @KafkaListener(topics = "notification-save-event.v1", groupId = "notification-system")
    public void consumeNotificationSaveEvent(CreateNotificationRequest request) {
        notificationController.createNotification(request);
    }

    @KafkaListener(topics = "notification-chat-message-event.v1")
    public void consumeChatMessageEvent(CreateNotificationRequest request) {
        Notification notification = notificationService.addNotification(request);
        NotificationMessage notificationMessage = new NotificationMessage(notification);

        messagingTemplate.convertAndSendToUser(
                request.getSenderEmail(),
                "/queue/messages",
                notificationMessage
        );

        messagingTemplate.convertAndSendToUser(
                request.getReceiverEmail(),
                "/queue/messages",
                notificationMessage
        );
    }
}
