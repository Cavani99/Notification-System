package main;

import project.Application;
import project.model.Notification;
import project.model.NotificationType;
import project.model.User;
import project.repository.NotificationRepository;
import project.repository.UserRepository;
import project.service.NotificationService;
import project.event.payloads.CreateNotificationRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = Application.class)
@ActiveProfiles("test")
@Transactional
public class NotificationCreationIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    void addNotification_savesNotificationAndUsers() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setTitle("Welcome");
        req.setMessage("Hello there");
        req.setLink("/test");
        req.setLinkTitle("Click");
        req.setType(NotificationType.REQUEST);
        req.setSenderId(senderId);
        req.setReceiverId(receiverId);

        Notification saved = notificationService.addNotification(req);

        Optional<Notification> dbNotification = notificationRepository.findById(saved.getId());
        assertTrue(dbNotification.isPresent(), "Notification should be saved to DB");

        Optional<User> sender = userRepository.findByUserId(senderId);
        assertTrue(sender.isPresent(), "Sender should be created in DB");

        Optional<User> receiver = userRepository.findByUserId(receiverId);
        assertTrue(receiver.isPresent(), "Receiver should be created in DB");

        Notification n = dbNotification.get();
        assertEquals("Welcome", n.getTitle());
        assertEquals("Hello there", n.getMessage());
        assertEquals(sender.get().getUserId(), n.getSender().getUserId());
        assertEquals(receiver.get().getUserId(), n.getReceiver().getUserId());
        assertFalse(n.isCompleted());
        assertNotNull(n.getCreatedOn());
    }
}
