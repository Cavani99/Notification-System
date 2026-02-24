package main;

import com.fasterxml.jackson.databind.ObjectMapper;
import project.model.Notification;
import project.model.User;
import project.service.NotificationService;
import project.service.UserService;
import project.web.NotificationController;
import project.event.payloads.CreateNotificationRequest;
import project.event.payloads.CreateUserRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(NotificationController.class)
public class NotificationControllerApiTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private NotificationService notificationService;


    @Test
    void createUser_ReturnsCreated() throws Exception {
        CreateUserRequest req = new CreateUserRequest(UUID.randomUUID(), "Ivan");

        User user = new User();
        user.setId(req.getId());

        when(userService.addUser(any())).thenReturn(user);

        mockMvc.perform(
                        post("/notification/v1/user")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .with(csrf())
                                .with(user("testuser").password("pass").roles("USER"))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(req.getId().toString()));
    }

    @Test
    void createNotification_ReturnsCreated() throws Exception {
        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setTitle("Test Title");

        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setTitle("Test Title");

        when(notificationService.addNotification(any())).thenReturn(notification);

        mockMvc.perform(
                        post("/notification/v1/notification")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(req))
                                .with(csrf())
                                .with(user("testuser").password("pass").roles("USER"))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Title"));

        verify(notificationService).setFullLink(notification.getId());
    }

    @Test
    void getUserNotifications_ReturnsList() throws Exception {
        UUID userId = UUID.randomUUID();

        User user = new User();
        user.setId(userId);

        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setTitle("Promo!");

        when(userService.findById(userId)).thenReturn(user);
        when(notificationService.findAllByUser(userId)).thenReturn(List.of(notification));

        mockMvc.perform(get("/notification/v1/notifications/" + userId)
                        .with(csrf())
                        .with(user("testuser").password("pass").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Promo!"));
    }


    @Test
    void getNotificationsByTitle_ReturnsList() throws Exception {
        Notification n = new Notification();
        n.setId(UUID.randomUUID());
        n.setTitle("Sale today!");

        when(notificationService.getNotificationsByTitle("Sale"))
                .thenReturn(List.of(n));

        mockMvc.perform(get("/notification/v1/notifications/title/Sale")
                        .with(csrf())
                        .with(user("testuser").password("pass").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sale today!"));
    }

    @Test
    void getNotification_ReturnsOne() throws Exception {
        UUID id = UUID.randomUUID();

        Notification n = new Notification();
        n.setId(id);
        n.setTitle("Alert!");

        when(notificationService.findById(id)).thenReturn(n);

        mockMvc.perform(get("/notification/v1/notification/" + id)
                        .with(csrf())
                        .with(user("testuser").password("pass").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Alert!"));
    }

    @Test
    void removeNotification_ReturnsOk() throws Exception {
        UUID id = UUID.randomUUID();

        when(notificationService.exists(id)).thenReturn(false);

        mockMvc.perform(delete("/notification/v1/notification/" + id)
                        .with(csrf())
                        .with(user("testuser").password("pass").roles("USER")))
                .andExpect(status().isOk());

        verify(notificationService).removeNotification(id);
    }

    @Test
    void removeNotification_ReturnsNotModified_WhenStillExists() throws Exception {
        UUID id = UUID.randomUUID();

        when(notificationService.exists(id)).thenReturn(true);

        mockMvc.perform(delete("/notification/v1/notification/" + id)
                        .with(csrf())
                        .with(user("testuser").password("pass").roles("USER")))
                .andExpect(status().isNotModified());
    }
}
