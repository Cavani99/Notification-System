package main.web;

import main.model.Notification;
import main.model.User;
import main.service.NotificationService;
import main.service.UserService;
import main.web.dto.CreateNotificationRequest;
import main.web.dto.CreateUserRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notification/v1")
public class NotificationController {

    private final NotificationService notificationService;

    private final UserService userService;

    @Autowired
    public NotificationController(NotificationService notificationService, UserService userService) {
        this.notificationService = notificationService;
        this.userService = userService;
    }

    @PostMapping("/user")
    public ResponseEntity<User> createUser(@RequestBody CreateUserRequest createUserRequest) {
        User user = userService.addUser(createUserRequest);

        return new ResponseEntity<>(user, HttpStatus.CREATED);
    }

    @PostMapping("/notification")
    public ResponseEntity<Notification> createNotification(@RequestBody CreateNotificationRequest createNotificationRequest) {
        Notification notification = notificationService.addNotification(createNotificationRequest);

        return new ResponseEntity<>(notification, HttpStatus.CREATED);
    }

}
