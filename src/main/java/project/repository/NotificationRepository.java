package project.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import project.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findAllByReceiverId(UUID userId);

    List<Notification> findAllByTitle(String title);

    @Query("""
                SELECT n FROM Notification n
                WHERE (
                        (n.receiver.id = :user_id AND n.sender.id = :friend_id)
                     OR (n.receiver.id = :friend_id AND n.sender.id = :user_id)
                      )
                  AND n.type = 'MESSAGE'
                ORDER BY n.createdOn ASC
            """)
    List<Notification> getChatMessages(
            @Param("user_id") UUID userId,
            @Param("friend_id") UUID friendId
    );
}
