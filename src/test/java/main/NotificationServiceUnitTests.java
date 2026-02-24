package main;

import project.model.Notification;
import project.model.NotificationType;
import project.model.User;
import project.repository.NotificationRepository;
import project.repository.UserRepository;
import project.service.NotificationService;
import project.event.payloads.CreateNotificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceUnitTests {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void findById_returnsNotification_whenExists() {
        UUID id = UUID.randomUUID();
        Notification n = new Notification();
        n.setId(id);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(n));

        Notification result = notificationService.findById(id);

        assertEquals(id, result.getId());
    }

    @Test
    void findById_throwsException_whenNotExists() {
        UUID id = UUID.randomUUID();

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> notificationService.findById(id));

        assertEquals("Notification does not exist!", ex.getMessage());
    }

    // -------------------------------------------------
    // exists
    // -------------------------------------------------
    @Test
    void exists_returnsTrue_whenFound() {
        UUID id = UUID.randomUUID();

        when(notificationRepository.findById(id)).thenReturn(Optional.of(new Notification()));

        assertTrue(notificationService.exists(id));
    }

    @Test
    void exists_returnsFalse_whenNotFound() {
        UUID id = UUID.randomUUID();

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertFalse(notificationService.exists(id));
    }

    // -------------------------------------------------
    // addNotification
    // -------------------------------------------------
    @Test
    void addNotification_createsSenderAndReceiver_ifMissing() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setTitle("Test");
        req.setMessage("Message");
        req.setLink("/test");
        req.setLinkTitle("Link Title");
        req.setType(NotificationType.INFORMATION);
        req.setSenderId(senderId);
        req.setReceiverId(receiverId);

        when(userRepository.findByUserId(senderId)).thenReturn(Optional.empty());
        when(userRepository.findByUserId(receiverId)).thenReturn(Optional.empty());

        User savedSender = new User();
        savedSender.setUserId(senderId);

        User savedReceiver = new User();
        savedReceiver.setUserId(receiverId);

        when(userRepository.save(any(User.class)))
                .thenReturn(savedSender)
                .thenReturn(savedReceiver);

        Notification savedNotification = new Notification();
        savedNotification.setId(UUID.randomUUID());
        when(notificationRepository.save(any(Notification.class))).thenReturn(savedNotification);

        Notification result = notificationService.addNotification(req);

        assertNotNull(result);
        verify(userRepository, times(2)).save(any(User.class));
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void addNotification_usesExistingSenderAndReceiver() {
        UUID senderId = UUID.randomUUID();
        UUID receiverId = UUID.randomUUID();

        CreateNotificationRequest req = new CreateNotificationRequest();
        req.setTitle("Test");
        req.setMessage("Message");
        req.setLink("link");
        req.setLinkTitle("Link Title");
        req.setType(NotificationType.INFORMATION);
        req.setSenderId(senderId);
        req.setReceiverId(receiverId);

        User existingSender = new User();
        existingSender.setUserId(senderId);

        User existingReceiver = new User();
        existingReceiver.setUserId(receiverId);

        when(userRepository.findByUserId(senderId)).thenReturn(Optional.of(existingSender));
        when(userRepository.findByUserId(receiverId)).thenReturn(Optional.of(existingReceiver));

        Notification saved = new Notification();
        saved.setId(UUID.randomUUID());

        when(notificationRepository.save(any(Notification.class))).thenReturn(saved);

        Notification result = notificationService.addNotification(req);

        assertNotNull(result);
        verify(userRepository, never()).save(any());
        verify(notificationRepository).save(any(Notification.class));
    }

    // -------------------------------------------------
    // setFullLink
    // -------------------------------------------------
    @Test
    void setFullLink_updatesLink_whenTitleIsFriendInvitation() {
        UUID id = UUID.randomUUID();

        Notification n = new Notification();
        n.setId(id);
        n.setTitle("Friend Invitation!");
        n.setLink("/invite");

        when(notificationRepository.findById(id)).thenReturn(Optional.of(n));

        notificationService.setFullLink(id);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());

        assertEquals("/invite/" + id, captor.getValue().getLink());
    }

    @Test
    void setFullLink_doesNothing_ifTitleNotFriendInvitation() {
        UUID id = UUID.randomUUID();

        Notification n = new Notification();
        n.setId(id);
        n.setTitle("Other title");
        n.setLink("/invite");

        when(notificationRepository.findById(id)).thenReturn(Optional.of(n));

        notificationService.setFullLink(id);

        verify(notificationRepository, never()).save(any());
    }

    // -------------------------------------------------
    // findAllByUser
    // -------------------------------------------------
    @Test
    void findAllByUser_returnsNotifications() {
        UUID userId = UUID.randomUUID();

        List<Notification> notifications = List.of(new Notification(), new Notification());

        when(notificationRepository.findAllByReceiverId(userId)).thenReturn(notifications);

        List<Notification> result = notificationService.findAllByUser(userId);

        assertEquals(2, result.size());
    }

    // -------------------------------------------------
    // getNotificationsByTitle
    // -------------------------------------------------
    @Test
    void getNotificationsByTitle_returnsNotifications() {
        String title = "Test";

        when(notificationRepository.findAllByTitle(title))
                .thenReturn(List.of(new Notification(), new Notification(), new Notification()));

        List<Notification> result = notificationService.getNotificationsByTitle(title);

        assertEquals(3, result.size());
    }

    // -------------------------------------------------
    // removeNotification
    // -------------------------------------------------
    @Test
    void removeNotification_deletesNotification() {
        UUID id = UUID.randomUUID();

        Notification n = new Notification();
        n.setId(id);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(n));

        notificationService.removeNotification(id);

        verify(notificationRepository).delete(n);
    }

}
