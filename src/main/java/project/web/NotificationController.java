package project.web;

import project.event.payloads.NotificationMessage;
import project.model.Notification;
import project.model.User;
import project.service.NotificationService;
import project.service.UserService;
import project.event.payloads.CreateNotificationRequest;
import project.event.payloads.CreateUserRequest;
import project.event.payloads.NotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notification/v1")
public class NotificationController {

    private final NotificationService notificationService;

    private final UserService userService;

    private final Logger logger;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
        this.logger = LoggerFactory.getLogger(NotificationController.class);
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = userService.addUser(createUserRequest);
        logger.info("User {} added in the notification api!", createUserRequest.getUsername());

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/notification")
    public ResponseEntity<Notification> createNotification(@RequestBody CreateNotificationRequest createNotificationRequest) {
        Notification notification = notificationService.addNotification(createNotificationRequest);
        if (notification != null) {
            notificationService.setFullLink(notification.getId());
            logger.info("Notification for {} created!", createNotificationRequest.getTitle());

            return new ResponseEntity<>(notification, HttpStatus.OK);
        }

        return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/notifications/{id}")
    public List<NotificationResponse> getUserNotifications(@PathVariable("id") UUID userId) {
        User user = userService.findById(userId);
        List<Notification> notifications = notificationService.findAllByUser(user.getId());

        return notifications.stream()
                .map(NotificationResponse::new)
                .toList();
    }

    @GetMapping("/notifications/title/{title}")
    public List<NotificationResponse> getNotificationsByTitle(@PathVariable("title") String title) {
        List<Notification> notifications = notificationService.getNotificationsByTitle(title);

        return notifications.stream()
                .map(NotificationResponse::new)
                .toList();
    }

    @GetMapping("/notification/{id}")
    public NotificationResponse getNotification(@PathVariable("id") UUID id) {
        Notification notification = notificationService.findById(id);

        return new NotificationResponse(notification);
    }

    @DeleteMapping("/notification/{id}")
    public ResponseEntity<Void> removeNotification(@PathVariable("id") UUID id) {
        notificationService.removeNotification(id);
        if (notificationService.exists(id)) {
            return ResponseEntity.status(HttpStatus.NOT_MODIFIED).build();
        }
        logger.info("Notification with id {} deleted!", id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/messages/{user_id}/{friend_id}")
    public List<NotificationMessage> getChatNotificationsBetweenUsers(@PathVariable("user_id") UUID userId, @PathVariable("friend_id") UUID friendId) {
        User user;
        User friend;

        if (!userService.userExists(userId)) {
            user = userService.saveUser(userId);
        } else {
            user = userService.findById(userId);
        }

        if (!userService.userExists(friendId)) {
            friend = userService.saveUser(friendId);
        } else {
            friend = userService.findById(friendId);
        }
        List<Notification> notifications = notificationService.getMessagesByUserAndFriend(user.getId(), friend.getId());

        return notifications.stream()
                .map(NotificationMessage::new)
                .toList();
    }

}
