package main.service;

import main.model.Notification;
import main.model.User;
import main.repository.NotificationRepository;
import main.repository.UserRepository;
import main.web.dto.CreateNotificationRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification findById(UUID id) {
        return notificationRepository.findById(id).orElseThrow(() -> new RuntimeException("Notification does not exist!"));
    }

    public Notification addNotification(CreateNotificationRequest createNotificationRequest) {
        Notification notification = new Notification();

        notification.setTitle(createNotificationRequest.getTitle());
        notification.setMessage(createNotificationRequest.getMessage());
        notification.setLink(createNotificationRequest.getLink());
        notification.setType(createNotificationRequest.getType());
        notification.setCompleted(false);

        if (createNotificationRequest.getSenderId() != null) {
            Optional<User> sender = userRepository.findByUserId(createNotificationRequest.getSenderId());
            if (sender.isEmpty()) {
                User user = new User();
                user.setUserId(createNotificationRequest.getSenderId());

                sender = Optional.of(userRepository.save(user));
            }
            notification.setSender(sender.get());
        }

        Optional<User> receiver = userRepository.findByUserId(createNotificationRequest.getReceiverId());
        if (receiver.isEmpty()) {
            User user = new User();
            user.setUserId(createNotificationRequest.getReceiverId());

            receiver = Optional.of(userRepository.save(user));
        }
        notification.setReceiver(receiver.get());

        return notificationRepository.save(notification);
    }

    public void completeNotification(UUID id) {
        Notification notification = findById(id);
        notification.setCompleted(true);
        notificationRepository.save(notification);
    }

    public List<Notification> findAllByUser(UUID userId) {
        return notificationRepository.findAllByReceiverId(userId);
    }
}
