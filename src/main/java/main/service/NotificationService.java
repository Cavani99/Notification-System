package main.service;

import main.model.Notification;
import main.model.User;
import main.repository.NotificationRepository;
import main.repository.UserRepository;
import main.web.dto.CreateNotificationRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    public Notification addNotification(CreateNotificationRequest createNotificationRequest) {
        Notification notification = new Notification();

        notification.setTitle(createNotificationRequest.getTitle());
        notification.setMessage(createNotificationRequest.getMessage());
        notification.setLink(createNotificationRequest.getLink());
        notification.setType(createNotificationRequest.getType());

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
}
